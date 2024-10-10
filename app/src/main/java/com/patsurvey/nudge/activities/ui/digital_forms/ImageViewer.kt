package com.patsurvey.nudge.activities.ui.digital_forms

import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.black20
import com.patsurvey.nudge.activities.ui.theme.smallTextStyle
import com.patsurvey.nudge.utils.MAX_IMAGE_FOR_FORM_C_OR_D
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

        for (i in 1..MAX_IMAGE_FOR_FORM_C_OR_D) {
            imageList.value = imageList.value.also {
                Log.d("TAG", "FormImageViewerScreen: ${viewModel.getFormPathKey(viewModel.getFormSubPath(fileName, i))}")
                val imagePath =
                    viewModel.prefRepo.getPref(viewModel.getFormPathKey(viewModel.getFormSubPath(fileName, i)), "")
                Log.d("TAG", "FormImageViewerScreen: Index $i :: ${imagePath} ")
                if (!imagePath.isNullOrEmpty())
                    it.add(imagePath)
            }
        }


        imageList.value.forEach {
            if (it.isNotEmpty()) {
                Log.d("TAG", "FormImageViewerScreen: FileDetails : $it")
                uriList.add(uriFromFile(context, File(it)))
            }
        }
    }

    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f
    ) {
        uriList.size
    }
    val coroutineScope = rememberCoroutineScope()

    BackHandler {
        navController.popBackStack()
    }

    val zoomState = rememberZoomState()

    if (uriList.isNotEmpty()) {
        Box(modifier = Modifier.fillMaxSize()) {
            HorizontalPager(state = pagerState) { currentItem ->
                Log.d("TAG", "FormImageViewerScreenURI: ${uriList[currentItem]} ")
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
                text = "${stringResource(id = R.string.page_name_text)} ${pagerState.currentPage + 1}",
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