package com.sarathi.contentmodule.media

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.github.barteksc.pdfviewer.PDFView
import com.nudge.core.ARG_IS_FROM_BACKSTACK
import com.nudge.core.ui.theme.defaultTextStyle
import com.nudge.core.ui.theme.dimen_0_dp
import com.nudge.core.ui.theme.dimen_15_dp
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_20_dp
import com.nudge.core.ui.theme.textColorDark
import com.sarathi.contentmodule.ui.component.MediaToolbarComponent
import com.sarathi.dataloadingmangement.download_manager.FileType
import java.io.File
import java.util.Locale

@Composable
fun MediaScreen(
    fileType: String, key: String, navController: NavController = rememberNavController(),
    viewModel: MediaScreenViewModel, contentTitle: String,
) {
    val activity = getActivity()
    var isToolbarVisible = remember { mutableStateOf(true) }
    LaunchedEffect(key1 = true) {
        viewModel.initData(key)
    }
    BackHandler {
        navController.previousBackStackEntry?.savedStateHandle?.set(
            ARG_IS_FROM_BACKSTACK, true
        )
        navController.popBackStack()
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        Scaffold(
            topBar = {

                    AnimatedVisibility(visible = isToolbarVisible.value, enter = fadeIn()+ expandVertically(), exit = fadeOut()+ shrinkVertically()) {
                    MediaToolbarComponent(
                        title = contentTitle,
                        modifier = Modifier,
                        onDownloadClick = {

                        },
                        onBackIconClick = {
                            if (activity != null) {
                                activity.requestedOrientation =
                                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                            }
                            navController.previousBackStackEntry?.savedStateHandle?.set(
                                ARG_IS_FROM_BACKSTACK, true
                            )
                            navController.popBackStack()
                        }
                    )
                }
            }
        ) { paddingValues ->
            if (!viewModel.contentUrl.value.isNullOrEmpty()) {
                if(fileType.uppercase(Locale.getDefault()) == FileType.TEXT.name){
                    TextViewer(viewModel.contentUrl.value,
                        Modifier
                            .clickable { isToolbarVisible.value = !isToolbarVisible.value }
                            .padding(
                                vertical = paddingValues.calculateTopPadding() + if (isToolbarVisible.value) dimen_20_dp else dimen_15_dp,
                                horizontal = dimen_15_dp
                            ))
                }
                val filePathUri = viewModel.getFilePathUri(viewModel.contentUrl.value)
                filePathUri?.let {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = paddingValues.calculateTopPadding() + if (isToolbarVisible.value) dimen_20_dp else dimen_0_dp)
                            .clickable { isToolbarVisible.value = !isToolbarVisible.value },
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        when (fileType.uppercase(Locale.getDefault())) {
                            FileType.VIDEO.name,
                            FileType.AUDIO.name -> {
                                VideoPlayer(uri = filePathUri,
                                    onPlayerViewClick = { isToolbarVisible.value = !isToolbarVisible.value }
                                )
                            }

                            FileType.IMAGE.name -> {
                                ZoomableImage(uri = filePathUri)
                            }

                            FileType.FILE.name -> {
                                viewModel.getFilePath(viewModel.contentUrl.value)
                                    ?.let { it1 -> PdfViewer(it1) }
                            }
                        }
                    }
                }
            }
        }

    }
    BackHandler {
        if (activity != null) {
            if (activity.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }
        navController.popBackStack()
    }

}

@Composable
fun VideoPlayer(uri: Uri, onPlayerViewClick: () -> Unit) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(uri)
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
        }
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(factory = {
            PlayerView(context).apply {
                player = exoPlayer
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                setOnClickListener {
                    onPlayerViewClick()
                    exoPlayer.playWhenReady = !exoPlayer.playWhenReady
                }

            }
        })
    }
}

@Composable
fun ZoomableImage(uri: Uri) {
    val scale = remember { mutableStateOf(1f) }
    val imagePainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(uri)
            .crossfade(true)
            .build()
    )
    Box(
        modifier = Modifier
            .clip(RectangleShape) // Clip the box content
            .fillMaxSize() // Give the size you want...
            .pointerInput(Unit) {
                detectTransformGestures { centroid, pan, zoom, rotation ->
                    scale.value *= zoom
                }
            }
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimen_16_dp)
                .align(Alignment.Center) // keep the image centralized into the Box
                .graphicsLayer(
                    // adding some zoom limits (min 50%, max 200%)
                    scaleX = maxOf(.5f, minOf(3f, scale.value)),
                    scaleY = maxOf(.5f, minOf(3f, scale.value)),
                ),
            contentDescription = null,
            painter = imagePainter,

            )
    }
}

@Composable
fun PdfViewer(pdfFile: File) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            PDFView(context, null).apply {
                fromFile(pdfFile)
                    .defaultPage(0)
                    .enableSwipe(true)
                    .swipeHorizontal(false)
                    .enableDoubletap(true)
                    .load()
            }
        }
    )
}

@Composable
fun getActivity(): Activity? {
    val context = LocalContext.current
    return remember(context) {
        context as? Activity
    }
}

@Composable
fun TextViewer(contentValue: String, modifier: Modifier) {
    Text(text = contentValue, modifier = modifier.fillMaxSize(), style = defaultTextStyle, color = textColorDark)
}
