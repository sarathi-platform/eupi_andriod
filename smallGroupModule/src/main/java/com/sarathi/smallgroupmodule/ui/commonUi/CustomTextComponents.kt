package com.sarathi.smallgroupmodule.ui.commonUi

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.nudge.core.ui.theme.black100Percent
import com.nudge.core.ui.theme.mediumTextStyle

@Composable
fun MainTitle(title: String, modifier: Modifier, align: TextAlign = TextAlign.Start) {
    Text(
        text = title,
        style = mediumTextStyle,
        color = black100Percent,
        modifier = modifier,
        maxLines = 1,
        textAlign = align,
        overflow = TextOverflow.Ellipsis,
    )
}