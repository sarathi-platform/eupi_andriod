package com.nudge.core.ui.commonUi

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun <T> CustomIconComponent(
    iconProperties: IconProperties<T>
) {

    when (iconProperties.icon) {
        is Painter -> {
            Icon(
                painter = iconProperties.icon,
                contentDescription = iconProperties.contentDescription,
                tint = iconProperties.tint,
                modifier = iconProperties.modifier
            )
        }

        is ImageVector -> {
            Icon(
                imageVector = iconProperties.icon,
                contentDescription = iconProperties.contentDescription,
                tint = iconProperties.tint,
                modifier = iconProperties.modifier
            )
        }

        is ImageBitmap -> {
            Icon(
                bitmap = iconProperties.icon,
                contentDescription = iconProperties.contentDescription,
                tint = iconProperties.tint,
                modifier = iconProperties.modifier
            )
        }
    }

}