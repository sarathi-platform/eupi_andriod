package com.nudge.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.nudge.core.ui.commonUi.shimmer
import com.nudge.core.ui.theme.dimen_100_dp
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_16_dp

@Composable
fun ShowLoadingEffect(repeatTime: Int = 4) {
    Column {

        repeat(repeatTime) {
            Box(
                modifier = Modifier
                    .padding(vertical = dimen_10_dp, horizontal = dimen_10_dp)
                    .shimmer()
                    .fillMaxWidth()
                    .height(dimen_100_dp)
                    .padding(dimen_16_dp)
                    .background(Color.LightGray, RoundedCornerShape(dimen_10_dp))
            )
        }
    }
}