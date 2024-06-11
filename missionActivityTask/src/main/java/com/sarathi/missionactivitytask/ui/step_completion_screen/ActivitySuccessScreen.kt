package com.sarathi.missionactivitytask.ui.step_completion_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nudge.core.ui.events.theme.largeTextStyle
import com.nudge.core.ui.events.theme.textColorDark
import com.sarathi.missionactivitytask.R
import kotlinx.coroutines.delay
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.compose.OnParticleSystemUpdateListener
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.PartySystem
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit


@Composable
fun ActivitySuccessScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    message: String,
    onNavigateBack: () -> Unit
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
                    emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(80),
                    position = Position.Relative(0.5, 0.3)
                )
            ),
            updateListener = object : OnParticleSystemUpdateListener {
                override fun onParticleSystemEnded(system: PartySystem, activeSystems: Int) {
                    if (activeSystems == 0) animationOver.value = true
                }
            }
        )

        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 14.dp)
        ) {
            LaunchedEffect(key1 = true) {
                delay(900L)
                onNavigateBack()
            }
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_activity_complete),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Column(modifier = modifier.padding(horizontal = 10.dp)) {
                        Text(
                            text = "Receipt of funds added successfully for \n" +
                                    "Ganbari Sikla VO.",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            color = textColorDark,
                            style = largeTextStyle,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

//@Composable
//fun ActivitySuccessScreen(
//    navController: NavController,
//    modifier: Modifier = Modifier,
//    message: String,
//    onNavigateBack: () -> Unit
//) {
//
//    val context = LocalContext.current
//
//    LaunchedEffect(key1 = true) {
//        delay(3000L)
//    }
//
//    val animationOver = remember {
//        mutableStateOf(false)
//    }
//
//    Column(
//        Modifier
//            .fillMaxSize()
//            .padding(horizontal = 16.dp)
//            .padding(top = 14.dp)
//    ) {
//
//        Box(modifier = Modifier.fillMaxSize()) {
//            Column(
//                modifier = Modifier.align(Alignment.Center),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Image(
//                    painter = painterResource(id = R.drawable.ic_activity_complete),
//                    contentDescription = null
//                )
//                Spacer(modifier = Modifier.height(20.dp))
//                Column(modifier = modifier.padding(horizontal = 10.dp)) {
//                    Text(
//                        text = "Receipt of funds added successfully for \n" +
//                                "Ganbari Sikla VO.",
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(horizontal = 16.dp),
//                        color = textColorDark,
//                        style = largeTextStyle,
//                        textAlign = TextAlign.Center
//                    )
//                }
//            }
//        }
//    }
//
//}