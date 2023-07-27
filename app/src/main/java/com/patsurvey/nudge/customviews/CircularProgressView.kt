package com.patsurvey.nudge.customviews

import android.graphics.Paint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    currentPosition: Int = 0,
    minProgress: Int = 0,
    maxProgress: Int = 6,
    borderThickness: Dp = 7.dp,
    centerTextSize: TextUnit = 14.sp
) {
    var circleCenter by remember {
        mutableStateOf(Offset.Zero)
    }

    var animationPlayed by remember {
        mutableStateOf(true)
    }

    val curPercentage = animateFloatAsState(
        targetValue = if (animationPlayed) initialPosition.toFloat() else 0f,
        animationSpec = tween()
    )

    LaunchedEffect(key1 = initialPosition) {
        animationPlayed = true
    }

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val circleThickness = borderThickness//width/30f
            circleCenter = Offset(x = width / 2f, y = height / 2f)

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
                sweepAngle = (360f / maxProgress) * curPercentage.value,
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
                    x = (width - circleRadius * 2f) / 2f,
                    y = (height - circleRadius * 2f) / 2f
                )
            )

            drawContext.canvas.nativeCanvas.apply {
                drawIntoCanvas {
                    drawText(
                        "$currentPosition/$maxProgress",
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

@Composable
fun CircularProgressBarWithOutText(
    modifier: Modifier,
    circleRadius: Float = 16f,
    backgroundColor: Color = Color.Transparent,
    progressBackgroundColor: Color = grayProgressBackground,
    progressColor: Color = blueDark,
    initialPosition: Float = 0f,
    maxProgress: Int = 100,
    borderThickness: Dp = 2.dp,
) {
    var circleCenter by remember {
        mutableStateOf(Offset.Zero)
    }

//    var animationPlayed by remember {
//        mutableStateOf(true)
//    }

    val curPercentage = animateFloatAsState(
        targetValue = initialPosition,
        animationSpec = tween()
    )

//    LaunchedEffect(key1 = initialPosition) {
//        animationPlayed = true
//    }

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val circleThickness = borderThickness//width/30f
            circleCenter = Offset(x = width / 2f, y = height / 2f)

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
                sweepAngle = (360f / maxProgress) * curPercentage.value,
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
                    x = (width - circleRadius * 2f) / 2f,
                    y = (height - circleRadius * 2f) / 2f
                )
            )
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
        initialPosition = 4,
        borderThickness = 10.dp,
        centerTextSize = 15.sp
    )
}

@Preview(showBackground = true)
@Composable
fun Preview2(
    modifier: Modifier = Modifier
) {
    CircularProgressBarWithOutText(
        modifier = modifier,
        initialPosition = 50f
    )
}


@Composable
fun CircularProgressBarWithIcon(
    modifier: Modifier,
    circleRadius: Float = 25f,
    icon: Painter,
    backgroundColor: Color = Color.Transparent,
    progressBackgroundColor: Color = grayProgressBackground,
    progressColor: Color = blueDark,
    initialPosition: Int = 0,
    minProgress: Int = 0,
    maxProgress: Int = 6,
    borderThickness: Dp = 7.dp,
) {
    var circleCenter by remember {
        mutableStateOf(Offset.Zero)
    }

    var animationPlayed by remember {
        mutableStateOf(false)
    }

    val curPercentage = animateFloatAsState(
        targetValue = if (animationPlayed) initialPosition.toFloat() else 0f,
        animationSpec = tween()
    )

    LaunchedEffect(key1 = initialPosition) {
        animationPlayed = true
    }

    Box(
        modifier = Modifier
            .size((circleRadius * 2).dp),
        contentAlignment = Alignment.Center)
    {
        Canvas(modifier = Modifier
            .size((circleRadius * 2).dp)) {
            val width = size.width
            val height = size.height
            val circleThickness = borderThickness//width/30f
            circleCenter = Offset(x = width / 2f, y = height / 2f)

            drawCircle(
                color = backgroundColor,
                radius = circleRadius,
                center = circleCenter
            )

            drawCircle(
                style = Stroke(
                    width = 2f
                ),
                color = progressBackgroundColor,
                radius = circleRadius,
                center = circleCenter
            )

            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = (360f / maxProgress) * curPercentage.value,
                style = Stroke(
                    width = 2f,
                    cap = StrokeCap.Round
                ),
                useCenter = false,
                size = Size(
                    width = circleRadius * 2f,
                    height = circleRadius * 2f
                ),
                topLeft = Offset(
                    x = (width - circleRadius * 2f) / 2f,
                    y = (height - circleRadius * 2f) / 2f
                )
            )

        }
        
        Icon(painter = icon, contentDescription = null)

    }

}

