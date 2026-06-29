package com.example.pokedex.hardware

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

data class CaptureLocation(
    val latitude: Double,
    val longitude: Double
)

data class CaptureResult(
    val photoPath: String,
    val location: CaptureLocation
)

interface CaptureHardwareController {
    val isCaptureInProgress: Boolean
    fun capture()
}

@Composable
expect fun rememberCaptureHardwareController(
    onCaptureCompleted: (CaptureResult) -> Unit,
    onPermissionDenied: (String) -> Unit,
    onCaptureFailed: (String) -> Unit
): CaptureHardwareController

@Composable
expect fun CapturedPhotoPreview(
    photoPath: String?,
    contentDescription: String,
    modifier: Modifier = Modifier
)
