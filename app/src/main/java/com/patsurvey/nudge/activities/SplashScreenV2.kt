package com.patsurvey.nudge.activities

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_1_dp
import com.nudge.core.ui.theme.dimen_300_dp
import com.nudge.core.ui.theme.dimen_8_dp
import com.nudge.core.ui.theme.grayColor
import com.nudge.navigationmanager.graphs.AuthScreen
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.splash.ConfigViewModel
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.activities.ui.theme.darkGray
import com.patsurvey.nudge.activities.ui.theme.darkYellow
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.CRP_USER_TYPE
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.ONE_SECOND
import com.patsurvey.nudge.utils.SPLASH_SCREEN_DURATION
import com.patsurvey.nudge.utils.showCustomToast
import kotlinx.coroutines.delay

@Composable
fun SplashScreenV2(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: ConfigViewModel
) {
    val context = LocalContext.current
    val networkErrorMessage = viewModel.networkErrorMessage.value
    if (networkErrorMessage.isNotEmpty()) {
        showCustomToast(context, networkErrorMessage)
        viewModel.networkErrorMessage.value = BLANK_STRING
    }
    val isLoggedIn = viewModel.isLoggedIn()/*false*/

    LaunchedEffect(key1 = true) {
        if (!(context as MainActivity).isOnline.value) {


            NudgeLogger.d(
                "SplashScreen",
                "LaunchedEffect(key1 = true) -> !(context as MainActivity).isOnline.value = true"
            )
            if (isLoggedIn) {
                NudgeLogger.d("SplashScreen", "LaunchedEffect(key1 = true) -> isLoggedIn = true")
                delay(ONE_SECOND)
                viewModel.showLoader.value = true
                delay(SPLASH_SCREEN_DURATION)
                viewModel.showLoader.value = false
                openUserHomeScreen(
                    userType = viewModel.getUserType() ?: CRP_USER_TYPE,
                    navController = navController
                )
            } else {
                NudgeLogger.d("SplashScreen", "LaunchedEffect(key1 = true) -> isLoggedIn = false")
                delay(ONE_SECOND)
                viewModel.showLoader.value = true
                viewModel.checkAndAddLanguage()
                delay(SPLASH_SCREEN_DURATION)
                viewModel.showLoader.value = false
                navController.navigate(AuthScreen.LANGUAGE_SCREEN.route)
            }
        } else {
            NudgeLogger.d(
                "SplashScreen",
                "LaunchedEffect(key1 = true) -> !(context as MainActivity).isOnline.value = false"
            )
            delay(ONE_SECOND)
            viewModel.showLoader.value = true
            NudgeLogger.d(
                "SplashScreen",
                "LaunchedEffect(key1 = true) -> fetchLanguageDetails before"
            )
            viewModel.fetchLanguageDetails() {
                NudgeLogger.d(
                    "SplashScreen",
                    "LaunchedEffect(key1 = true) -> fetchLanguageDetails callback: -> it: $it"
                )
                viewModel.fetchAppConfigForProperties()
                viewModel.showLoader.value = false
                if (it.isNotEmpty()) {
                    (context as MainActivity).quesImageList = it as MutableList<String>
                }
                if (isLoggedIn) {
                    openUserHomeScreen(
                        userType = viewModel.getUserType() ?: CRP_USER_TYPE,
                        navController = navController
                    )
                    /*
                    NudgeLogger.d(
                        "SplashScreen",
                        "LaunchedEffect(key1 = true) -> fetchLanguageDetails callback: -> isLoggedIn = true"
                    )
                    if (viewModel.getLoggedInUserType() == UPCM_USER) {
                        navController.popBackStack()
                        navController.navigate(
                            Graph.HOME
                        )
                    } else {
                        navController.navigate(AuthScreen.VILLAGE_SELECTION_SCREEN.route) {
                            popUpTo(AuthScreen.START_SCREEN.route) {
                                inclusive = true
                            }
                        }
                    }*/
                } else {
                    NudgeLogger.d(
                        "SplashScreen",
                        "LaunchedEffect(key1 = true) -> fetchLanguageDetails callback: -> isLoggedIn = false"
                    )
                    navController.navigate(AuthScreen.LANGUAGE_SCREEN.route)
                }
            }
        }
    }
    val showLoader = viewModel.showLoader.value
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(color = grayColor)
    ) {

        val (backgroundImage, nrlmContent, appNameContent, loader, bottomContent) = createRefs()
        Image(
            painter = painterResource(id = R.drawable.splash_screen),
            contentDescription = "Background Image",
            modifier = Modifier
                .fillMaxSize()
                .constrainAs(backgroundImage) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            contentScale = ContentScale.Crop

        )
        NrlmLogo(modifier = Modifier.constrainAs(nrlmContent) {
            top.linkTo(parent.top)
            bottom.linkTo(appNameContent.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        })

        AppNameContent(modifier = Modifier.constrainAs(appNameContent) {
            top.linkTo(nrlmContent.bottom, margin = 20.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        })

        if (showLoader) {
            Loader(modifier = Modifier.constrainAs(loader) {
                top.linkTo(appNameContent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            })
        }

        BottomContent(modifier = Modifier.constrainAs(bottomContent) {
            bottom.linkTo(parent.bottom)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        })
    }
}

// Component for NRLM Logo
@Composable
fun NrlmLogo(modifier: Modifier) {
    Box(modifier = modifier) {
        Image(
            painter = painterResource(id = R.drawable.ic_aajeevika_logo),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(137.dp)
        )
    }
}

@Composable
fun AppNameContent(modifier: Modifier) {
    Box(
        modifier = modifier
            .size(dimen_300_dp)
            .shadow(dimen_8_dp, shape = CircleShape)
            .border(dimen_1_dp, darkYellow, CircleShape)
            .background(Color.White, shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.ic_sarathi_logo_mini),
                contentDescription = "Sarathi Logo"
            )
            Image(
                painter = painterResource(id = R.drawable.ic_loks_icon),
                contentDescription = "LokOS Logo",
            )
        }
    }
}

@Composable
fun Loader(modifier: Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
            .height(48.dp)
    ) {
        CircularProgressIndicator(
            color = blueDark,
            modifier = Modifier
                .size(28.dp)
                .align(Alignment.Center)
        )
    }
}

// Bottom Content Component
@Composable
fun BottomContent(modifier: Modifier) {
    Box(modifier = modifier.padding(vertical = dimen_10_dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.size(height = 41.dp, width = 71.dp),
                painter = painterResource(id = R.drawable.ministry_logo),
                contentDescription = "Ministry Logo"
            )
            DividerLine()
            Image(
                painter = painterResource(id = R.drawable.ic_digital_india_logo),
                contentDescription = "Digital India Logo"
            )
            DividerLine()
            Image(
                painter = painterResource(id = R.drawable.nudge_logo),
                contentDescription = "nudge Logo"
            )
        }
    }
}

@Composable
fun DividerLine() {
    Canvas(
        modifier = Modifier
            .height(50.dp)
            .width(2.dp)
    ) {
        drawLine(
            color = darkGray,
            start = Offset(size.width / 2, 0f),
            end = Offset(size.width / 2, size.height),
            strokeWidth = 2.dp.toPx()
        )
    }
}
