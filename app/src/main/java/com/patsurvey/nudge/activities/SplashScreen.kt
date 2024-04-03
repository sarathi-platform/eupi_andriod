package com.patsurvey.nudge.activities

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
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.nudge.core.ui.navigation.CoreGraph
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.splash.ConfigViewModel
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.activities.ui.theme.smallTextStyleNormalWeight
import com.patsurvey.nudge.activities.ui.theme.smallerTextStyle
import com.patsurvey.nudge.navigation.AuthScreen
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.ONE_SECOND
import com.patsurvey.nudge.utils.SPLASH_SCREEN_DURATION
import com.patsurvey.nudge.utils.UPCM_USER
import com.patsurvey.nudge.utils.showCustomToast
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: ConfigViewModel
) {
    val context = LocalContext.current
    val networkErrorMessage = viewModel.networkErrorMessage.value
    if(networkErrorMessage.isNotEmpty()){
        showCustomToast(context,networkErrorMessage)
        viewModel.networkErrorMessage.value = BLANK_STRING
    }
    val isLoggedIn = viewModel.isLoggedIn()/*false*/
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = true) {
        if (!(context as MainActivity).isOnline.value) {


            NudgeLogger.d("SplashScreen", "LaunchedEffect(key1 = true) -> !(context as MainActivity).isOnline.value = true")
            if (isLoggedIn) {
                NudgeLogger.d("SplashScreen", "LaunchedEffect(key1 = true) -> isLoggedIn = true")
                delay(ONE_SECOND)
                viewModel.showLoader.value=true
                delay(SPLASH_SCREEN_DURATION)
                viewModel.showLoader.value=false
                if(viewModel.getUserType().equals(UPCM_USER)){
                    navController.navigate(route = CoreGraph.HOME){
                        launchSingleTop=true
                        popUpTo(AuthScreen.START_SCREEN.route){
                            inclusive=true
                        }
                    }
                }else{
                    navController.navigate(AuthScreen.VILLAGE_SELECTION_SCREEN.route) {
                        popUpTo(AuthScreen.START_SCREEN.route) {
                            inclusive = true
                        }
                    }
                }

            } else {
                NudgeLogger.d("SplashScreen", "LaunchedEffect(key1 = true) -> isLoggedIn = false")
                delay(ONE_SECOND)
                viewModel.showLoader.value=true
                viewModel.checkAndAddLanguage()
                delay(SPLASH_SCREEN_DURATION)
                viewModel.showLoader.value=false
                navController.navigate(AuthScreen.LANGUAGE_SCREEN.route)
            }
        } else
        {
            NudgeLogger.d("SplashScreen", "LaunchedEffect(key1 = true) -> !(context as MainActivity).isOnline.value = false")
            delay(ONE_SECOND)
            viewModel.showLoader.value=true
            NudgeLogger.d("SplashScreen", "LaunchedEffect(key1 = true) -> fetchLanguageDetails before")
            viewModel.fetchLanguageDetails() {
                NudgeLogger.d("SplashScreen", "LaunchedEffect(key1 = true) -> fetchLanguageDetails callback: -> it: $it")
                viewModel.showLoader.value=false
                if(it.isNotEmpty()){
                    (context as MainActivity).quesImageList = it as MutableList<String>
                }
                if (isLoggedIn) {
                    if(viewModel.getUserType().equals(UPCM_USER)){
                        navController.navigate(route = CoreGraph.BASE_HOME){
                            launchSingleTop=true
                            popUpTo(AuthScreen.START_SCREEN.route){
                                inclusive=true
                            }
                        }
                    }else{
                        navController.navigate(AuthScreen.VILLAGE_SELECTION_SCREEN.route) {
                            popUpTo(AuthScreen.START_SCREEN.route) {
                                inclusive = true
                            }
                        }
                    }
                } else {
                    NudgeLogger.d("SplashScreen", "LaunchedEffect(key1 = true) -> fetchLanguageDetails callback: -> isLoggedIn = false")
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
                Image(painter = painterResource(id = R.drawable.sarathi_logo_full), contentDescription = "Sarathi Logo")
            }
        }
        if(viewModel.showLoader.value){
            Box(
                modifier = Modifier
                    .constrainAs(loader) {
                        top.linkTo(appNameContent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
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