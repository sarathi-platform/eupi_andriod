package com.patsurvey.nudge.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.os.Environment
import android.util.TypedValue
import androidx.activity.ComponentActivity
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Constraints
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.MainActivity
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.transform
import java.io.File

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

fun findCompleteValue(status:String): StepStatus {
    return when(status){
        BLANK_STRING->StepStatus.NOT_STARTED
        INPROGRESS_STRING->StepStatus.INPROGRESS
        COMPLETED_STRING->StepStatus.COMPLETED
        else -> {StepStatus.NOT_STARTED}
    }
}
fun Context.findActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}