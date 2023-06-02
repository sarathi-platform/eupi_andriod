package com.patsurvey.nudge.activities.video

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.MainActivity
import com.patsurvey.nudge.activities.ui.socialmapping.ShowDialog
import com.patsurvey.nudge.activities.ui.theme.*
import com.patsurvey.nudge.customviews.CircularProgressBarWithOutText
import com.patsurvey.nudge.customviews.SearchWithFilterView
import com.patsurvey.nudge.database.TrainingVideoEntity
import com.patsurvey.nudge.utils.DownloadStatus
import com.patsurvey.nudge.utils.debounceClickable
import com.patsurvey.nudge.utils.showToast
import com.patsurvey.nudge.utils.videoList


@Composable
fun VideoListScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier.fillMaxSize(),
    viewModel: VideoListViewModel
) {

    var filterSelected by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = true) {
        viewModel.getVideoList()
    }

    val trainingVideos = viewModel.filterdList

    val downloadStatus = viewModel.downloadStauts.collectAsState()


    Scaffold(modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Training Videos",
                        color = textColorDark,
                        modifier = Modifier
                            .fillMaxWidth(),
                        textAlign = TextAlign.Start,
                        style = largeTextStyle
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, null, tint = textColorDark)
                    }
                },
                backgroundColor = Color.White
            )
        }) {

        Column() {

            SearchWithFilterView(placeholderString = stringResource(id = R.string.search_video),
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = it.calculateTopPadding()+20.dp),
                showFilter = false,
                filterSelected = filterSelected,
                onFilterSelected = {

                }, onSearchValueChange = {
                    viewModel.performQuery(it)
                }
            )

            Text(
                text = "Showing ${videoList.size} Videos",
                style = TextStyle(
                    color = textColorDark,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = NotoSans
                ),
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 16.dp, end = 16.dp, top = 20.dp)
            )

            LazyColumn(
                contentPadding = PaddingValues(bottom = 10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                itemsIndexed(
                    trainingVideos
                ) { index, videoItem ->
                    VideoItemCard(
                        Modifier
                            .debounceClickable {
                                navController.navigate("video_player_screen/${videoItem.id}")
                            }, videoItem, viewModel,
                        mDownloadStatus = downloadStatus.value[videoItem.id]
                            ?: DownloadStatus.UNAVAILABLE
                    )
                }
            }
        }
    }
}

@Composable
fun VideoItemCard(
    modifier: Modifier,
    videoItem: TrainingVideoEntity,
    videoListViewModel: VideoListViewModel,
    mDownloadStatus: DownloadStatus
) {

    val context = LocalContext.current

    val isDownloaded by remember {
        mutableStateOf(videoItem.isDownload)
    }


    val showDialog = remember { mutableStateOf(false) }

    if (showDialog.value) {
        ShowDialog(title = "Are you sure?", message = "You want to delete ${videoItem.title}", setShowDialog ={showDialog.value = it}) {
            videoListViewModel.removeDownload(context, videoItem)

        }
    }
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .then(modifier),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = videoItem.thumbUrl),
                contentDescription = null,
                modifier = Modifier
                    .width(100.dp)
                    .height(75.dp)
                    .clip(RoundedCornerShape(6.dp)),
                contentScale = ContentScale.Inside
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
            ) {
                Text(
                    text = videoItem.title,
                    style = TextStyle(
                        color = textColorDark,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = NotoSans,
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                )

                Text(
                    text = videoItem.description,
                    style = TextStyle(
                        color = textColorDark,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = NotoSans
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }

            when (mDownloadStatus) {
                DownloadStatus.DOWNLOAD_PAUSED -> {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            color = blueDark,
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.Center),
                            strokeWidth = 2.dp
                        )
                    }
                }
                DownloadStatus.DOWNLOADING -> {
                    CircularProgressBarWithOutText(
                        modifier = Modifier.size(28.dp),
                        circleRadius = 28f,
                        initialPosition = videoListViewModel.initialPosition.value,
                        borderThickness = 7.dp
                    )
                }
                DownloadStatus.DOWNLOADED -> {
                    Icon(painter = painterResource(id = R.drawable.file_download_remove),
                        contentDescription = "download file",
                        tint = Color.Black,
                        modifier = Modifier
                            .clickable {
                                showDialog.value = true
                            }
                            .absolutePadding(top = 4.dp)
                    )
                }
                else -> {
                    Icon(painter = painterResource(R.drawable.outline_file_download),
                        contentDescription = "download file",
                        tint = GreyDark,
                        modifier = Modifier
                            .absolutePadding(top = 4.dp)
                            .clickable {
                                if ((context as MainActivity).isOnline.value) {
                                    videoListViewModel.downloadItem(context, videoItem)
                                } else {
                                    showToast(
                                        context,
                                        "You are offline, unable to download videos."
                                    )
                                }
                            }
                    )
                }
            }
        }

        Divider(
            color = borderGreyLight,
            thickness = 1.dp,
            modifier = Modifier.padding(
                vertical = 8.dp,
                horizontal = 16.dp
            )
        )
    }
}

/*
@Preview(showBackground = true)
@Composable
fun VideoListPreview() {
    VideoListScreen(
        navController = rememberNavController(),
        modifier = Modifier.fillMaxSize()
    )
}*/
