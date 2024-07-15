package com.sarathi.contentmodule.media

import android.content.Intent
import android.os.Environment
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.github.barteksc.pdfviewer.PDFView
import com.nudge.core.BLANK_STRING
import com.nudge.core.model.CoreAppDetails
import com.nudge.core.ui.theme.mediumTextStyle
import com.nudge.core.ui.theme.pdfViewerBg
import com.nudge.core.ui.theme.textColorDark
import com.nudge.core.uriFromFile
import java.io.File

@Composable
fun PdfViewer(
    modifier: Modifier = Modifier,
    navController: NavController,
    filePath: String
) {

    val context = LocalContext.current

    val pdfFile = File(
        "${
            context.getExternalFilesDir(
                Environment.DIRECTORY_DOCUMENTS
            )?.absolutePath
        }", filePath
    )

    val nonUpdatedUri = uriFromFile(
        context, pdfFile,
        CoreAppDetails.getApplicationDetails()?.applicationID ?: BLANK_STRING
    )
    val pdfView = remember { PDFView(context, null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = filePath,
                        color = textColorDark,
                        modifier = Modifier
                            .fillMaxWidth(),
                        textAlign = TextAlign.Start,
                        style = mediumTextStyle
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, "Back Action", tint = textColorDark)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val fileUri = uriFromFile(
                            context,
                            pdfFile,
                            CoreAppDetails.getApplicationDetails()?.applicationID ?: BLANK_STRING
                        )
                        val shareIntent = Intent(Intent.ACTION_SEND)
                        shareIntent.type = "application/pdf"
                        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri)
                        ContextCompat.startActivity(
                            context,
                            Intent.createChooser(shareIntent, "Share Form E"),
                            null
                        )
                    }) {
                        Icon(Icons.Filled.Share, "Menu", tint = textColorDark)
                    }
                },
                backgroundColor = Color.White,
            )
        },
        backgroundColor = pdfViewerBg
    ) {
        BoxWithConstraints(
            modifier = modifier
                .fillMaxWidth()
                .background(pdfViewerBg)
                .padding(it)
                .padding(horizontal = 10.dp)
        ) {

            AndroidView(
                factory = { _ ->
                    pdfView.fromUri(nonUpdatedUri)
                        .enableDoubletap(true)
                        .enableSwipe(true)
                        .defaultPage(0)
                        .enableAnnotationRendering(false)
                        .onPageChange { _, _ ->
                            // Handle page change if needed
                        }
                        .onLoad { _ ->
                            // Handle load event if needed
                        }
                        .load()

                    pdfView
                },
                update = {

                },
                modifier = Modifier
                    .fillMaxSize()
                    .background(pdfViewerBg)
            )
        }
    }
    @Composable
    fun ZoomableImage(
        request: ImageRequest,
        modifier: Modifier
    ) {
        val scale = remember { mutableStateOf(1f) }
        val rotationState = remember { mutableStateOf(1f) }
        Box(
            modifier = Modifier
                .clip(RectangleShape) // Clip the box content
                .fillMaxSize() // Give the size you want...
                .pointerInput(Unit) {
                    detectTransformGestures { _, _, zoom, _ ->
                        scale.value *= zoom
                    }
                }
                .then(modifier)
        ) {
            Image(
                modifier = Modifier
                    .align(Alignment.Center) // keep the image centralized into the Box
                    .graphicsLayer(
                        // adding some zoom limits (min 50%, max 200%)
                        scaleX = maxOf(.5f, minOf(3f, scale.value)),
                        scaleY = maxOf(.5f, minOf(3f, scale.value)),
                        rotationZ = rotationState.value
                    ),
                contentDescription = null,
                painter = rememberAsyncImagePainter(request)
            )
        }
    }
}
