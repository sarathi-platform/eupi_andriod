package com.patsurvey.nudge.customviews

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nrlm.baselinesurvey.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_20_dp
import com.patsurvey.nudge.R

@Preview
@Composable
fun SarathiLogoTextViewV2(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = dimen_20_dp, horizontal = dimen_16_dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimen_20_dp),
            horizontalArrangement = Arrangement.SpaceBetween, // Equal spacing between items
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_loks_aajeevika_logo),
                contentDescription = null,
                modifier = Modifier
            )
            Image(
                painter = painterResource(id = R.drawable.sarathi_logo_full),
                contentDescription = null,
                modifier = Modifier.size(97.dp),
            )
            Image(
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(57.dp),
                painter = painterResource(id = R.drawable.ic_lokos_logo_only),
                contentDescription = "LokOS Logo",
            )
        }
    }
}