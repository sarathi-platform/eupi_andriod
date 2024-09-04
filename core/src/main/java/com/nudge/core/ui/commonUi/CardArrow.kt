package com.nudge.core.ui.commonUi

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.nudge.core.ui.theme.blueDark

@Composable
fun CardArrow(
    degrees: Float,
    modifier: Modifier,
    iconColor: Color = blueDark,
    arrowIcon: Int,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.padding(5.dp),
        content = {
            Icon(
                painter = painterResource(id = arrowIcon),
                contentDescription = "Expandable Arrow",
                modifier = Modifier.rotate(degrees),
                tint = iconColor

            )
        }
    )
}