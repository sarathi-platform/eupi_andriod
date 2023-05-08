package com.patsurvey.nudge.customviews

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patsurvey.nudge.activities.ui.theme.black1
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.activities.ui.theme.grayProgressBackground
import com.patsurvey.nudge.activities.ui.theme.textColorDark

@Composable
fun CircularProgressBar(
    modifier: Modifier,
    circleRadius: Float = 100f,
    backgroundColor: Color = Color.Transparent,
    progressBackgroundColor: Color = grayProgressBackground,
    progressColor: Color = blueDark,
    initialPosition: Int = 0,
    minProgress: Int = 0,
    maxProgress: Int = 6,
    borderThickness : Dp = 7.dp,
    centerTextSize: TextUnit = 14.sp
) {
    var circleCenter by remember {
        mutableStateOf(Offset.Zero)
    }

    val positionValue by remember {
        mutableStateOf(initialPosition)
    }

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val circleThickness = borderThickness//width/30f
            circleCenter = Offset(x = width/2f, y = height/2f)

            drawCircle(
               color = backgroundColor,
                radius = circleRadius,
                center = circleCenter
            )

            drawCircle(
                style = Stroke(
                    width = circleThickness.value
                ),
                color = progressBackgroundColor,
                radius = circleRadius,
                center = circleCenter
            )

            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = (360f / maxProgress) * positionValue.toFloat(),
                style = Stroke(
                    width = circleThickness.value,
                    cap = StrokeCap.Butt
                ),
                useCenter = false,
                size = Size(
                    width = circleRadius * 2f,
                    height = circleRadius * 2f
                ),
                topLeft = Offset(
                    x = (width - circleRadius*2f)/2f,
                    y = (height- circleRadius*2f)/2f
                )
            )

            drawContext.canvas.nativeCanvas.apply {
                drawIntoCanvas {
                    drawText(
                        "$initialPosition/$maxProgress",
                        center.x,
                        center.y + centerTextSize.toPx() / 3f,
                        Paint().apply {
                            textSize = centerTextSize.toPx()
                            textAlign = Paint.Align.CENTER
                            color = textColorDark.toArgb()
                            isFakeBoldText = true

                        }
                    )
                }
            }

        }

    }
}
@Preview(showBackground = true)
@Composable
fun Preview() {
    CircularProgressBar(
        modifier = Modifier
            .size(250.dp),
        circleRadius = LocalDensity.current.run { 120.dp.toPx() },
        initialPosition = 5,
        borderThickness = 10.dp,
        centerTextSize = 15.sp
    )
}