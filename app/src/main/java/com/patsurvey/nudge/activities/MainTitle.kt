package com.patsurvey.nudge.activities

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.patsurvey.nudge.activities.ui.theme.black100Percent
import com.patsurvey.nudge.activities.ui.theme.mediumTextStyle

@Composable
fun MainTitle(title: String, modifier: Modifier) {
    Text(
        text = title,
        style = mediumTextStyle,
        color = black100Percent,
        modifier = modifier,
        maxLines = 1,
        textAlign=TextAlign.Start,
        overflow = TextOverflow.Ellipsis,
    )
}