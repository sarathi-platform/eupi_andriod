package com.sarathi.smallgroupmodule.ui.smallGroupAttendance.presentation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sarathi.missionactivitytask.ui.components.ToolBarWithMenuComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmallGroupAttendanceScreen(
    modifier: Modifier = Modifier,
    smallGroupId: Int = 0
) {

    ToolBarWithMenuComponent(
        title = "",
        modifier = Modifier,
        onBackIconClick = { /*TODO*/ },
        onSearchValueChange = {},
        isFilterSelected = {},
        isDataAvailable = true,
        onBottomUI = { /*TODO*/ },
        tabBarView = { /*TODO*/ }) {

        DatePicker(state = rememberDatePickerState(), modifier = Modifier.fillMaxWidth())
        DatePickerDialog(onDismissRequest = { /*TODO*/ }, confirmButton = { /*TODO*/ }) {

        }


    }

}