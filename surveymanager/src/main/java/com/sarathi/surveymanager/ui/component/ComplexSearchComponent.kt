package com.sarathi.surveymanager.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nudge.core.ui.theme.NotoSans
import com.nudge.core.ui.theme.borderGrey
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.placeholderGrey
import com.nudge.core.ui.theme.white
import com.sarathi.surveymanager.R

@Composable
fun ComplexSearchComponent(
    modifier: Modifier = Modifier,
    onSearchScreenActive: () -> Unit
) {
    OutlinedButton(modifier = Modifier.background(color = white, shape = RoundedCornerShape(6.dp)),
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.dp, borderGrey),
        onClick = {
            onSearchScreenActive()
        }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                painter = painterResource(id = R.drawable.icon_search),
                tint = placeholderGrey,
                contentDescription = "seach icon",
                modifier = Modifier.absolutePadding(top = 3.dp)
            )
            Spacer(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(dimen_10_dp)
            )
            Text(
                text = "Search Question",
                style = TextStyle(
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                ), color = placeholderGrey
            )
        }
    }
}