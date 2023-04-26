package com.patsurvey.nudge.activities

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patsurvey.nudge.activities.ui.theme.*
import com.patsurvey.nudge.utils.WealthRank

@Preview
@Composable
fun ParticipatoryWealthRankingSurvey(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .then(modifier)
    ) {

        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Participatory Wealth \nRanking Survey",
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .padding(top = 28.dp),
                style = largeTextStyle,
                color = textColorDark,
                textAlign = TextAlign.Center
            )
        }
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Didis",
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .padding(top = 20.dp, start = 16.dp),
                style = mediumTextStyle,
                fontSize = 16.sp,
                color = textColorDark80,
                textAlign = TextAlign.Start
            )
        }


    }
}


@Composable
fun WealthRankingBox(
    modifier: Modifier = Modifier,
    count: Int,
    wealthRank: WealthRank
) {
    val boxColor = when (wealthRank) {
        WealthRank.RICH -> brownLoght
        WealthRank.MEDIUM -> yellowLight
        WealthRank.POOR -> blueLighter
    }
    val boxTitle = when (wealthRank) {
        WealthRank.RICH -> "Rich"
        WealthRank.MEDIUM -> "Medium"
        WealthRank.POOR -> "Poor"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(boxColor)
            .clip(RoundedCornerShape(6.dp))
            .border(
                width = 1.dp,
                color = brownLight,
                shape = RoundedCornerShape(6.dp)
            )
            .then(modifier)
    ) {
        Row() {
            
        }
    }
}