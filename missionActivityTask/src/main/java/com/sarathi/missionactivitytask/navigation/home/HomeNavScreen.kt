package com.nrlm.baselinesurvey.navigation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.sarathi.missionactivitytask.navigation.BottomNavigationBar
import com.sarathi.missionactivitytask.ui.theme.white

@Composable
fun HomeNavScreen(navController: NavHostController = rememberNavController()) {
    Scaffold(
        modifier = Modifier.background(white),
        bottomBar = { BottomNavigationBar(navController = navController) }) {
        it
        NavHomeGraph(
            navController = navController,
            modifier = Modifier.padding(bottom = it.calculateBottomPadding())
        )
    }
}
