package com.sarathi.missionactivitytask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.sarathi.missionactivitytask.viewmodels.MissionScreenViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MatActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MissionScreen_1()


        }
    }


    @Preview(showBackground = true)
    @Composable
    fun MissionScreen_1(
        viewModel: MissionScreenViewModel = hiltViewModel()
    ) {
        Text(text = "abc")
    }

}