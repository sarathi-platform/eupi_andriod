package com.nrlm.baselinesurvey.ui.common_components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
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
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.ui.theme.NotoSans
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.ui.theme.lightGrayColor

@Preview
@Composable
fun SarathiLogoTextViewComponent() {
    Box(modifier = Modifier
        .background(Color.White)
        .fillMaxWidth()) {

        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            /*Spacer(modifier = Modifier
                .height(12.dp))
            Text(text = "Sarathi", style =mediumTextStyle, color = blueDark)*/
            Image(painter = painterResource(id = R.drawable.sarathi_logo_full),
                contentDescription = null,
                modifier = Modifier.size(width = 120.dp, height = 85.dp).padding(top = 5.dp)
            )

            Spacer(modifier = Modifier
                .height(5.dp))
//
            Spacer(modifier = Modifier
                .width(158.dp)
                .height(1.dp)
                .background(lightGrayColor))

            Spacer(modifier = Modifier
                .height(20.dp))
        }
    }
}