package com.patsurvey.nudge.navigation.baseline

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.nrlm.baselinesurvey.navigation.BottomNavigationBar
import com.patsurvey.nudge.data.prefs.PrefRepo

@Composable
fun BSHomeNavScreen(navController: NavHostController = rememberNavController(),prefRepo: PrefRepo) {
    Scaffold(bottomBar = { BottomNavigationBar(navController = navController) }) {
        it
        BSNavHomeGraph(
            navController = navController,
            modifier = Modifier.padding(bottom = 30.dp),
            prefRepo= prefRepo
        )
    }
}
