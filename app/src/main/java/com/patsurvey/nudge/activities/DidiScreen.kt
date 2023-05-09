package com.patsurvey.nudge.activities

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController

@Composable
fun DidiScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    didiViewModel: AddDidiViewModel
) {
    /*Box(modifier = modifier.fillMaxSize()) {
        Text(text = "Didi Screen", modifier = Modifier.align(Alignment.Center), color = Color.Red)
    }*/
    SocialMappingDidiListScreen(navController, modifier = modifier, didiViewModel = didiViewModel)
}