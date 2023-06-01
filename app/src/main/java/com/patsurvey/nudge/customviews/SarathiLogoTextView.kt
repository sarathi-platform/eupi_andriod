package com.patsurvey.nudge.customviews

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.activities.ui.theme.lightGrayColor
@Preview
@Composable
fun SarathiLogoTextView() {
    Box(modifier = Modifier
        .background(Color.White)
        .fillMaxWidth()) {

        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            /*Spacer(modifier = Modifier
                .height(12.dp))
            Text(text = "Sarathi", style =mediumTextStyle, color = blueDark)*/
            Image(painter = painterResource(id = R.drawable.sarathi_logo_full),
                contentDescription = null,
                modifier = Modifier.size(width = 90.dp, height = 65.dp)
            )
            Text(text = "To End Ultra Poverty", style = TextStyle(
                fontFamily = NotoSans,
                fontWeight = FontWeight.Normal,
                fontSize = 8.sp
            ), color = blueDark, modifier = Modifier.padding(bottom = 12.dp))
            Spacer(modifier = Modifier
                .width(158.dp)
                .height(1.dp)
                .background(lightGrayColor))

            Spacer(modifier = Modifier
                .height(20.dp))
        }
    }
}