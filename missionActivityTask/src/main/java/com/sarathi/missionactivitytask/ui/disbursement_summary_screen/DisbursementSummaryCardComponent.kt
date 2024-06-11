package com.sarathi.missionactivitytask.ui.disbursement_summary_screen

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.nudge.core.ui.events.theme.black20
import com.nudge.core.ui.events.theme.brownDark
import com.nudge.core.ui.events.theme.defaultTextStyle
import com.nudge.core.ui.events.theme.dimen_10_dp
import com.nudge.core.ui.events.theme.dimen_16_dp
import com.nudge.core.ui.events.theme.dimen_1_dp
import com.nudge.core.ui.events.theme.dimen_4_dp
import com.nudge.core.ui.events.theme.dimen_6_dp
import com.nudge.core.ui.events.theme.dimen_8_dp
import com.nudge.core.ui.events.theme.smallTextStyle
import com.nudge.core.ui.events.theme.white
import com.sarathi.missionactivitytask.ui.components.CircularImageViewComponent


@Preview(showSystemUi = true)
@Composable
fun DisbursementSummaryCardComponent() {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = dimen_10_dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimen_16_dp)
            .clip(RoundedCornerShape(dimen_6_dp))
            .border(
                width = dimen_1_dp,
                color = black20,
                shape = RoundedCornerShape(dimen_6_dp)
            )
            .background(Color.Transparent)
    ) {
        Column(modifier = Modifier.background(white)) {
            makeRow(text1 = "VO - Ganbari Shikla", text2 = "15 Apr, 2024")
            makeRow(text1 = "CSG Disbursed", text2 = "₹36,000")
            makeRow(text1 = "Number of Didis", text2 = "4")
            Divider(
                color = Color.Gray,
                thickness = dimen_1_dp,
                modifier = Modifier.padding(vertical = dimen_8_dp)
            )
            makeTaskCard()
        }

    }
}

@Composable
private fun makeRow(text1: String, text2: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimen_6_dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text1)
        Text(text2)
    }
}


@Composable
private fun makeTaskCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(white)
            .padding(dimen_10_dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimen_4_dp),
            horizontalArrangement = Arrangement.spacedBy(dimen_10_dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularImageViewComponent(modifier = Modifier, Uri.EMPTY)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Shanti Devi",
                    style = defaultTextStyle,
                    color = brownDark
                )
                Text(
                    text = "Tola: A",
                    style = smallTextStyle,
                    color = brownDark
                )
            }

        }
        Column(modifier = Modifier.padding(start = dimen_10_dp, end = dimen_10_dp)) {
            makeRow(text1 = "Mode: Kind", text2 = "Nature: BYP")
            makeRow(text1 = "Amount: ₹500", text2 = "")
        }

    }

}