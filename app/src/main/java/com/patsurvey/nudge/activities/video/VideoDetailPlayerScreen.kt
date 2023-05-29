package com.patsurvey.nudge.activities.video

import android.content.pm.ActivityInfo
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.R
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.ui.StyledPlayerView.SHOW_BUFFERING_ALWAYS
import com.patsurvey.nudge.activities.MainActivity
import com.patsurvey.nudge.activities.MainTitle
import com.patsurvey.nudge.activities.ui.theme.*
import com.patsurvey.nudge.database.TrainingVideoEntity
import com.patsurvey.nudge.utils.DownloadStatus
import com.patsurvey.nudge.utils.setScreenOrientation
import com.patsurvey.nudge.utils.videoList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File


@Composable
fun VideoDetailPlayerScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier.fillMaxSize(),
    viewModel: VideDetailPlayerViewModel,
    videoId: Int
) {

    val showFullScreen = remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = true) {
        viewModel.getVideoDetails(videoId)
    }

    val videoItem = viewModel.trainingVideo.collectAsState()

    val context = LocalContext.current

    val screenHeight = LocalConfiguration.current.screenHeightDp

    BackHandler() {
        showFullScreen.value = false
        with(context) {
            setScreenOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        }
        navController.popBackStack()
    }

    Column(modifier = modifier.padding(horizontal = 16.dp)) {

        if (viewModel.showLoader.value) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                CircularProgressIndicator(
                    color = blueDark,
                    modifier = Modifier
                        .size(28.dp)
                        .align(Alignment.Center)
                )
            }
        } else {

            AnimatedVisibility(visible = !showFullScreen.value) {
                MainTitle(
                    videoItem.value.title,
                    Modifier
                        .padding(top = 30.dp)
                )
            }

            if (!viewModel.showLoader.value) {
                VideoPlayer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (showFullScreen.value) screenHeight.dp else 250.dp)
                        .background(white),
                    videoItem = videoItem.value,
                    viewModel = viewModel,
                    showFullScreen = {
                        showFullScreen.value = it
                    }
                )
            }


            AnimatedVisibility(visible = !showFullScreen.value) {
                Text(
                    text = videoItem.value.description,
                    style = TextStyle(
                        color = textColorDark,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = NotoSans,
                    ),
                    modifier = Modifier
                        .align(Alignment.Start)
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                )
            }


            /*Text(
                text = videoItem.value.description,
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
                    .padding(top = 8.dp)
            )*/
        }
    }
}


@Composable
fun VideoPlayer(
    modifier: Modifier = Modifier,
    videoItem: TrainingVideoEntity,
    viewModel: VideDetailPlayerViewModel,
    showFullScreen: (Boolean) -> Unit
) {
    val context = LocalContext.current

    val mediaItems = arrayListOf<MediaItem>()

    val scope = rememberCoroutineScope()

    // create MediaItem
    mediaItems.add(
        MediaItem.Builder()
            .setUri(
                if (videoItem.isDownload == DownloadStatus.DOWNLOADED.value) viewModel.getVideoPath(
                    context,
                    videoItem.id
                ) else videoItem.url
            )
            .setMediaId(videoItem.id.toString())
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
            this.playWhenReady = false
            addListener(
                object : Player.Listener {

                    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                        super.onPlayWhenReadyChanged(playWhenReady, reason)
                        Log.d("VideoDetailPlayerScreen", "onPlayWhenReadyChanged: $playWhenReady")
                    }

                    override fun onIsLoadingChanged(isLoading: Boolean) {
                        super.onIsLoadingChanged(isLoading)
                        Log.d("VideoDetailPlayerScreen", "onIsLoadingChanged: $isLoading")

                    }

                    override fun onEvents(player: Player, events: Player.Events) {
                        super.onEvents(player, events)
                    }

                    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                        super.onMediaItemTransition(mediaItem, reason)

                    }
                }
            )
        }
    }

    ConstraintLayout(modifier = modifier) {
        val (videoPlayer) = createRefs()

        // player view
        DisposableEffect(
            AndroidView(
                modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(0.dp),
                factory = {

                    // exo player view for our video player
                    StyledPlayerView(context).apply {
                        useController = true
                        player = exoPlayer
                        layoutParams =
                            FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams
                                    .MATCH_PARENT,
                                ViewGroup.LayoutParams
                                    .MATCH_PARENT
                            )
                        setShowNextButton(false)
                        setShowPreviousButton(false)
                        setFullscreenButtonClickListener { isFullScreen ->
                            Log.d("VideoDetailPlayerScreen", "isFullScreen: $isFullScreen")
                            scope.launch {
                                showFullScreen(isFullScreen)
                                delay(1000)
                                with(context as MainActivity) {
                                    if (isFullScreen) {
                                        setScreenOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
                                    } else {
                                        setScreenOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                                    }
                                }
                            }

                        }
                        findViewById<View>(R.id.exo_fullscreen).visibility = View.VISIBLE
                        setShowBuffering(SHOW_BUFFERING_ALWAYS)
                        findViewById<View>(R.id.exo_settings).visibility = View.GONE
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


//@Preview(showBackground = true)
//@Composable
//fun VideoDetailPlayerPreview() {
//    VideoDetailPlayerScreen(
//        navController = rememberNavController(),
//        modifier = Modifier.fillMaxSize()
//    )
//}