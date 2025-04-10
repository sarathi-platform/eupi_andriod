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
fun MoreScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Text(text = "More Screen", modifier = Modifier.align(Alignment.Center), color = Color.Red)
    }
//    SocialMappingDidiListScreen(navController, modifier = modifier)
}