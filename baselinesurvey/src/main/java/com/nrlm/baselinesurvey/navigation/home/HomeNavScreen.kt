package com.nrlm.baselinesurvey.navigation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.navigation.BottomNavigationBar
import com.nrlm.baselinesurvey.ui.theme.white

@Composable
fun HomeNavScreen(navController: NavHostController = rememberNavController(), prefRepo: PrefRepo) {
    Scaffold(
        modifier = Modifier.background(white),
        bottomBar = { BottomNavigationBar(navController = navController) }) {
        it
        NavHomeGraph(
            navController = navController,
            prefRepo,
            modifier = Modifier.padding(bottom = it.calculateBottomPadding())
        )
    }
}
