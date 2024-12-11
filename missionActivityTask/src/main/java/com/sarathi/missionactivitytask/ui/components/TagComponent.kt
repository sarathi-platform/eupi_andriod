package com.sarathi.missionactivitytask.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nudge.core.ui.theme.brownDark
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_4_dp
import com.nudge.core.ui.theme.dimen_8_dp
import com.nudge.core.ui.theme.grayColor
import com.nudge.core.ui.theme.smallerTextStyle
import com.nudge.core.ui.theme.stepBoxActiveColor

@Composable
fun TagComponent(text: String) {
    Box(
        modifier = Modifier
            .padding(dimen_10_dp)
            .background(
                color = stepBoxActiveColor,
                shape = RoundedCornerShape(50)
            )
            .border(
                width = 0.3.dp,
                color = grayColor,
                shape = RoundedCornerShape(50)
            )
            .padding(horizontal = dimen_8_dp, vertical = dimen_4_dp)
    ) {
        Text(
            text = text,
            style = smallerTextStyle.copy(fontSize = 10.sp, color = brownDark),
        )
    }

}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun TagComponentPreview() {
    TagComponent(text = "Livelihood1")
}