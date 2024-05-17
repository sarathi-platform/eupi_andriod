package com.sarathi.missionactivitytask.ui.components

import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asAndroidColorFilter
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun AppImageViewComponent(
    modifier: Modifier = Modifier,
    @DrawableRes resource: Int,
    colorFilter: ColorFilter? = null
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            ImageView(context).apply {
                setImageResource(resource)
                setColorFilter(colorFilter?.asAndroidColorFilter())
            }
        },
        update = {
            it.setImageResource(resource)
            it.colorFilter = colorFilter?.asAndroidColorFilter()
        }
    )
}