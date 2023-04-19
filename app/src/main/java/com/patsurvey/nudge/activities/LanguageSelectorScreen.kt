package com.patsurvey.nudge.activities

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.*

@Composable
fun LanguageScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .background(color = Color.White)
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 32.dp)
            .then(modifier)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Choose Language",
                color = textColorBlueLight,
                fontSize = 18.sp,
                fontFamily = NotoSans,
                fontWeight = FontWeight.SemiBold,

                )
            Spacer(modifier = Modifier.height(25.dp))
            Column(
            ) {
                LanguageListItem(language = "English")
                LanguageListItem(language = stringResource(id = R.string.hindi), isActive = true)
                LanguageListItem(language = stringResource(id = R.string.gujrati))
                LanguageListItem(language = stringResource(id = R.string.kannada))

            }
        }

        Button(
            onClick = {
                      //TODO//
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
                .align(Alignment.BottomCenter),
            colors = ButtonDefaults.buttonColors(blueDark),
            shape = RoundedCornerShape(6.dp)
        ) {
            Text(
                text = "Continue",
                color = Color.White,
                fontSize = 18.sp,
                fontFamily = NotoSans,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .background(blueDark)
            )
        }
    }
}

@Composable
fun LanguageListItem(
    language: String,
    modifier: Modifier = Modifier,
    isActive: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 0.dp)
            .clip(RoundedCornerShape(6.dp))
            .border(
                width = 1.dp,
                color = if (isActive) languageItemActiveBorderBg else languageItemInActiveBorderBg,
                shape = RoundedCornerShape(6.dp)
            )
            .background(if (isActive) languageItemActiveBg else Color.White)
            .padding(vertical = 20.dp, horizontal = 0.dp)
            .clickable {

            }
            .then(modifier)
    ) {
        Text(
            text = language,
            color = blueDark,
            fontSize = 18.sp,
            fontFamily = NotoSans,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.align(Alignment.Center)
        )
    }

}