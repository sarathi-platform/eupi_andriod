package com.patsurvey.nudge.activities.video

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.activity.OnBackPressedCallback
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.navigation.NavHostController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.mediumTextStyle
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.utils.*


/*@Composable
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

    BackHandler() {
        showFullScreen.value = false
        with(context) {
            setScreenOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        }
        navController.popBackStack()
    }
    val mediaItem = MediaItem.Builder()
        .setUri(
            if (videoItem.value.isDownload == DownloadStatus.DOWNLOADED.value) viewModel.getVideoPath(
                context,
                videoItem.value.id
            ) else videoItem.value.url
        )
        .setMediaId(videoItem.value.id.toString())
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setDisplayTitle(videoItem.value.title)
                .build()
        )
        .build()

    if (!viewModel.showLoader.value) {
        FullscreenView(navController = navController, mediaItem = mediaItem, videoItem = videoItem.value)
*//*                VideoPlayer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (showFullScreen.value) screenHeight.dp else 250.dp)
                        .background(white),
                    videoItem = videoItem.value,
                    viewModel = viewModel,
                    showFullScreen = {
                        showFullScreen.value = it
                    }
                )*//*
    }


    AnimatedVisibility(visible = !showFullScreen.value) {

    }
}*/


/*@Composable
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
}*/

@Composable
fun FullscreenView(
    navController: NavHostController,
    viewModel: VideDetailPlayerViewModel,
    videoId: Int
) {
    LaunchedEffect(key1 = true) {
        viewModel.getVideoDetails(videoId)
    }

    val videoItem = viewModel.trainingVideo.collectAsState()

    val context = LocalContext.current

    val mMediaItem = remember { mutableStateOf(MediaItem.EMPTY) }
    val mediaItem = MediaItem.Builder()
        .setUri(
            if (videoItem.value.isDownload == DownloadStatus.DOWNLOADED.value) viewModel.getVideoPath(
                context,
                videoItem.value.id
            ) else videoItem.value.url
        )
        .setMediaId(videoItem.value.id.toString())
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setDisplayTitle(videoItem.value.title)
                .build()
        )
        .build()
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
            .systemBarsPadding(),
    ) { padding ->
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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

            Text(
                videoItem.value.title,
                style = mediumTextStyle,
                color = textColorDark,
                modifier = Modifier
                    .align(Alignment.Start)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

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
                    .padding(horizontal = 16.dp)
            )
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
fun rememberManagedExoPlayer(): State<androidx.media3.common.Player?> =
    rememberManagedPlayer { context ->
        val builder = androidx.media3.exoplayer.ExoPlayer.Builder(context)
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
