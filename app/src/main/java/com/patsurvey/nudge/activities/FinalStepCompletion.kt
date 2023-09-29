package com.patsurvey.nudge.activities

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
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.largeTextStyle
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.utils.FINAL_STEP_COMPLETION_DELAY
import kotlinx.coroutines.delay

@Composable
fun FinalStepCompletionScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    message: String,
    onNavigateBack:()->Unit
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
        /*KonfettiView(
            modifier = Modifier.fillMaxSize(),
            parties = listOf(
                Party(
                    speed = 0f,
                    maxSpeed = 30f,
                    damping = 0.9f,
                    spread = 360,
                    colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
                    emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(80),
                    position = Position.Relative(0.5, 0.3)
                )
            ),
            updateListener = object : OnParticleSystemUpdateListener {
                override fun onParticleSystemEnded(system: PartySystem, activeSystems: Int) {
                    if (activeSystems == 0) animationOver.value = true
                }
            }
        )*/

//        if (animationOver.value) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val composition by rememberLottieComposition(LottieCompositionSpec.Asset("animations/animation.json"))

//            val composition by rememberLottieComposition(LottieCompositionSpec.Asset("app/src/main/assets/animations/animation.json"))
//            val composition by rememberLottieComposition(LottieCompositionSpec.Url("https://lottie.host/63e951fa-dd81-46c2-84df-e1d56406746b/BPYw85o5W3.json"))
            Log.d("FinalStepCompletionScreen", "composition: $composition,  composition?.duration: ${composition?.duration}, composition?.startFrame: ${composition?.startFrame} composition?.endFrame: ${composition?.endFrame}")
            composition?.duration
            mediaPlayer.start()
            LottieAnimation(composition, modifier = Modifier
                .size(300.dp), maintainOriginalImageBounds = true)

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