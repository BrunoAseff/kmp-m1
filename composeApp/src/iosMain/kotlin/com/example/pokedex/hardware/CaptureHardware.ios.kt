package com.example.pokedex.hardware

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.interop.UIKitView
import androidx.compose.ui.uikit.LocalUIViewController
import androidx.compose.ui.unit.dp
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFoundation.AVAuthorizationStatusAuthorized
import platform.AVFoundation.AVAuthorizationStatusDenied
import platform.AVFoundation.AVAuthorizationStatusRestricted
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVMediaTypeVideo
import platform.CoreLocation.CLAuthorizationStatus
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.Foundation.NSDate
import platform.Foundation.NSFileManager
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerCameraCaptureMode
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerEditedImage
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UIImagePickerControllerSourceTypeCamera
import platform.UIKit.UIImageView
import platform.UIKit.UIViewContentMode
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.UIKit.UIViewController
import platform.darwin.NSObject

@Composable
actual fun rememberCaptureHardwareController(
    onCaptureCompleted: (CaptureResult) -> Unit,
    onPermissionDenied: (String) -> Unit,
    onCaptureFailed: (String) -> Unit
): CaptureHardwareController {
    val presenter = LocalUIViewController.current
    var isCaptureInProgress by remember { mutableStateOf(false) }
    val coordinator = remember {
        IosCaptureCoordinator(
            setCaptureInProgress = { isCaptureInProgress = it },
            onCaptureCompleted = onCaptureCompleted,
            onPermissionDenied = onPermissionDenied,
            onCaptureFailed = onCaptureFailed
        )
    }

    return object : CaptureHardwareController {
        override val isCaptureInProgress: Boolean
            get() = isCaptureInProgress

        override fun capture() {
            if (isCaptureInProgress) return
            isCaptureInProgress = true
            coordinator.capture(presenter)
        }
    }
}

@Composable
actual fun CapturedPhotoPreview(
    photoPath: String?,
    contentDescription: String,
    modifier: Modifier
) {
    Box(
        modifier = modifier
            .heightIn(min = 180.dp)
            .aspectRatio(4f / 3f)
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (photoPath != null) {
            UIKitView(
                factory = {
                    UIImageView().apply {
                        contentMode = UIViewContentMode.UIViewContentModeScaleAspectFill
                        clipsToBounds = true
                        image = UIImage.imageWithContentsOfFile(photoPath)
                    }
                },
                update = { imageView ->
                    imageView.image = UIImage.imageWithContentsOfFile(photoPath)
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(24.dp)
            )
            Text(
                text = "Nenhuma foto capturada",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
            )
        }
    }
}

private class IosCaptureCoordinator(
    private val setCaptureInProgress: (Boolean) -> Unit,
    private val onCaptureCompleted: (CaptureResult) -> Unit,
    private val onPermissionDenied: (String) -> Unit,
    private val onCaptureFailed: (String) -> Unit
) : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol,
    CLLocationManagerDelegateProtocol {

    private val locationManager = CLLocationManager()
    private var currentLocation: CaptureLocation? = null
    private var presenter: UIViewController? = null

    init {
        locationManager.delegate = this
    }

    fun capture(presenter: UIViewController) {
        this.presenter = presenter

        if (!UIImagePickerController.isSourceTypeAvailable(UIImagePickerControllerSourceTypeCamera)) {
            fail("A câmera não está disponível neste dispositivo.")
            return
        }

        when (AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)) {
            AVAuthorizationStatusAuthorized -> requestLocation()
            AVAuthorizationStatusDenied, AVAuthorizationStatusRestricted -> deny()
            else -> AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo) { granted ->
                if (granted) requestLocation() else deny()
            }
        }
    }

    private fun requestLocation() {
        when (CLLocationManager.authorizationStatus()) {
            CLAuthorizationStatus.kCLAuthorizationStatusAuthorizedAlways,
            CLAuthorizationStatus.kCLAuthorizationStatusAuthorizedWhenInUse -> locationManager.requestLocation()
            CLAuthorizationStatus.kCLAuthorizationStatusDenied,
            CLAuthorizationStatus.kCLAuthorizationStatusRestricted -> deny()
            else -> locationManager.requestWhenInUseAuthorization()
        }
    }

    override fun locationManagerDidChangeAuthorization(manager: CLLocationManager) {
        requestLocation()
    }

    override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
        val location = didUpdateLocations.lastOrNull() as? CLLocation
        if (location == null) {
            fail("Não foi possível obter a localização atual.")
            return
        }
        currentLocation = CaptureLocation(
            latitude = location.coordinate.latitude,
            longitude = location.coordinate.longitude
        )
        presentCamera()
    }

    override fun locationManager(manager: CLLocationManager, didFailWithError: platform.Foundation.NSError) {
        fail("Não foi possível obter a localização atual.")
    }

    private fun presentCamera() {
        val presenter = presenter ?: run {
            fail("Não foi possível abrir a câmera.")
            return
        }
        val picker = UIImagePickerController().apply {
            sourceType = UIImagePickerControllerSourceTypeCamera
            cameraCaptureMode = UIImagePickerControllerCameraCaptureMode.UIImagePickerControllerCameraCaptureModePhoto
            allowsEditing = false
            delegate = this@IosCaptureCoordinator
        }
        presenter.presentViewController(picker, animated = true, completion = null)
    }

    override fun imagePickerController(
        picker: UIImagePickerController,
        didFinishPickingMediaWithInfo: Map<Any?, *>
    ) {
        val image = didFinishPickingMediaWithInfo[UIImagePickerControllerEditedImage]
            ?: didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage]
        val uiImage = image as? UIImage
        val location = currentLocation

        if (uiImage == null || location == null) {
            picker.dismissViewControllerAnimated(true, completion = null)
            fail("Não foi possível salvar a captura.")
            return
        }

        val path = saveImage(uiImage)
        picker.dismissViewControllerAnimated(true) {
            setCaptureInProgress(false)
            onCaptureCompleted(CaptureResult(photoPath = path, location = location))
        }
    }

    override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
        picker.dismissViewControllerAnimated(true) {
            fail("A captura foi cancelada.")
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun saveImage(image: UIImage): String {
        val directory = "${NSTemporaryDirectory()}captures"
        NSFileManager.defaultManager.createDirectoryAtPath(
            path = directory,
            withIntermediateDirectories = true,
            attributes = null,
            error = null
        )
        val path = "$directory/pokemon_capture_${NSDate().timeIntervalSince1970}.jpg"
        val data = UIImageJPEGRepresentation(image, 0.88)
            ?: throw IllegalStateException("Não foi possível converter a imagem capturada.")
        data.writeToURL(NSURL.fileURLWithPath(path), atomically = true)
        return path
    }

    private fun deny() {
        setCaptureInProgress(false)
        onPermissionDenied("Permita o acesso à câmera e localização para capturar o Pokémon.")
    }

    private fun fail(message: String) {
        setCaptureInProgress(false)
        onCaptureFailed(message)
    }
}
