package com.patsurvey.nudge.activities.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.patsurvey.nudge.R

// Set of Material typography styles to start with

val Typography = Typography(
    body1 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    )
)

val NotoSans = FontFamily(
    Font(R.font.noto_sans_regular, FontWeight.Normal),
    Font(R.font.noto_sans_bold, FontWeight.Bold),
    Font(R.font.noto_sans_black, FontWeight.Black),
    Font(R.font.noto_sans_semi_bold, FontWeight.SemiBold),
    Font(R.font.noto_sans_medium, FontWeight.Medium),
    Font(R.font.noto_sans_thin, FontWeight.Thin),
)

val largeTextStyle = TextStyle(
    fontFamily = NotoSans,
    fontWeight = FontWeight.SemiBold,
    fontSize = 20.sp
)

val mediumTextStyle = TextStyle(
    fontFamily = NotoSans,
    fontWeight = FontWeight.SemiBold,
    fontSize = 18.sp
)

val smallTextStyle = TextStyle(
    fontFamily = NotoSans,
    fontWeight = FontWeight.SemiBold,
    fontSize = 14.sp
)

val smallerTextStyle = TextStyle(
    fontFamily = NotoSans,
    fontWeight = FontWeight.SemiBold,
    fontSize = 12.sp
)

val smallerTextStyleNormalWeight = TextStyle(
    fontFamily = NotoSans,
    fontWeight = FontWeight.Normal,
    fontSize = 12.sp
)

val smallestTextStyle = TextStyle(
    fontFamily = NotoSans,
    fontWeight = FontWeight.SemiBold,
    fontSize = 10.sp
)