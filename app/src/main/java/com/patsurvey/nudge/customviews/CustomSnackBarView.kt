package com.patsurvey.nudge.customviews

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.smallTextStyleNormalWeight
import com.patsurvey.nudge.utils.SNACKBAR_MESSAGE
import com.patsurvey.nudge.utils.SNACKBAR_TAG
import java.util.Timer
import kotlin.concurrent.schedule

@Composable
fun rememberSnackBarState(): CustomSnackBarViewState {
    return remember { CustomSnackBarViewState() }
}

@Composable
fun SnackBarCustom(
    modifier: Modifier = Modifier,
    state: CustomSnackBarViewState,
    position: CustomSnackBarViewPosition = CustomSnackBarViewPosition.Bottom,
    duration: Long = 3000L,
    icon: Int,
    containerColor: Color = Color.Gray,
    contentColor: Color = TextWhite,
    enterAnimation: EnterTransition = expandVertically(
        animationSpec = tween(delayMillis = 300),
        expandFrom = when(position) {
            is CustomSnackBarViewPosition.Top -> Alignment.Top
            is CustomSnackBarViewPosition.Bottom -> Alignment.Bottom
            is CustomSnackBarViewPosition.Float -> Alignment.CenterVertically
        }
    ),
    exitAnimation: ExitTransition = shrinkVertically(
        animationSpec = tween(delayMillis = 300),
        shrinkTowards =  when(position) {
            is CustomSnackBarViewPosition.Top -> Alignment.Top
            is CustomSnackBarViewPosition.Bottom -> Alignment.Bottom
            is CustomSnackBarViewPosition.Float -> Alignment.CenterVertically
        }
    ),
    verticalPadding: Dp = 12.dp,
    horizontalPadding: Dp = 12.dp
) {
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        CustomSnackBarViewComponent(
            state,
            duration,
            position,
            containerColor,
            contentColor,
            verticalPadding,
            horizontalPadding,
            icon,
            enterAnimation,
            exitAnimation
        )
    }
}

@Composable
fun CustomSnackBarShow(state: CustomSnackBarViewState){
    SnackBarCustom(
        state = state,
        position = CustomSnackBarViewPosition.Top,
        duration = 3000L,
        icon = R.drawable.ic_completed_tick,
        containerColor = Color.Gray,
        contentColor = Color.White,
        enterAnimation = fadeIn(),
        exitAnimation = fadeOut(),
        verticalPadding = 12.dp,
        horizontalPadding = 30.dp
    )
}

@Composable
fun SnackBarError(
    modifier: Modifier = Modifier,
    state: CustomSnackBarViewState,
    position: CustomSnackBarViewPosition = CustomSnackBarViewPosition.Bottom,
    duration: Long = 3000L,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        CustomSnackBarViewComponent(
            state = state,
            duration = duration,
            position = position,
            containerColor = BrightRed,
            contentColor = TextWhite,
            verticalPadding = 12.dp,
            horizontalPadding = 12.dp,
            icon = R.drawable.baseline_error_outline_24,
            enterAnimation = expandVertically(
                animationSpec = tween(delayMillis = 300),
                expandFrom = when(position) {
                    is CustomSnackBarViewPosition.Top -> Alignment.Top
                    is CustomSnackBarViewPosition.Bottom -> Alignment.Bottom
                    is CustomSnackBarViewPosition.Float -> Alignment.CenterVertically
                }
            ),
            exitAnimation = shrinkVertically(
                animationSpec = tween(delayMillis = 300),
                shrinkTowards =  when(position) {
                    is CustomSnackBarViewPosition.Top -> Alignment.Top
                    is CustomSnackBarViewPosition.Bottom -> Alignment.Bottom
                    is CustomSnackBarViewPosition.Float -> Alignment.CenterVertically
                }
            )
        )
    }
}

@Composable
fun SnackBarSuccess(
    modifier: Modifier = Modifier,
    state: CustomSnackBarViewState,
    position: CustomSnackBarViewPosition = CustomSnackBarViewPosition.Top,
    duration: Long = 3000L,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        CustomSnackBarViewComponent(
            state = state,
            duration = duration,
            position = position,
            containerColor = BrightGreen,
            contentColor = TextWhite,
            verticalPadding = 12.dp,
            horizontalPadding = 12.dp,
            icon = R.drawable.ic_completed_tick,
            enterAnimation = expandVertically(
                animationSpec = tween(delayMillis = 300),
                expandFrom = when(position) {
                    is CustomSnackBarViewPosition.Top -> Alignment.Top
                    is CustomSnackBarViewPosition.Bottom -> Alignment.Bottom
                    is CustomSnackBarViewPosition.Float -> Alignment.CenterVertically
                }
            ),
            exitAnimation = shrinkVertically(
                animationSpec = tween(delayMillis = 300),
                shrinkTowards =  when(position) {
                    is CustomSnackBarViewPosition.Top -> Alignment.Top
                    is CustomSnackBarViewPosition.Bottom -> Alignment.Bottom
                    is CustomSnackBarViewPosition.Float -> Alignment.CenterVertically
                }
            )
        )
    }
}


