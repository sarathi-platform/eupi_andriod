package com.patsurvey.nudge.customviews

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.blueDark

@Composable
fun CardArrow(
    degrees: Float,
    modifier: Modifier,
    iconColor: Color = blueDark,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
        content = {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_keyboard_arrow_down_24),
                contentDescription = "Expandable Arrow",
                modifier = Modifier.rotate(degrees),
                tint = iconColor

            )
        }
    )
}