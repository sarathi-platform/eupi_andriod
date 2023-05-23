package com.patsurvey.nudge.activities.video

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.patsurvey.nudge.activities.MainTitle
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.activities.ui.theme.textColorDark50
import com.patsurvey.nudge.activities.ui.theme.white

private val videoItem = VideoItem(
    id = 1,
    title = "Video 1",
    description = "Introducing Chromecast. The easiest way to enjoy online video and music on your TV. For \$35.  Find out more at google.com/chromecast.",
    url = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
    thumbUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerJoyrides.jpg"
)

@Composable
fun VideoDetailPlayerScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier.fillMaxSize()
) {

    Column(modifier = modifier) {
        MainTitle(videoItem.title, Modifier.padding(start = 16.dp, end = 16.dp, top = 30.dp))

        VideoPlayer(
            modifier =
            Modifier.fillMaxWidth()
                .height(250.dp)
                .background(white)
                .padding(start = 16.dp, end = 16.dp, top = 10.dp)
        )

        Text(
            text = videoItem.description,
            style = TextStyle(
                color = textColorDark,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = NotoSans,
            ),
            modifier = Modifier
                .align(Alignment.Start)
                .fillMaxWidth()
                .padding(start = 16.dp, top = 12.dp)
        )

        Text(
            text = videoItem.description,
            style = TextStyle(
                color = textColorDark50,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = NotoSans
            ),
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.Start)
                .fillMaxWidth()
                .padding(start = 16.dp, top = 8.dp)
        )

    }
}


@Composable
fun VideoPlayer(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val mediaItems = arrayListOf<MediaItem>()

    // create MediaItem
    mediaItems.add(
        MediaItem.Builder()
            .setUri(videoItem.url)
            .setMediaId(videoItem.url)
            .setTag(videoItem)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setDisplayTitle(videoItem.title)
                    .build()
            )
            .build()
    )

    // create our player
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            this.setMediaItems(mediaItems)
            this.prepare()
            this.playWhenReady = true
        }
    }

    ConstraintLayout(modifier = modifier) {
        val (videoPlayer) = createRefs()

        // player view
        DisposableEffect(
            AndroidView(
                modifier =
                Modifier.testTag("VideoPlayer")
                    .constrainAs(videoPlayer) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    },
                factory = {

                    // exo player view for our video player
                    StyledPlayerView(context).apply {
//                        useController = false
                        player = exoPlayer
                        layoutParams =
                            FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams
                                    .MATCH_PARENT,
                                ViewGroup.LayoutParams
                                    .MATCH_PARENT
                            )
                    }
                }
            )
        ) {
            onDispose {
                // release player when no longer needed
                exoPlayer.release()
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun VideoDetailPlayerPreview() {
    VideoDetailPlayerScreen(
        navController = rememberNavController(),
        modifier = Modifier.fillMaxSize()
    )
}