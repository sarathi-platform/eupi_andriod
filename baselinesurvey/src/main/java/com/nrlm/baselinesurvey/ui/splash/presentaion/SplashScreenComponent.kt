package com.nrlm.baselinesurvey.ui.splash.presentaion

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.ONE_SECOND
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.SPLASH_SCREEN_DURATION
import com.nrlm.baselinesurvey.navigation.navgraph.Graph
import com.nrlm.baselinesurvey.ui.common_components.LoaderComponent
import com.nrlm.baselinesurvey.ui.splash.viewModel.SplashScreenViewModel
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.ui.theme.smallTextStyleNormalWeight
import com.nrlm.baselinesurvey.ui.theme.smallerTextStyle
import com.nrlm.baselinesurvey.utils.BaselineCore
import com.nrlm.baselinesurvey.navigation.AuthScreen
import kotlinx.coroutines.delay

@Composable
fun SplashScreenComponent(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: SplashScreenViewModel
) {

    val context = LocalContext.current
    val networkErrorMessage = viewModel.networkErrorMessage.value
    if(networkErrorMessage.isNotEmpty()){
        viewModel.networkErrorMessage.value = BLANK_STRING
    }

    val loaderState = viewModel.loaderState.value
    val isLoggedIn = viewModel.isLoggedIn

    LaunchedEffect(key1 = Unit) {
        if (!BaselineCore.isOnline.value) {
            if (viewModel.isLoggedIn) {
                delay(ONE_SECOND)
                viewModel.onEvent<LoaderEvent>(LoaderEvent.UpdateLoaderState(true))
                delay(SPLASH_SCREEN_DURATION)
                viewModel.onEvent<LoaderEvent>(LoaderEvent.UpdateLoaderState(false))
                navController.navigate(route = Graph.HOME){
                    launchSingleTop=true
                    popUpTo(AuthScreen.START_SCREEN.route){
                        inclusive=true
                    }
                }
            } else {
                viewModel.splashScreenUseCase.saveLanguageOpenFromUseCase.invoke()
                delay(ONE_SECOND)
                viewModel.onEvent<LoaderEvent>(LoaderEvent.UpdateLoaderState(true))
                viewModel.checkAndAddLanguage()
                delay(SPLASH_SCREEN_DURATION)
                viewModel.onEvent<LoaderEvent>(LoaderEvent.UpdateLoaderState(false))

                navController.navigate(AuthScreen.LANGUAGE_SCREEN.route)
            }
        } else {
            delay(ONE_SECOND)
            viewModel.onEvent<LoaderEvent>(LoaderEvent.UpdateLoaderState(true))
            viewModel.fetchLanguageConfigDetails() {
                viewModel.onEvent<LoaderEvent>(LoaderEvent.UpdateLoaderState(false))
                if (isLoggedIn) {
                    navController.navigate(route = Graph.HOME){
                        launchSingleTop=true
                        popUpTo(AuthScreen.START_SCREEN.route){
                            inclusive=true
                        }
                    }
                } else {
                    navController.navigate(AuthScreen.LANGUAGE_SCREEN.route)
                }
            }
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .background(color = Color.White)
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(bottom = 32.dp, top = 20.dp)
            .then(modifier)
    ) {

        val (bottomContent, appNameContent, nrlmContent,loader) = createRefs()

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
                /*Text(text = "Sarathi", style = veryLargeTextStyle, color = blueDark)*/
                Image(painter = painterResource(id = R.drawable.sarathi_logo_full), contentDescription = "Sarathi Logo")
                Text(text = "To End Ultra Poverty", style = smallTextStyleNormalWeight, color = blueDark)
            }
        }

        LoaderComponent(
            modifier = Modifier.constrainAs(loader) {
            top.linkTo(appNameContent.bottom)
            start.linkTo(parent.start)
            end.linkTo(parent.end) },
            visible = loaderState.isLoaderVisible
        )

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