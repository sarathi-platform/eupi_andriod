package com.patsurvey.nudge.activities.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButtonElevation
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.activities.ui.theme.languageItemActiveBg

@Composable
fun CustomFloatingButton(modifier: Modifier = Modifier,buttonTitle:String, isNext:Boolean,onClick: () -> Unit) {

    val interactionSource = remember { MutableInteractionSource() }
    val elevation = CustomElevation()
        .elevation(interactionSource = interactionSource).value
    Card(
        elevation = elevation,
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier
            .width(80.dp)
            .height(50.dp)
            .background(color = languageItemActiveBg)
            .pointerInput(true) {
                detectTapGestures(onTap = {
                    onClick()
                },
                    onPress = {},
                    onLongPress = {},
                    onDoubleTap = {})
            }
            .then(modifier)
    ) {
        Box(
            modifier = Modifier
                .defaultMinSize(minWidth = FabSize, minHeight = FabSize)
                .background(languageItemActiveBg),
            contentAlignment = Alignment.Center
        ) {
            val startPadding = if (!isNext) ExtendedFabIconPadding else ExtendedFabTextPadding
            val endPadding = if (isNext) ExtendedFabIconPadding else ExtendedFabTextPadding
            Row(
                modifier = Modifier.padding(
                    start = startPadding,
                    end = endPadding
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isNext) {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_arrow_back),
                        contentDescription = "Negative Button",
                        modifier = Modifier
                            .height(20.dp)
                            .absolutePadding(top = 2.dp),
                        colorFilter = ColorFilter.tint(blueDark)
                    )
                }
                Text(
                    text = buttonTitle,
                    color = blueDark,
                    style = TextStyle(
                        fontFamily = NotoSans,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Start
                    )
                )
                if (isNext) {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_arrow_forward_ios_24),
                        contentDescription = "Negative Button",
                        modifier = Modifier
                            .height(20.dp)
                            .absolutePadding(top = 2.dp),
                        colorFilter = ColorFilter.tint(blueDark)
                    )
                }
            }
        }
    }
}

@Composable
fun CustomElevation(
    defaultElevation: Dp = 10.dp,
    pressedElevation: Dp = 12.dp,
    hoveredElevation: Dp = 8.dp,
    focusedElevation: Dp = 8.dp,
): FloatingActionButtonElevation {
    return remember(defaultElevation, pressedElevation, hoveredElevation, focusedElevation) {
        CustomDefaultFloatingActionButtonElevation(
            defaultElevation = defaultElevation,
            pressedElevation = pressedElevation,
            hoveredElevation = hoveredElevation,
            focusedElevation = focusedElevation
        )
    }
}


private class CustomDefaultFloatingActionButtonElevation(
    private val defaultElevation: Dp,
    private val pressedElevation: Dp,
    private val hoveredElevation: Dp,
    private val focusedElevation: Dp
) : FloatingActionButtonElevation {
    @Composable
    override fun elevation(interactionSource: InteractionSource): State<Dp> {
        val interactions = remember { mutableStateListOf<Interaction>() }
        LaunchedEffect(interactionSource) {
            interactionSource.interactions.collect { interaction ->
                when (interaction) {
                    is HoverInteraction.Enter -> {
                        interactions.add(interaction)
                    }
                    is HoverInteraction.Exit -> {
                        interactions.remove(interaction.enter)
                    }
                    is FocusInteraction.Focus -> {
                        interactions.add(interaction)
                    }
                    is FocusInteraction.Unfocus -> {
                        interactions.remove(interaction.focus)
                    }
                    is PressInteraction.Press -> {
                        interactions.add(interaction)
                    }
                    is PressInteraction.Release -> {
                        interactions.remove(interaction.press)
                    }
                    is PressInteraction.Cancel -> {
                        interactions.remove(interaction.press)
                    }
                }
            }
        }

        val interaction = interactions.lastOrNull()

        val target = when (interaction) {
            is PressInteraction.Press -> pressedElevation
            is HoverInteraction.Enter -> hoveredElevation
            is FocusInteraction.Focus -> focusedElevation
            else -> defaultElevation
        }

        val animatable = remember { Animatable(target, Dp.VectorConverter) }

        LaunchedEffect(target) {
            val lastInteraction = when (animatable.targetValue) {
                pressedElevation -> PressInteraction.Press(Offset.Zero)
                hoveredElevation -> HoverInteraction.Enter()
                focusedElevation -> FocusInteraction.Focus()
                else -> null
            }
            animatable.animateCustomElevation(
                from = lastInteraction,
                to = interaction,
                target = target
            )
        }

        return animatable.asState()
    }
}

internal suspend fun Animatable<Dp, *>.animateCustomElevation(
    target: Dp,
    from: Interaction? = null,
    to: Interaction? = null
) {
    val spec = when {
        // Moving to a new state
        to != null -> CustomElevationDefaults.incomingAnimationSpecForInteraction(to)
        // Moving to default, from a previous state
        from != null -> CustomElevationDefaults.outgoingAnimationSpecForInteraction(from)
        // Loading the initial state, or moving back to the baseline state from a disabled /
        // unknown state, so just snap to the final value.
        else -> null
    }
    if (spec != null) animateTo(target, spec) else snapTo(target)
}

object CustomElevationDefaults {
    /**
     * Returns the [AnimationSpec]s used when animating elevation to [interaction], either from a
     * previous [Interaction], or from the default state. If [interaction] is unknown, then
     * returns `null`.
     *
     * @param interaction the [Interaction] that is being animated to
     */
    fun incomingAnimationSpecForInteraction(interaction: Interaction): AnimationSpec<Dp>? {
        return when (interaction) {
            is PressInteraction.Press -> DefaultIncomingSpec
            is DragInteraction.Start -> DefaultIncomingSpec
            is HoverInteraction.Enter -> DefaultIncomingSpec
            is FocusInteraction.Focus -> DefaultIncomingSpec
            else -> null
        }
    }

    /**
     * Returns the [AnimationSpec]s used when animating elevation away from [interaction], to the
     * default state. If [interaction] is unknown, then returns `null`.
     *
     * @param interaction the [Interaction] that is being animated away from
     */
    fun outgoingAnimationSpecForInteraction(interaction: Interaction): AnimationSpec<Dp>? {
        return when (interaction) {
            is PressInteraction.Press -> DefaultOutgoingSpec
            is DragInteraction.Start -> DefaultOutgoingSpec
            is HoverInteraction.Enter -> HoveredOutgoingSpec
            is FocusInteraction.Focus -> DefaultOutgoingSpec
            else -> null
        }
    }
}

private val DefaultIncomingSpec = TweenSpec<Dp>(
    durationMillis = 120,
    easing = FastOutSlowInEasing
)

private val DefaultOutgoingSpec = TweenSpec<Dp>(
    durationMillis = 150,
    easing = CubicBezierEasing(0.40f, 0.00f, 0.60f, 1.00f)
)

private val HoveredOutgoingSpec = TweenSpec<Dp>(
    durationMillis = 120,
    easing = CubicBezierEasing(0.40f, 0.00f, 0.60f, 1.00f)
)


private val FabSize = 56.dp
private val ExtendedFabSize = 48.dp
private val ExtendedFabIconPadding = 12.dp
private val ExtendedFabTextPadding = 20.dp



@Preview(showBackground = true)
@Composable
fun prevFloatButtonPreview(){
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(bottom = 40.dp)) {
        CustomFloatingButton(Modifier, "Q12",true) {}
    }
}