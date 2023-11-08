package com.nrlm.baselinesurvey.ui.video_player.presentation

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.activity.OnBackPressedCallback
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.navigation.NavHostController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.utils.Media
import com.nrlm.baselinesurvey.utils.MediaState
import com.nrlm.baselinesurvey.utils.ShowBuffering
import com.nrlm.baselinesurvey.utils.SimpleController
import com.nrlm.baselinesurvey.utils.findActivity
import com.nrlm.baselinesurvey.utils.rememberManagedPlayer
import com.nrlm.baselinesurvey.utils.rememberMediaState


@Composable
fun FullscreenView(
    navController: NavHostController
) {


    val context = LocalContext.current

    val mMediaItem = remember { mutableStateOf(MediaItem.EMPTY) }
    val mediaItem = MediaItem.Builder().setUri("https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M6ParticipatoryWealthRanking.mp4")
        .setMediaId("5")
        .setMediaMetadata(MediaMetadata.Builder().setDisplayTitle("Participatory Wealth Ranking").build()).build()
    mMediaItem.value = mediaItem

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.isStatusBarVisible = !isLandscape
        systemUiController.isNavigationBarVisible = !isLandscape
    }

    val player by rememberManagedExoPlayer()
    LaunchedEffect(mediaItem, player) {
        player?.run {
            setMediaItem(mMediaItem.value)
            prepare()
        }
    }

    val mediaState = rememberMediaState(player)
    val mediaContent = remember {
        movableContentOf { isLandscape: Boolean, modifier: Modifier ->
            MediaContent(mediaState, isLandscape, modifier, navController)
        }
    }
    Scaffold(
        topBar = {
            /*if (!isLandscape) {
                TopAppBar(
                    title = {
                        Text(videoItem.value.title, style = mediumTextStyle, color = textColorDark)
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Filled.ArrowBack, null, tint = textColorDark)
                        }
                    },
                    backgroundColor = Color.White
                )
            }*/
        },
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .background(Color.White),
    ) { padding ->
        Box(modifier = Modifier.background(
            Color.White
        ).fillMaxSize(), contentAlignment = Alignment.Center) {
            if (!isLandscape) {
                mediaContent(
                    false,
                    Modifier
                        .padding(padding)
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                )
            }
            if (isLandscape) {
                mediaContent(
                    true,
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                )
            }

        }
    }
}

@Composable
private fun MediaContent(
    mediaState: MediaState,
    isLandscape: Boolean,
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    val activity = LocalContext.current.findActivity()!!
    val enterFullscreen = {
        activity.requestedOrientation =
            ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
    }
    val exitFullscreen = {
        @SuppressLint("SourceLockedOrientationActivity")
        // Will reset to SCREEN_ORIENTATION_USER later
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT
    }
    Box(modifier) {
        Media(
            mediaState,
            modifier = Modifier.fillMaxSize(),
            showBuffering = ShowBuffering.Always,
            buffering = {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            },
            errorMessage = { error ->
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text(
                        error.message ?: "",
                        modifier = Modifier
                            .background(Color(0x80808080), RoundedCornerShape(16.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            },
            controllerHideOnTouch = true,
            controllerAutoShow = true,
            controller = @Composable { state ->
                SimpleController(mediaState = mediaState, Modifier.fillMaxSize())
            }
        )
        Crossfade(
            targetState = mediaState.isControllerShowing,
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            if (it) {
                IconButton(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    onClick = if (isLandscape) exitFullscreen else enterFullscreen
                ) {
                    Icon(
                        painter =
                        if (isLandscape) painterResource(id = R.drawable.baseline_fullscreen_exit) else painterResource(
                            id = R.drawable.baseline_fullscreen
                        ),
                        contentDescription = "full screen button",
                    )
                }
            }
        }

        Crossfade(
            targetState = mediaState.isControllerShowing,
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            if (it) {
                IconButton(
                    onClick = { if (isLandscape) exitFullscreen() else navController.popBackStack() }, modifier = Modifier.align(
                        Alignment.TopStart
                    )
                ) {
                    Icon(Icons.Filled.ArrowBack, null, tint = Color.White)
                }
            }
        }

    }
    val onBackPressedCallback = remember {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                exitFullscreen()
            }
        }
    }
    val onBackPressedDispatcher = activity.onBackPressedDispatcher
    DisposableEffect(onBackPressedDispatcher) {
        onBackPressedDispatcher.addCallback(onBackPressedCallback)
        onDispose { onBackPressedCallback.remove() }
    }
    SideEffect {
        onBackPressedCallback.isEnabled = isLandscape
        if (isLandscape) {
            if (activity.requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_USER) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
            }
        } else {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER
        }
    }
}

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun rememberManagedExoPlayer(): State<Player?> =
    rememberManagedPlayer { context ->
        val builder = ExoPlayer.Builder(context)
        builder.setMediaSourceFactory(
            ProgressiveMediaSource.Factory(
                DefaultDataSource.Factory(
                    context
                )
            )
        )
        builder.build().apply {
            playWhenReady = true
        }
    }
