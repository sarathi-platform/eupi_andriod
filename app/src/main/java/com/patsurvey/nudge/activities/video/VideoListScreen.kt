package com.patsurvey.nudge.activities.video

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.MainTitle
import com.patsurvey.nudge.activities.ui.theme.*
import com.patsurvey.nudge.customviews.CircularProgressBarWithIcon
import com.patsurvey.nudge.customviews.SearchWithFilterView
import com.patsurvey.nudge.database.TrainingVideoEntity
import com.patsurvey.nudge.navigation.home.SettingScreens
import com.patsurvey.nudge.utils.debounceClickable
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


    Column(modifier = modifier) {
        MainTitle("Training Videos", Modifier.padding(start = 16.dp, end = 16.dp, top = 30.dp))

        SearchWithFilterView(placeholderString = stringResource(id = R.string.search_didis),
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 16.dp),
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
                        }, videoItem, viewModel
                )
            }
        }


    }

}

@Composable
fun VideoItemCard(
    modifier: Modifier,
    videoItem: TrainingVideoEntity,
    videoListViewModel: VideoListViewModel
) {

    val context = LocalContext.current

    val isDownloaded by remember {
        mutableStateOf(false)
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

            val isDownloaded = videoListViewModel.getVideoPath(
                context,
                videoItem.id
            ).exists()
            Icon(painter = painterResource(
                id = if (isDownloaded) R.drawable.file_download_remove else R.drawable.outline_file_download
            ),
                contentDescription = "download file",
                tint = if (!isDownloaded) GreyDark else Color.Black,
                modifier = Modifier
                    .clickable {
                        videoListViewModel.downloadItem(context, videoItem)
                    }
                    .absolutePadding(top = 4.dp)
            )
        }

        Divider(
            color = borderGreyLight,
            thickness = 1.dp,
            modifier = Modifier.padding(
                vertical = 2.dp,
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
