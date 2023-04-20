package com.patsurvey.nudge.activities

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.activities.ui.theme.languageItemActiveBg
import com.patsurvey.nudge.activities.ui.theme.languageItemInActiveBorderBg

@Composable
fun BlueButton(
    buttonText: String,
    isArrowRequired: Boolean,
    shouldBeActive: Boolean = false,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = { },
        modifier = Modifier
            .padding(vertical = 14.dp)
            .background(Color.Transparent)
            .width(180.dp)
            .then(modifier),
        colors = ButtonDefaults.buttonColors(if (shouldBeActive) blueDark else languageItemActiveBg),
        shape = RoundedCornerShape(6.dp)

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = buttonText,
                color = if (shouldBeActive) Color.White else languageItemInActiveBorderBg,
                fontSize = 18.sp,
                fontFamily = NotoSans,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier/*.align(Alignment.Center)*/
            )
            if (isArrowRequired) {
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = "Forward arrow",
                    tint = if (shouldBeActive) Color.White else languageItemInActiveBorderBg,
                    modifier = Modifier.absolutePadding(top = 4.dp)

                )
            }
        }
    }
}

@Composable
fun ButtonPositive(
    modifier: Modifier = Modifier,
    isArrowRequired: Boolean = true
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(blueDark)
//            .width(200.dp)
            .clickable {
                //Click action
            }
            .then(modifier),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            Modifier
                .padding(14.dp)
                .fillMaxWidth()
                .align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Mark Complete",
                fontFamily = NotoSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = Color.White
            )
            Icon(
                Icons.Default.ArrowForward,
                contentDescription = "Positive Button",
                tint = Color.White,
                modifier = Modifier
                    .absolutePadding(top = 4.dp)
            )
        }
    }
}

@Composable
fun ButtonNegative(
    alpha: Float = 1.0f,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(languageItemActiveBg)
            .clickable {
                //Click action
            }
            .padding(horizontal = 10.dp)
            .then(modifier),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.baseline_arrow_back),
                contentDescription = "Negative Button",
                modifier = Modifier
                    .absolutePadding(top = 2.dp),
                colorFilter = ColorFilter.tint(blueDark)
            )
            Text(
                text = "Go Back",
                fontFamily = NotoSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = blueDark
            )
        }
    }
}