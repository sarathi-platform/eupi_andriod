package com.patsurvey.nudge.navigation.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.navigation.home.NavHomeGraph

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HomeNavScreen(navController: NavHostController = rememberNavController(), prefRepo: PrefRepo) {
    Scaffold() {
        it
        NavHomeGraph(navController = navController, prefRepo)
    }
}