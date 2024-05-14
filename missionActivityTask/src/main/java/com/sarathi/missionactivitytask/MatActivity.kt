package com.sarathi.missionactivitytask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.sarathi.missionactivitytask.viewmodels.MissionScreenViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MatActivity : ComponentActivity() {
    private val mViewModel: MissionScreenViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        setContent {
            MissionScreen_1()


        }
    }


    @Preview(showBackground = true)
    @Composable
    fun MissionScreen_1(
    ) {
        mViewModel.getMission()
        Text(text = "abc")
    }

}