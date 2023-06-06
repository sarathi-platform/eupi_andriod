package com.patsurvey.nudge.activities.ui.digital_forms

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.patsurvey.nudge.activities.ui.theme.black20
import com.patsurvey.nudge.activities.ui.theme.smallTextStyle
import com.patsurvey.nudge.utils.PREF_FORM_PATH
import com.patsurvey.nudge.utils.uriFromFile
import java.io.File


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FormImageViewerScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    fileName: String,
    viewModel: ImageViewerViewModel
) {
    val context = LocalContext.current
    val scale = remember { mutableStateOf(1f) }

    val imageList = remember {
        mutableStateOf(mutableListOf<String>())
    }

    val uriList = remember { mutableListOf<Uri>() }

    LaunchedEffect(key1 = true) {

        for (i in 1..5) {
            imageList.value = imageList.value.also {
                val imagePath =
                    viewModel.prefRepo.getPref("${PREF_FORM_PATH}_${fileName}_page_$i", "")
                if (!imagePath.isNullOrEmpty())
                    it.add(imagePath)
            }
        }


        imageList.value.forEach {
            uriList.add(uriFromFile(context, File(it)))
        }
    }

    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    BackHandler {
        navController.popBackStack()
    }

    if (uriList.isNotEmpty()) {

        HorizontalPager(pageCount = uriList.size, state = pagerState, userScrollEnabled = true) {
            Box() {
                Image(
                    modifier = Modifier
                        .background(Color.Black)
                        .fillMaxSize()
                        .align(Alignment.Center)
                        .pointerInput(Unit) {
                            detectTransformGestures { centroid, pan, zoom, rotation ->
                                scale.value *= zoom
                            }
                        }
                        .graphicsLayer(
                            // adding some zoom limits (min 100%, max 200%)
                            scaleX = maxOf(0.5f, minOf(3f, scale.value)),
                            scaleY = maxOf(0.5f, minOf(3f, scale.value)),
                        )
                        .then(modifier),
                    contentScale = ContentScale.FillWidth,
                    painter = rememberImagePainter(uriList[it]),
                    contentDescription = "image"
                )
                IconButton(
                    onClick = { navController.popBackStack() },
                    Modifier.background(black20)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack, contentDescription = "back button",
                        modifier
                            .align(
                                Alignment.TopStart
                            )
                            .padding(10.dp),
                        tint = Color.White
                    )
                }
                Text(text = "Page ${it + 1}", style = smallTextStyle, color = Color.White, modifier=Modifier.align(
                    Alignment.BottomCenter).background(Brush.verticalGradient(
                    listOf(
                        Color.Transparent,
                        Color.Black
                    ),
                    startY = 300f
                )))
            }
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            IconButton(onClick = { navController.popBackStack() }, Modifier.background(black20)) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack, contentDescription = "back button",
                    modifier
                        .align(
                            Alignment.TopStart
                        )
                        .padding(10.dp),
                    tint = Color.White
                )
            }
        }
    }
}