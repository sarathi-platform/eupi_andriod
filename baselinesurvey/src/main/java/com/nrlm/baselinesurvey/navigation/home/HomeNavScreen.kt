package com.patsurvey.nudge.navigation.home

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.navigation.BottomNavigationBar
import com.nrlm.baselinesurvey.navigation.home.NavHomeGraph

@Composable
fun HomeNavScreen(navController: NavHostController = rememberNavController(), prefRepo: PrefRepo) {
    Scaffold(bottomBar = { BottomNavigationBar(navController = navController) }) {
        it
//        Box(modifier = Modifier.padding(top = dimen_56_dp + dimen_10_dp).fillMaxSize()) {
        NavHomeGraph(navController = navController, prefRepo)
//        }
    }
}
