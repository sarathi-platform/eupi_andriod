package com.patsurvey.nudge.activities

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import coil.compose.rememberImagePainter

@Composable
fun PatImagePreviewScreen(
    modifier: Modifier = Modifier,
    viewModal: PatImagePreviewViewModal
) {

    val localContext = LocalContext.current

    LaunchedEffect(key1 = localContext) {
        viewModal.setUpOutputDirectory(localContext as MainActivity)
        requestCameraPermission(localContext as Activity, viewModal)
    }

    if (viewModal.shouldShowCamera.value) {
        CameraView(
            outputDirectory = viewModal.outputDirectory,
            executor = viewModal.cameraExecutor,
            onImageCaptured = {
                handleImageCapture(it, viewModal)
            },
            onError = { Log.e("PatImagePreviewScreen", "View error:", it) }
        )
    }

    if (viewModal.shouldShowPhoto.value) {
        Image(painter = rememberImagePainter(viewModal.photoUri),
            contentDescription = null,
            modifier = Modifier.size(width = 300.dp, height = 180.dp))
    }

}

fun handleImageCapture(uri: Uri, viewModal: PatImagePreviewViewModal) {
    viewModal.shouldShowCamera.value = false
    viewModal.photoUri = uri
    viewModal.shouldShowPhoto.value = true
    viewModal.cameraExecutor.shutdown()
}

fun requestCameraPermission(context: Activity, viewModal: PatImagePreviewViewModal) {
    when {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED -> {
            Log.i("PatImagePreviewScreen", "Permission previously granted")
            viewModal.shouldShowCamera.value = true
        }
        ActivityCompat.shouldShowRequestPermissionRationale(
            context,
            Manifest.permission.CAMERA
        ) -> {
            Log.i("PatImagePreviewScreen", "Show camera permissions dialog")
            viewModal.shouldShowCamera.value = true
        }

        else -> viewModal.shouldShowCamera.value = false
    }
}