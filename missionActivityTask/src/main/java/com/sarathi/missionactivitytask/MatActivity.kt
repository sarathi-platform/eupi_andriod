package com.sarathi.missionactivitytask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.sarathi.missionactivitytask.ui.basic.mission.BasicActivityCard
import com.sarathi.missionactivitytask.ui.theme.The_nudgeTheme
import com.sarathi.missionactivitytask.ui.theme.white
import com.sarathi.missionactivitytask.ui.utils.StatusEnum

class MatActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            The_nudgeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = white
                ) {
                    CheckMissionScreen()
                }
            }
        }
    }
}

@Preview
@Composable
fun CheckMissionScreen(){
    BasicActivityCard(
        title = "CSG disbursement to Didis dsgsgs",
        status = StatusEnum.InProgress,
        pendingCount = 2,
        totalCount = 10,
        primaryButtonText = "Start",
        countStatusText = "Activity Pending",
        topHeaderText = "Due on 22nd March",
        needToShowProgressBar = true,
    )
}