package com.patsurvey.nudge.activities.ui.digital_forms

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import coil.imageLoader
import coil.memory.MemoryCache
import coil.request.ImageRequest
import com.patsurvey.nudge.activities.ui.theme.mediumTextStyle
import com.patsurvey.nudge.activities.ui.theme.pdfViewerBg
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.utils.uriFromFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import java.io.File
import kotlin.math.sqrt

@Composable
fun PdfViewer(
    modifier: Modifier = Modifier,
    navController: NavController,
    filePath: String,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(8.dp)
) {

    val context = LocalContext.current

    val pdfFile = File(
        "${
            context.getExternalFilesDir(
                Environment.DIRECTORY_DOCUMENTS
            )?.absolutePath
        }", filePath
    )

    val nonUpdatedUri = uriFromFile(context, pdfFile)
    val uri = Uri.fromFile(nonUpdatedUri.path?.replace("content:", "file:")
        ?.let { File(it) })
    val rendererScope = rememberCoroutineScope()
    val mutex = remember { Mutex() }
    val renderer by produceState<PdfRenderer?>(null, uri) {
        rendererScope.launch(Dispatchers.IO) {
            val input = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
            value = PdfRenderer(input)
        }
        awaitDispose {
            val currentRenderer = value
            rendererScope.launch(Dispatchers.IO) {
                mutex.withLock {
                    currentRenderer?.close()
                }
            }
        }
    }
    val imageLoader = LocalContext.current.imageLoader
    val imageLoadingScope = rememberCoroutineScope()
    val zoomState = rememberZoomState()

    val lazyScrollState = rememberLazyListState()
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
                        val fileUri = uriFromFile(context, pdfFile)
                        val shareIntent = Intent(Intent.ACTION_SEND)
                        shareIntent.type = "application/pdf"
                        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri)
                        ContextCompat.startActivity(
                            context,
                            Intent.createChooser(shareIntent, "Share Form A"),
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
                .zoomable(zoomState)
                .background(pdfViewerBg)
                .padding(it)
                .padding(horizontal = 10.dp)
        )
        {
            val width = with(LocalDensity.current) { maxWidth.toPx() }.toInt()
            val height = (width * sqrt(2f)).toInt()
            val pageCount by remember(renderer) { derivedStateOf { renderer?.pageCount ?: 0 } }
            LazyColumn(
                verticalArrangement = verticalArrangement,
                state = lazyScrollState
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }
                items(
                    count = pageCount,
                    key = { index -> "$uri-$index" }
                ) { index ->
                    val cacheKey = MemoryCache.Key("$uri-$index")
                    var bitmap by remember { mutableStateOf(imageLoader.memoryCache?.get(cacheKey) as? Bitmap?) }
                    if (bitmap == null) {
                        DisposableEffect(uri, index) {
                            val job = imageLoadingScope.launch(Dispatchers.IO) {
                                val destinationBitmap =
                                    Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                                mutex.withLock {
                                    Log.d("PdfViewer", "Loading PDF $uri - page $index/$pageCount")
                                    if (!coroutineContext.isActive) return@launch
                                    try {
                                        renderer?.let {
                                            it.openPage(index).use { page ->
                                                page.render(
                                                    destinationBitmap,
                                                    null,
                                                    null,
                                                    PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                                                )
                                            }
                                        }
                                    } catch (e: Exception) {
                                        //Just catch and return in case the renderer is being closed
                                        return@launch
                                    }
                                }
                                bitmap = destinationBitmap
                            }
                            onDispose {
                                job.cancel()
                            }
                        }
                        Box(
                            modifier = Modifier
                                .background(pdfViewerBg)
                                .aspectRatio(1f / sqrt(2f))
                                .fillMaxWidth()
                        )
                    } else {
                        val request = ImageRequest.Builder(context)
                            .size(width, height)
                            .memoryCacheKey(cacheKey)
                            .data(bitmap)
                            .build()
                        val painter = rememberImagePainter(request)
                        painter.state.painter?.intrinsicSize?.let { it1 ->
                            zoomState.setContentSize(
                                it1
                            )
                        }
                        Card(
                            elevation = 4.dp,
                            contentColor = Color.Transparent,
                            shape = RectangleShape
                        ) {
                            Image(
                                modifier = Modifier
                                    .background(Color.White)
                                    .aspectRatio(sqrt(2f) / 1f)
                                    .fillMaxWidth(),
                                contentScale = ContentScale.FillBounds,
                                painter = painter,
                                contentDescription = "Page ${index + 1} of $pageCount"
                            )
                        }
//                    ZoomableImage(request = request, modifier = Modifier)
                    }
                }
                item { Spacer(modifier = Modifier.height(8.dp)) }
            }
        }
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
                detectTransformGestures { centroid, pan, zoom, rotation ->
                    scale.value *= zoom
//                    rotationState.value += rotation
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
            painter = rememberImagePainter(request)
        )
    }
}