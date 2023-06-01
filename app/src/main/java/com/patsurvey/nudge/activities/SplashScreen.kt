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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.splash.ConfigViewModel
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.activities.ui.theme.smallTextStyleNormalWeight
import com.patsurvey.nudge.activities.ui.theme.smallerTextStyle
import com.patsurvey.nudge.navigation.AuthScreen
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.showCustomToast

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
    LaunchedEffect(key1 = true) {
        viewModel.fetchLanguageDetails {
            if(isLoggedIn){
                navController.navigate(AuthScreen.VILLAGE_SELECTION_SCREEN.route){
                    popUpTo(AuthScreen.START_SCREEN.route){
                        inclusive=true
                    }
                }
            }else{
                navController.navigate(AuthScreen.LANGUAGE_SCREEN.route)

            }
        }
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
                /*Text(text = "Sarathi", style = veryLargeTextStyle, color = blueDark)*/
                Image(painter = painterResource(id = R.drawable.sarathi_logo_full), contentDescription = "Sarathi Logo")
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