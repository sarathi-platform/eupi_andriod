package com.nrlm.baselinesurvey.ui.common_components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandIn
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CustomAnimatedVisibilityComponent(
    visible: Boolean,
    modifier: Modifier = Modifier,
    enter: EnterTransition = fadeIn() + expandIn(),
    exit: ExitTransition = shrinkOut() + fadeOut(),
    label: String = "AnimatedVisibility",
    content: @Composable() AnimatedVisibilityScope.() -> Unit

) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = enter,
        exit = exit,
        label = label
    ) {
        content()
    }
}

@Composable
fun VerticalAnimatedVisibilityComponent(
    visible: Boolean,
    modifier: Modifier = Modifier,
    enter: EnterTransition = fadeIn() + expandVertically(),
    exit: ExitTransition = shrinkVertically() + fadeOut(),
    label: String = "AnimatedVisibility",
    content: @Composable() AnimatedVisibilityScope.() -> Unit

) {
    CustomAnimatedVisibilityComponent(
        visible = visible,
        modifier = modifier,
        enter = enter,
        exit = exit,
        label = label
    ) {
        content()
    }
}

@Composable
fun HorizontalAnimatedVisibilityComponent(
    visible: Boolean,
    modifier: Modifier = Modifier,
    enter: EnterTransition = fadeIn() + expandHorizontally(),
    exit: ExitTransition = shrinkHorizontally() + fadeOut(),
    label: String = "AnimatedVisibility",
    content: @Composable() AnimatedVisibilityScope.() -> Unit

) {
    CustomAnimatedVisibilityComponent(
        visible = visible,
        modifier = modifier,
        enter = enter,
        exit = exit,
        label = label
    ) {
        content()
    }
}