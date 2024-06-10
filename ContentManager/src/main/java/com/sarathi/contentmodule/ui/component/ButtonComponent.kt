package com.sarathi.contentmodule.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nudge.core.ui.theme.lightGray2
import com.nudge.core.ui.theme.smallerTextStyle
import com.sarathi.dataloadingmangement.BLANK_STRING

@Composable
fun ButtonComponent(title: String = BLANK_STRING) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .padding(end = 16.dp, top = 2.dp)
                .size(60.dp)
                .clip(shape = CircleShape)
                .border(
                    1.dp, color = lightGray2, RoundedCornerShape(100.dp)
                )
                .background(color = Color.Transparent)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                textAlign = TextAlign.Center,
                style = smallerTextStyle.copy(fontSize = 10.sp),
                modifier = Modifier.padding(10.dp),
            )

        }
    }
}