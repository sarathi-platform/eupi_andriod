package com.patsurvey.nudge.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.patsurvey.nudge.activities.ui.theme.Nudge_Theme
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.navigation.StartFlowNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Nudge_Theme {
                val navController = rememberNavController()
                val isLoggedIn = true
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(0.dp)
                        .background(blueDark),
                ) {
                    if (isLoggedIn)
                        HomeScreen(navController = navController, modifier = Modifier.fillMaxWidth())
                    else
                        StartFlowNavigation(navController = navController)
                }
            }
        }
    }
}