package com.patsurvey.nudge.customviews

import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.patsurvey.nudge.activities.ui.theme.blueDark

@Composable
fun CustomProgressBar(modifier: Modifier){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(top = 30.dp)
            .then(modifier)
    ) {
        CircularProgressIndicator(
            color = blueDark,
            modifier = Modifier
                .size(28.dp)
                .align(Alignment.Center)
        )
    }
}