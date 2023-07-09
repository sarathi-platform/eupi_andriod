package com.patsurvey.nudge.activities

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.patsurvey.nudge.activities.ui.theme.white
import com.patsurvey.nudge.database.DidiEntity
import java.io.File
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
fun CameraView(
    modifier: Modifier = Modifier,
    outputDirectory: File,
    viewModel: PatDidiSummaryViewModel,
    didiEntity: DidiEntity,
    executor: Executor,
    onImageCaptured: (Uri, String) -> Unit,
    onCloseButtonClicked: () -> Unit,
    onError: (ImageCaptureException) -> Unit
) {
    val lensFacing = CameraSelector.LENS_FACING_BACK
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val screenHeight = LocalConfiguration.current.screenHeightDp

    val preview = Preview.Builder().build()
    val previewView = remember { PreviewView(context) }
    previewView.scaleType = PreviewView.ScaleType.FILL_CENTER
    val imageCapture: ImageCapture = remember { ImageCapture.Builder().build() }
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing)
        .build()

    LaunchedEffect(key1 = Unit) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture
        )
        preview.setSurfaceProvider(previewView.surfaceProvider)
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .then(modifier)) {
        AndroidView({ previewView }, modifier = Modifier.fillMaxSize())

        Box(
            Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .height((screenHeight / 4).dp)
                .background(Color.Black.copy(alpha = 0.5f))
        )

        Box(
            Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .height((screenHeight / 4).dp)
                .background(Color.Black.copy(alpha = 0.5f))
        )

        IconButton(
            onClick = {
                onCloseButtonClicked()
            }, modifier = Modifier
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "close camera",
                tint = white,
            )
        }

        Box(
            modifier = Modifier
                .padding(bottom = 20.dp)
                .clip(CircleShape)
                .background(Color.Transparent, shape = CircleShape)
                .size(size = 50.dp)
                .align(Alignment.BottomCenter)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(
                        bounded = true,
                        color = Color.Black
                    )
                ) {
                    takePhoto(
                        context = context,
                        viewModel = viewModel,
                        fileNameFormat = "yyyy-MM-dd-HH-mm-ss-SSS",
                        didiEntity = didiEntity,
                        imageCapture = imageCapture,
                        outputDirectory = outputDirectory,
                        executor = executor,
                        onImageCaptured = onImageCaptured,
                        onError = onError
                    )
                },
        ) {
            Canvas(
                modifier = Modifier
                    .size(size = 50.dp)
                    .border(
                        width = 2.dp,
                        color = white,
                        shape = CircleShape
                    )
                    .padding(5.dp)

            ) {
                drawCircle(
                    color = Color.White,
                )
            }
        }

    }
}

fun takePhoto(
    fileNameFormat: String,
    context: Context,
    viewModel: PatDidiSummaryViewModel,
    didiEntity: DidiEntity,
    imageCapture: ImageCapture,
    outputDirectory: File,
    executor: Executor,
    onImageCaptured: (Uri, String) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {
    val photoFile = File(
        outputDirectory/*viewModel.getOutputDirectory(context as MainActivity)*/,
        "${didiEntity.id}-${didiEntity.cohortId}-${didiEntity.villageId}_${System.currentTimeMillis()}.jpg"
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(context.applicationContext), object : ImageCapture.OnImageSavedCallback {
        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
            val saveUri = Uri.fromFile(photoFile)
            Log.d("CameraView", "Take Photo path: ${photoFile.absoluteFile}")
            onImageCaptured(saveUri, photoFile.absolutePath)
        }

        override fun onError(exception: ImageCaptureException) {
            Log.e("CameraView", "Take Photo error: ", exception)
            onError(exception)
        }

    })
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { cameraProvider ->
            cameraProvider.addListener({
                continuation.resume(cameraProvider.get())
            }, ContextCompat.getMainExecutor(this))
        }

    }

@Composable
fun CameraViewForForm(
    modifier: Modifier = Modifier,
    outputDirectory: File,
    formName: String,
    executor: Executor,
    onImageCaptured: (Uri, String) -> Unit,
    onCloseButtonClicked: () -> Unit,
    onError: (ImageCaptureException) -> Unit
) {
    val lensFacing = CameraSelector.LENS_FACING_BACK
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val preview = Preview.Builder().build()
    val previewView = remember { PreviewView(context) }
    val imageCapture: ImageCapture = remember { ImageCapture.Builder().build() }
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing)
        .build()

    LaunchedEffect(key1 = lensFacing) {
        val cameraProvider = context.getCameraProviderForForm()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture
        )
        preview.setSurfaceProvider(previewView.surfaceProvider)
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .then(modifier)) {
        AndroidView({ previewView }, modifier = Modifier.fillMaxSize())

        IconButton(onClick = { onCloseButtonClicked() }) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "close camera",
                tint = white,
                modifier = Modifier
                    .align(
                        Alignment.TopStart
                    )
                    .padding(start = 4.dp, top = 4.dp)
            )
        }

        Box(
            modifier = Modifier
                .padding(bottom = 20.dp)
                .clip(CircleShape)
                .background(Color.Transparent, shape = CircleShape)
                .size(size = 50.dp)
                .align(Alignment.BottomCenter)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(
                        bounded = true,
                        color = Color.Black
                    )
                ) {
                    Log.d("FormPictureScreen_CameraViewForForm", "formName: $formName")
                    takeFormPhoto(
                        formName = formName,
                        imageCapture = imageCapture,
                        outputDirectory = outputDirectory,
                        executor = executor,
                        onImageCaptured = onImageCaptured,
                        onError = onError
                    )
                },
        ) {
            Canvas(
                modifier = Modifier
                    .size(size = 50.dp)
                    .border(
                        width = 2.dp,
                        color = white,
                        shape = CircleShape
                    )
                    .padding(5.dp)

            ) {
                drawCircle(
                    color = Color.White,
                )
            }
        }

    }
}

fun takeFormPhoto(
    formName: String,
    imageCapture: ImageCapture,
    outputDirectory: File,
    executor: Executor,
    onImageCaptured: (Uri, String) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {
    val photoFile = File(
        outputDirectory,
        "$formName.jpg"
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(outputOptions, executor, object : ImageCapture.OnImageSavedCallback {
        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
            val saveUri = Uri.fromFile(photoFile)
            Log.d("FormPictureScreen_CameraViewForForm", "Take Photo path: ${photoFile.absoluteFile}")
            Log.d("CameraView", "Take Photo path: ${photoFile.absoluteFile}")
            onImageCaptured(saveUri, photoFile.absolutePath)
        }

        override fun onError(exception: ImageCaptureException) {
            Log.e("CameraView", "Take Photo error: ", exception)
            onError(exception)
        }

    })
}

private suspend fun Context.getCameraProviderForForm(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { cameraProvider ->
            cameraProvider.addListener({
                continuation.resume(cameraProvider.get())
            }, ContextCompat.getMainExecutor(this))
        }

    }