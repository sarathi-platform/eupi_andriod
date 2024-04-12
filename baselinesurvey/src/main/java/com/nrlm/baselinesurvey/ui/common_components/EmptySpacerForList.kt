package com.nrlm.baselinesurvey.ui.common_components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier

fun LazyListScope.emptySpacer(modifier: Modifier) {
    item {
        Spacer(modifier = modifier)
    }
}

