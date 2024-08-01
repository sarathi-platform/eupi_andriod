package com.nudge.incomeexpensemodule.ui.screens.dataTab.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.nudge.core.ui.theme.defaultTextStyle
import com.nudge.incomeexpensemodule.ui.screens.dataTab.viewModel.DataTabScreenViewModel

@Composable
fun DataTabScreen(
    modifier: Modifier = Modifier,
    dataTabScreenViewModel: DataTabScreenViewModel
) {

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

        Text(text = "Data Tab", color = Color.Red, style = defaultTextStyle)

    }

}