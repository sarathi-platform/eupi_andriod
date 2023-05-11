package com.patsurvey.nudge.activities

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.patsurvey.nudge.activities.ui.theme.largeTextStyle
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.navigation.ScreenRoutes
import com.patsurvey.nudge.utils.SPLASH_SCREEN_DURATION
import kotlinx.coroutines.delay
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.compose.OnParticleSystemUpdateListener
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.PartySystem
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

@Composable
fun StepCompletionScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    message: String,
    fromScreen: String
) {

    val animationOver = remember {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier)
    ) {
        KonfettiView(
            modifier = Modifier.fillMaxSize(),
            parties = listOf(
                Party(
                    speed = 0f,
                    maxSpeed = 30f,
                    damping = 0.9f,
                    spread = 360,
                    colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
                    emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100),
                    position = Position.Relative(0.5, 0.3)
                )
            ),
            updateListener = object : OnParticleSystemUpdateListener {
                override fun onParticleSystemEnded(system: PartySystem, activeSystems: Int) {
                    if (activeSystems == 0) animationOver.value = true
                }
            }
        )

        if (animationOver.value) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LaunchedEffect(key1 = true) {
                    delay(SPLASH_SCREEN_DURATION)
                    navController.popBackStack(
                        when (fromScreen) {
                            "transect_walk_screen" -> ScreenRoutes.TRANSECT_WALK_SCREEN.route
                            "didi_screen" -> ScreenRoutes.DIDI_SCREEN.route
                            else -> {
                                ScreenRoutes.TRANSECT_WALK_SCREEN.route
                            }
                        },
                        inclusive = true,
                        saveState = true
                    )
                }
                Text(text = "🎉", fontSize = 50.sp)
                Text(
                    text = message,
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    color = textColorDark,
                    textAlign = TextAlign.Center,
                    style = largeTextStyle
                )
            }
        }
    }
}