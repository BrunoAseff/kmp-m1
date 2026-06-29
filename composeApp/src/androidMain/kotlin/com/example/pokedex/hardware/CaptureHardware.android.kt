package com.example.pokedex.hardware

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.os.SystemClock
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Composable
actual fun rememberCaptureHardwareController(
    onCaptureCompleted: (CaptureResult) -> Unit,
    onPermissionDenied: (String) -> Unit,
    onCaptureFailed: (String) -> Unit
): CaptureHardwareController {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isCaptureInProgress by remember { mutableStateOf(false) }
    var pendingCapture by remember { mutableStateOf<PendingCapture?>(null) }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { saved ->
        val pending = pendingCapture
        pendingCapture = null
        isCaptureInProgress = false

        if (saved && pending != null) {
            onCaptureCompleted(
                CaptureResult(
                    photoPath = pending.file.absolutePath,
                    location = pending.location
                )
            )
        } else {
            pending?.file?.delete()
            onCaptureFailed("A captura foi cancelada ou a foto não pôde ser salva.")
        }
    }

    fun beginCapture() {
        scope.launch {
            isCaptureInProgress = true
            try {
                val location = currentLocation(context)
                val imageFile = createCaptureFile(context)
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    imageFile
                )
                pendingCapture = PendingCapture(imageFile, location, uri)
                takePictureLauncher.launch(uri)
            } catch (error: Throwable) {
                isCaptureInProgress = false
                onCaptureFailed(error.message ?: "Não foi possível acessar câmera ou GPS.")
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val cameraGranted = result[Manifest.permission.CAMERA] == true
        val fineLocationGranted = result[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseLocationGranted = result[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (cameraGranted && (fineLocationGranted || coarseLocationGranted)) {
            beginCapture()
        } else {
            isCaptureInProgress = false
            onPermissionDenied("Permita o acesso à câmera e localização para capturar o Pokémon.")
        }
    }

    return object : CaptureHardwareController {
        override val isCaptureInProgress: Boolean
            get() = isCaptureInProgress

        override fun capture() {
            if (isCaptureInProgress) return
            isCaptureInProgress = true

            val cameraGranted = context.hasPermission(Manifest.permission.CAMERA)
            val fineLocationGranted = context.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            val coarseLocationGranted = context.hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)

            if (cameraGranted && (fineLocationGranted || coarseLocationGranted)) {
                beginCapture()
            } else {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }
}

@Composable
actual fun CapturedPhotoPreview(
    photoPath: String?,
    contentDescription: String,
    modifier: Modifier
) {
    val bitmap = remember(photoPath) {
        photoPath?.let { BitmapFactory.decodeFile(it)?.asImageBitmap() }
    }

    Box(
        modifier = modifier
            .heightIn(min = 180.dp)
            .aspectRatio(4f / 3f)
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
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

private data class PendingCapture(
    val file: File,
    val location: CaptureLocation,
    val uri: Uri
)

private fun Context.hasPermission(permission: String): Boolean =
    ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

private fun createCaptureFile(context: Context): File {
    val directory = File(context.filesDir, "captures").apply { mkdirs() }
    return File(directory, "pokemon_capture_${System.currentTimeMillis()}.jpg")
}

@SuppressLint("MissingPermission")
private suspend fun currentLocation(context: Context): CaptureLocation = withContext(Dispatchers.Main) {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val provider = bestEnabledProvider(locationManager)
        ?: throw IllegalStateException("Ative o GPS ou a localização de rede para continuar.")

    val lastKnown = locationManager.getLastKnownLocation(provider)
    if (lastKnown != null && lastKnown.isFreshEnough()) {
        return@withContext lastKnown.toCaptureLocation()
    }

    withTimeout(15_000) {
        suspendCancellableCoroutine { continuation ->
            val listener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    locationManager.removeUpdates(this)
                    if (continuation.isActive) {
                        continuation.resume(location.toCaptureLocation())
                    }
                }

                override fun onProviderDisabled(provider: String) {
                    locationManager.removeUpdates(this)
                    if (continuation.isActive) {
                        continuation.resumeWithException(
                            IllegalStateException("Ative a localização do dispositivo para continuar.")
                        )
                    }
                }

                @Deprecated("Deprecated in Android SDK")
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) = Unit
            }

            locationManager.requestSingleUpdate(provider, listener, Looper.getMainLooper())
            continuation.invokeOnCancellation { locationManager.removeUpdates(listener) }
        }
    }
}

private fun bestEnabledProvider(locationManager: LocationManager): String? =
    locationManager.getBestProvider(
        Criteria().apply { accuracy = Criteria.ACCURACY_FINE },
        true
    ) ?: listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
        .firstOrNull { locationManager.isProviderEnabled(it) }

private fun Location.toCaptureLocation(): CaptureLocation =
    CaptureLocation(latitude = latitude, longitude = longitude)

private fun Location.isFreshEnough(): Boolean {
    val ageMillis = SystemClock.elapsedRealtimeNanos().let { now ->
        (now - elapsedRealtimeNanos) / NANOS_PER_MILLI
    }
    return ageMillis <= MAX_LAST_KNOWN_LOCATION_AGE_MILLIS
}

private const val MAX_LAST_KNOWN_LOCATION_AGE_MILLIS = 2 * 60 * 1000L
private const val NANOS_PER_MILLI = 1_000_000L
