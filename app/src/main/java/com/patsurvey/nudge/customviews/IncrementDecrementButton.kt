package com.patsurvey.nudge.customviews

import android.annotation.SuppressLint
import android.graphics.Typeface
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.utils.AnimationType

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun IncrementDecrementButton1(modifier: Modifier = Modifier,
                             animationType: AnimationType = AnimationType.FADE,
                             value: Int = 0,
                             onDecrementClick: (Int) -> Unit = {},
                             onIncrementClick: (Int) -> Unit = {},
                             onMiddleClick: (Int) -> Unit = {},
                             decrementComposable: @Composable (cb: (Int) -> Unit) -> Unit = { cb ->
                                  DefaultDecrementComposable(
                                      modifier = modifier,
                                      onDecrementClick = { cb(-1) }
                                  )
                              },
                             incrementComposable: @Composable (cb: (Int) -> Unit) -> Unit = { cb ->
                                  DefaultIncrementComposable(
                                      modifier = modifier,
                                      onIncrementClick = { cb(-1) }
                                  )
                              },
                             middleComposable: @Composable (Int, cb: (Int) -> Unit) -> Unit = { buttonValue, cb ->
                                  DefaultMiddleComposable(
                                      modifier = modifier.wrapContentWidth(),
                                      onMiddleClick = { cb(-1) },
                                      value = buttonValue
                                  )
                              },
) {
    var buttonValue by rememberSaveable { mutableStateOf(value) }
    var isDecrement by rememberSaveable { mutableStateOf(false) }

    Row {
        decrementComposable {
            isDecrement = true
            if (buttonValue <= 0)
                buttonValue = 0
            else
                buttonValue--
            onDecrementClick(buttonValue)
        }
        AnimatedContent(
            targetState = buttonValue,
            transitionSpec = {
                getAnimationSpec(animationType, isDecrement)
            }
        ) { value ->
            middleComposable(value) {
                isDecrement = false
                buttonValue++
                onMiddleClick(value)
            }
        }
        incrementComposable {
            isDecrement = false
            buttonValue++
            onIncrementClick(buttonValue)
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun IncrementDecrementButton(modifier: Modifier = Modifier,
                              animationType: AnimationType = AnimationType.FADE,
                              value: Int = 0,
                              onDecrementClick: (Int) -> Unit = {},
                              onIncrementClick: (Int) -> Unit = {},
                              onMiddleClick: (Int) -> Unit = {},
                              decrementComposable: @Composable (cb: (Int) -> Unit) -> Unit = { cb ->
                                  DefaultDecrementComposable(
                                      modifier = modifier,
                                      onDecrementClick = { cb(-1) }
                                  )
                              },
                              incrementComposable: @Composable (cb: (Int) -> Unit) -> Unit = { cb ->
                                  DefaultIncrementComposable(
                                      modifier = modifier,
                                      onIncrementClick = { cb(-1) }
                                  )
                              },
                              middleComposable: @Composable (Int, cb: (Int) -> Unit) -> Unit = { buttonValue, cb ->
                                  DefaultMiddleComposable(
                                      modifier = modifier,
                                      onMiddleClick = { cb(-1) },
                                      value = buttonValue
                                  )
                              },
) {
    var buttonValue by rememberSaveable { mutableStateOf(value) }
    var isDecrement by rememberSaveable { mutableStateOf(false) }

    Row(modifier = Modifier.fillMaxWidth()) {
        decrementComposable {
            isDecrement = true
            if (buttonValue <= 0)
                buttonValue = 0
            else
                buttonValue--
            onDecrementClick(buttonValue)
        }
        AnimatedContent(
            targetState = buttonValue,
            transitionSpec = {
                getAnimationSpec(animationType, isDecrement)
            }
        ) { value ->
            middleComposable(value) {
                isDecrement = false
                buttonValue++
                onMiddleClick(value)
            }
        }
        incrementComposable {
            isDecrement = false
            buttonValue++
            onIncrementClick(buttonValue)
        }
    }
}


@OptIn(ExperimentalAnimationApi::class)
fun getAnimationSpec(
    animationType: AnimationType,
    isDecrement: Boolean
): ContentTransform {
    val inverseConstant = if (isDecrement) -1 else 1
    return when (animationType) {
        AnimationType.FADE -> {
            fadeIn() with fadeOut()
        }
        AnimationType.VERTICAL -> {
            slideInVertically {
                inverseConstant * it
            } with slideOutVertically { inverseConstant * -it }
        }
        else -> {
            slideInHorizontally { inverseConstant * it } with slideOutHorizontally { inverseConstant * -it }
        }
    }
}


@Composable
fun DefaultDecrementComposable(
    modifier: Modifier,
    onDecrementClick: () -> Unit
) {
    Text(
        text = "-",
        color = Color.Black,
        fontFamily = NotoSans,
        fontSize = 16.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.clickable {
            onDecrementClick()
        }
    )
}

@Composable
fun DefaultIncrementComposable(
    modifier: Modifier = Modifier,
    onIncrementClick: () -> Unit
) {
    val buttonModifier = modifier
        .clickable {
            onIncrementClick()
        }
    Text(
        text = "+",
        color = Color.Black,
        fontFamily = NotoSans,
        fontSize = 16.sp,
        textAlign = TextAlign.Center,
        modifier = buttonModifier
    )

}

@Composable
fun DefaultMiddleComposable(
    modifier: Modifier = Modifier,
    value: Int,
    onMiddleClick: () -> Unit
) {

    val buttonModifier = modifier
        .clickable {
            onMiddleClick()
        }



    Text(
        text = value.toString()
            .toUpperCase(
                Locale.current
            ),
        color = Color.Black,
        fontFamily = NotoSans,
        fontSize = 16.sp,
        textAlign = TextAlign.Center,
        modifier = buttonModifier
    )
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Preview
@Composable
fun IncrementDecrementButtonPreview() {
    Scaffold {
        IncrementDecrementButton()
    }
}
