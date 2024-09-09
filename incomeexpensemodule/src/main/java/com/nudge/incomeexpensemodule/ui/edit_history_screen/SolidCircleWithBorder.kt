package com.nudge.incomeexpensemodule.ui.edit_history_screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(showBackground = true)
@Composable
fun SolidCircleWithBorder(
    circleColor: Color = MaterialTheme.colorScheme.primary,
    borderColor: Color = Color.Black,
    circleDiameter: Int = 10,
    borderWidth: Float = 2f
) {
    Canvas(modifier = Modifier.size(circleDiameter.dp)) {
        val radius = size.minDimension / 2
        drawCircle(
            color = circleColor,
            radius = radius - 5
        )
        drawCircle(
            color = borderColor,
            radius = radius,
            style = Stroke(width = borderWidth)
        )
    }
}
