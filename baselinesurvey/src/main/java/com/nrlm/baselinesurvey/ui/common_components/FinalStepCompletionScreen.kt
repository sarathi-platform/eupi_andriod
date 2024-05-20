package com.nrlm.baselinesurvey.ui.common_components

import android.media.MediaPlayer
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.nrlm.baselinesurvey.FINAL_STEP_COMPLETION_DELAY
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.ui.theme.largeTextStyle
import com.nrlm.baselinesurvey.ui.theme.textColorDark
import kotlinx.coroutines.delay

@Composable
fun FinalStepCompletionScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    message: String = "***Welcome to BaseLine Application***",
    onNavigateBack: () -> Unit
) {

    val animationOver = remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current

    val mediaPlayer = MediaPlayer.create(context, R.raw.success_audio)
    mediaPlayer.isLooping = false

    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier)
    ) {

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val composition by rememberLottieComposition(LottieCompositionSpec.Asset("animations/animation.json"))

            Log.d(
                "FinalStepCompletionScreen",
                "composition: $composition,  composition?.duration: ${composition?.duration}, composition?.startFrame: ${composition?.startFrame} composition?.endFrame: ${composition?.endFrame}"
            )
            composition?.duration
            mediaPlayer.start()
            LottieAnimation(
                composition, modifier = Modifier
                    .size(300.dp), maintainOriginalImageBounds = true
            )

            LaunchedEffect(key1 = true) {
                delay(FINAL_STEP_COMPLETION_DELAY)
                mediaPlayer.stop()
                onNavigateBack()
            }


            Text(
                text = message,
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                color = textColorDark,
                textAlign = TextAlign.Center,
                style = largeTextStyle
            )
        }
//        }
    }
}