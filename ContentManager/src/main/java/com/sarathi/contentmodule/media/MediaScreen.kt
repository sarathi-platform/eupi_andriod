package com.sarathi.contentmodule.media

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.github.barteksc.pdfviewer.PDFView
import com.sarathi.contentmodule.download_manager.FileType
import java.io.File

@Composable
fun MediaScreen(
    fileType: String, key: String, navController: NavController = rememberNavController(),
    viewModel: MediaScreenViewModel
) {
    LaunchedEffect(key1 = true) {
        viewModel.initData(key)
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (viewModel.contentUrl.value.isNotEmpty()) {
            val filePathUri = viewModel.getFilePathUri(viewModel.contentUrl.value)
            filePathUri?.let {
                Column {
                    when (fileType) {
                        FileType.Video.name,
                        FileType.Audio.name -> {
                            VideoPlayer(uri = filePathUri)
                        }

                        FileType.Image.name -> {
                            ImageViewer(uri = filePathUri)
                        }

                        FileType.File.name -> {
                            viewModel.getFilePath(viewModel.contentUrl.value)
                                ?.let { it1 -> PdfViewer(it1) }
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun VideoPlayer(uri: Uri) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(uri)
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
        }
    }

    DisposableEffect(
        AndroidView(factory = {
            PlayerView(context).apply {
                player = exoPlayer
            }
        })
    ) {
        onDispose {
            exoPlayer.release()
        }
    }
}

@Composable
fun ImageViewer(uri: Uri) {
    val imagePainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(uri)
            .crossfade(true)
            .build()
    )
    Image(
        painter = imagePainter,
        contentDescription = "Loaded Image",
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp)
    )
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
