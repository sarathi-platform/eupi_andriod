package com.patsurvey.nudge.activities.ui.digital_forms

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.patsurvey.nudge.activities.ui.theme.black20
import com.patsurvey.nudge.activities.ui.theme.smallTextStyle
import com.patsurvey.nudge.utils.PREF_FORM_PATH
import com.patsurvey.nudge.utils.uriFromFile
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
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

    val zoomState = rememberZoomState()

    if (uriList.isNotEmpty()) {
        Box(modifier = Modifier.fillMaxSize()) {
            HorizontalPager(pageCount = uriList.size, state = pagerState) {currentItem ->

                val painter = rememberImagePainter(uriList[currentItem])
                painter.state.painter?.intrinsicSize?.let { it1 -> zoomState.setContentSize(it1) }
                Box() {
                    Image(
                        modifier = Modifier
                            .background(Color.Black)
                            .fillMaxSize()
                            .align(Alignment.Center)
                            .zoomable(zoomState = zoomState)
                            .then(modifier),
                        contentScale = ContentScale.FillWidth,
                        painter = painter,
                        contentDescription = "image"
                    )
                }

            }

            IconButton(
                onClick = { navController.popBackStack() },
                Modifier.align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "back button",
                    modifier,
                    tint = Color.White
                )
            }

            Text(
                text = "Page ${pagerState.currentPage + 1}",
                style = smallTextStyle,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                black20,
                                Color.Black
                            ),
                            startY = 20f
                        )
                    )
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(bottom = 16.dp)
                        .fillMaxWidth(),
                ) {
                    val prevButtonVisible = remember {
                        derivedStateOf {
                            pagerState.currentPage > 0
                        }
                    }

                    val nextButtonVisible = remember {
                        derivedStateOf {
                            pagerState.currentPage < 4 // total pages are 5
                        }
                    }

                    /*AnimatedVisibility(
                        visible = prevButtonVisible.value,
                        enter = fadeIn(),
                        exit = fadeOut(),
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Button(
                            enabled = prevButtonVisible.value,
                            onClick = {
                                val prevPageIndex = pagerState.currentPage - 1
                                coroutineScope.launch { pagerState.animateScrollToPage(prevPageIndex) }
                            },
                        ) {
                            Text(text = "<")
                        }
                    }*/


                    /*AnimatedVisibility(
                        visible = nextButtonVisible.value,
                        enter = fadeIn(),
                        exit = fadeOut(),
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Button(
                            onClick = {
                                val nextPageIndex = pagerState.currentPage + 1
                                coroutineScope.launch { pagerState.animateScrollToPage(nextPageIndex) }
                            },
                        ) {
                            Text(text = ">")
                        }
                    }*/
                }

            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
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
            }
        }
    }