@Composable
internal fun CustomSnackBarViewComponent(
    state: CustomSnackBarViewState,
    duration: Long,
    position: CustomSnackBarViewPosition,
    containerColor: Color,
    contentColor: Color,
    verticalPadding: Dp,
    horizontalPadding: Dp,
    icon: Int,
    enterAnimation: EnterTransition,
    exitAnimation: ExitTransition,
) {
    var showSnackBar by remember { mutableStateOf(false) }
    val message by rememberUpdatedState(newValue = state.message.value)
    val messageIcon by rememberUpdatedState(newValue = state.messageIcon.value)
    val isSuccess by rememberUpdatedState(newValue = state.isSuccess.value)
    val isCustomIcon by rememberUpdatedState(newValue = state.isCustomIcon.value)

    DisposableEffect(
        key1 = state.updateState
    ) {
        showSnackBar = true
        val timer = Timer("Animation Timer", true)
        timer.schedule(duration) {
            showSnackBar = false
        }
        onDispose {
            timer.cancel()
            timer.purge()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                bottom = when (position) {
                    is CustomSnackBarViewPosition.Top -> 0.dp
                    is CustomSnackBarViewPosition.Bottom -> 0.dp
                    is CustomSnackBarViewPosition.Float -> 24.dp
                }
            ),
        verticalArrangement = when(position) {
            is CustomSnackBarViewPosition.Top -> Arrangement.Top
            is CustomSnackBarViewPosition.Bottom -> Arrangement.Bottom
            is CustomSnackBarViewPosition.Float -> Arrangement.Bottom
        },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(
            visible = state.isNotEmpty() && showSnackBar,
            enter = when(position) {
                is CustomSnackBarViewPosition.Top -> enterAnimation
                is CustomSnackBarViewPosition.Bottom -> enterAnimation
                is CustomSnackBarViewPosition.Float -> fadeIn()
            },
            exit = when(position) {
                is CustomSnackBarViewPosition.Top -> exitAnimation
                is CustomSnackBarViewPosition.Bottom -> exitAnimation
                is CustomSnackBarViewPosition.Float -> fadeOut()
            }
        ) {
            SnackBarView(
                message,
                position,
                containerColor,
                contentColor,
                verticalPadding,
                horizontalPadding,
                messageIcon ?: R.drawable.ic_completed_tick,
                isCustomIcon,
                isSuccess

            )
        }
    }
}



@Composable
internal fun SnackBarView(
    message: String?,
    position: CustomSnackBarViewPosition,
    containerColor: Color,
    contentColor: Color,
    verticalPadding: Dp,
    horizontalPadding: Dp,
    icon: Int,
    isCustomIcon: Boolean,
    isSuccess: Boolean,
) {

    Row(modifier = Modifier.
    padding(top = 90.dp)) {
    Row(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = Color.White,
                shape = RoundedCornerShape(6.dp)
            )
            .shadow(
                elevation = 16.dp,
                ambientColor = Color.White,
                spotColor = Color.Black,
                shape = RoundedCornerShape(6.dp),
            )
            .background(
                color = Color.White,
                shape = when (position) {
                    is CustomSnackBarViewPosition.Top -> RectangleShape
                    is CustomSnackBarViewPosition.Bottom -> RectangleShape
                    is CustomSnackBarViewPosition.Float -> RoundedCornerShape(8.dp)
                }
            )
            .padding(vertical = verticalPadding)
            .padding(horizontal = horizontalPadding)
            .animateContentSize()
            .testTag(SNACKBAR_TAG),
        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(if(isCustomIcon) icon else {
                    if(isSuccess) R.drawable.ic_completed_tick else R.drawable.baseline_error_outline_24
                }),
                contentDescription = "completed",
                modifier = Modifier
                    .padding(top = dimensionResource(id = R.dimen.dp_5))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                modifier = Modifier.testTag(SNACKBAR_MESSAGE),
                text = message ?: "Unknown",
                color = Color.Black,
                style = smallTextStyleNormalWeight,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    }

    }

}



val BrightGreen = Color(0xFF19B661)
val BrightRed = Color(0xFFE8503A)
val TextWhite = Color(0xFFEEEEEE)