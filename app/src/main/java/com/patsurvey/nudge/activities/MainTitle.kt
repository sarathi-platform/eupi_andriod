package com.patsurvey.nudge.activities

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.patsurvey.nudge.activities.ui.theme.black100Percent
import com.patsurvey.nudge.activities.ui.theme.largeTextStyle

@Composable
fun MainTitle(title: String, modifier: Modifier) {
    Text(
        text = title,
        style = largeTextStyle,
        color = black100Percent,
        modifier = modifier,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}