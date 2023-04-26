package com.patsurvey.nudge.utils

import android.content.Context
import android.util.TypedValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.*
import androidx.compose.ui.unit.Constraints

fun Modifier.visible(visible: Boolean) = if (visible) this else this.then(Invisible)
private object Invisible : LayoutModifier {
    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {
        val placeable = measurable.measure(constraints)
        return layout(placeable.width, placeable.height) {}
    }
}

fun dpToPx(iContext: Context, dp: Int): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        iContext.resources.displayMetrics
    )
        .toInt()
}