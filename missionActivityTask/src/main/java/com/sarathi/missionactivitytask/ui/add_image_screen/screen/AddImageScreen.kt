package com.sarathi.missionactivitytask.ui.add_image_screen.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.largeTextStyle
import com.nudge.core.ui.theme.white
import com.sarathi.missionactivitytask.navigation.navigateToActivityCompletionScreen
import com.sarathi.surveymanager.ui.component.AddImageComponent
import com.sarathi.surveymanager.ui.component.ButtonNegative
import com.sarathi.surveymanager.ui.component.ButtonPositive

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddImageScreen(navController: NavController = rememberNavController()) {
    val outerState = rememberLazyListState()
    Scaffold(modifier = Modifier.fillMaxWidth(),
        containerColor = white,
        topBar = {},
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    ButtonNegative(
                        modifier = Modifier.weight(0.4f),
                        buttonTitle = "Go Back",
                        isArrowRequired = true,
                        onClick = {
                            navController.popBackStack()
                        })
                    Spacer(modifier = Modifier.width(10.dp))
                    ButtonPositive(modifier = Modifier.weight(0.4f),
                        buttonTitle = "Submit Form E",
                        isActive = true,
                        isArrowRequired = false,
                        onClick = {

                            navigateToActivityCompletionScreen(
                                navController,
                                "Completed Disbursement to 4 Didis of Ganbari Sikla (VO)"
                            )
                        })
                }
            }
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimen_16_dp)
            ) {
                Text(
                    text = "Ganbari Sikla (VO) - Attach \n" + "Physical Form E (Signed & Sealed)",
                    style = largeTextStyle
                )
                BoxWithConstraints(
                    modifier = Modifier.scrollable(
                        state = outerState,
                        Orientation.Vertical,
                    )
                ) {
                    AddImageComponent(maxCustomHeight = maxHeight) { selectedValue, isDeleted ->

                    }
                }

            }
        })
}