package com.nrlm.baselinesurvey.ui.common_components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nrlm.baselinesurvey.ui.theme.blueDark

@Composable
fun LoaderComponent(
    modifier: Modifier = Modifier,
    visible: Boolean = true
) {
    AnimatedVisibility(visible = visible, enter = fadeIn(), exit = fadeOut(), modifier = Modifier.then(modifier)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
                .height(48.dp)
        ) {
            CircularProgressIndicator(
                color = blueDark,
                modifier = Modifier
                    .size(28.dp)
                    .align(Alignment.Center)
            )
        }
    }
}