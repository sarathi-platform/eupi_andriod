package com.patsurvey.nudge.customviews

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nudge.core.ui.theme.dimen_10_dp
import com.patsurvey.nudge.R

@Preview
@Composable
fun SarathiLogoTextViewV2() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimen_10_dp)
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.sarathi_logo_full),
                contentDescription = null,
                modifier = Modifier
                    .size(width = 90.dp, height = 65.dp)
                    .padding(bottom = 5.dp)
            )

            Image(
                modifier = Modifier.padding(bottom = 12.dp),
                painter = painterResource(id = R.drawable.ic_loks_icon),
                contentDescription = "LokOS Logo",
            )


        }
    }
}