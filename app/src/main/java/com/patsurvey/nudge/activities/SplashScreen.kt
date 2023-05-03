package com.patsurvey.nudge.activities

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.splash.ConfigViewModel
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.activities.ui.theme.smallTextStyleNormalWeight
import com.patsurvey.nudge.activities.ui.theme.smallerTextStyle
import com.patsurvey.nudge.activities.ui.theme.veryLargeTextStyle
import com.patsurvey.nudge.navigation.ScreenRoutes
import com.patsurvey.nudge.utils.SPLASH_SCREEN_DURATION
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: ConfigViewModel
) {

    val isLoggedIn = viewModel.isLoggedIn() 
    LaunchedEffect(key1 = true) {
        delay(SPLASH_SCREEN_DURATION)
        navController.navigate(if (isLoggedIn) ScreenRoutes.VILLAGE_SELECTION_SCREEN.route else ScreenRoutes.LANGUAGE_SCREEN.route)
    }

    ConstraintLayout(
        modifier = Modifier
            .background(color = Color.White)
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 32.dp)
            .then(modifier)
    ) {

        val (bottomContent, appNameContent, nrlmContent) = createRefs()

        Box(modifier = Modifier.constrainAs(nrlmContent) {
            top.linkTo(parent.top)
            bottom.linkTo(appNameContent.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }) {
            Image(
                painter = painterResource(id = R.drawable.nrlm_logo),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(137.dp)
            )
        }

        Box(modifier = Modifier.constrainAs(appNameContent) {
            top.linkTo(parent.top)
            bottom.linkTo(bottomContent.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Sarathi", style = veryLargeTextStyle, color = blueDark)
                Text(text = "To End Ultra Poverty", style = smallTextStyleNormalWeight, color = blueDark)
            }
        }

        Box(modifier = Modifier.constrainAs(bottomContent) {
            bottom.linkTo(parent.bottom)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(
                        Alignment.BottomCenter
                    )
            ) {
                Text(text = "Designed By", style = smallerTextStyle, color = blueDark)
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()

                ) {
                    Image(
                        painter = painterResource(id = R.drawable.nudge_logo),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(93.dp, 19.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Image(
                        painter = painterResource(id = R.drawable.ttn_logo),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(40.dp, 19.dp)
                    )
                }
            }
        }
    }
}