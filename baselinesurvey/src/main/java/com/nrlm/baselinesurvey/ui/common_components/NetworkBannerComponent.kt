package com.nrlm.baselinesurvey.ui.common_components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.ui.theme.NotoSans
import com.nrlm.baselinesurvey.ui.theme.greenOnline
import com.nrlm.baselinesurvey.ui.theme.redOffline

@Composable
fun NetworkBanner(
    modifier: Modifier = Modifier,
    isOnline: Boolean = true,
) {
    Column(
        modifier = Modifier
            .then(modifier)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (isOnline) greenOnline else redOffline)
        ) {
            Text(
                text = stringResource(if (isOnline) R.string.online_text else R.string.offline_text),
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                fontFamily = NotoSans,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}