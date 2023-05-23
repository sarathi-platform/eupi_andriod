package com.patsurvey.nudge.activities.video

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.MainTitle
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.customviews.SearchWithFilterView

private val videoList = listOf(
    VideoItem(
        id = 1,
        title = "Video 1",
        description = "Introducing Chromecast. The easiest way to enjoy online video and music on your TV. For \$35.  Find out more at google.com/chromecast.",
        url = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
        thumbUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerJoyrides.jpg"
    ),
    VideoItem(
        id = 2,
        title = "Video 2",
        description = "Supporting description",
        url = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
        thumbUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerJoyrides.jpg"
    ),
    VideoItem(
        id = 3,
        title = "Video 3",
        description = "Supporting description",
        url = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
        thumbUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerJoyrides.jpg"
    ),
    VideoItem(
        id = 4,
        title = "Video 4",
        description = "Supporting description",
        url = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
        thumbUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerJoyrides.jpg"
    )

)

@Composable
fun VideoListScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier.fillMaxSize()
) {

    var filterSelected by remember {
        mutableStateOf(false)
    }

    Column(modifier = modifier) {
        MainTitle("Training Videos", Modifier.padding(start = 16.dp, end = 16.dp, top = 30.dp))

        SearchWithFilterView(placeholderString = stringResource(id = R.string.search_didis),
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 16.dp)
            ,
            showFilter = false,
            filterSelected = filterSelected,
            onFilterSelected = {

            }, onSearchValueChange = {
//                didiViewModel.performQuery(it, filterSelected)

            }
        )

        Text(text = "Showing ${videoList.size} Videos",
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
            contentPadding = PaddingValues(bottom = 10.dp, start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(
                videoList
            ) { index, videoItem ->
                VideoItemCard(Modifier, videoItem)
            }
        }


    }

}

@Composable
fun VideoItemCard(modifier: Modifier, videoItem: VideoItem) {
    Row(
        modifier = Modifier.fillMaxWidth().then(modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberImagePainter(videoItem.thumbUrl),
            contentDescription = null,
            modifier = Modifier
                .width(135.dp)
                .height(112.dp),
            contentScale = ContentScale.Inside
        )

        Column() {

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
                    .align(Alignment.Start)
                    .fillMaxWidth()
                    .padding(start = 12.dp, top = 8.dp)
            )

            Text(
                text = videoItem.description,
                style = TextStyle(
                    color = textColorDark,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = NotoSans
                ),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.Start)
                    .fillMaxWidth()
                    .padding(start = 12.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VideoListPreview() {
    VideoListScreen(
        navController = rememberNavController(),
        modifier = Modifier.fillMaxSize()
    )
}