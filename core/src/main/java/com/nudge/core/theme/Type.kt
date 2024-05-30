package com.nudge.core.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.nudge.core.R

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
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

val veryLargeTextStyle = TextStyle(
    fontFamily = NotoSans,
    fontWeight = FontWeight.SemiBold,
    fontSize = 32.sp
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

val newMediumTextStyle = TextStyle(
    fontFamily = NotoSans,
    fontWeight = FontWeight.SemiBold,
    fontSize = 14.sp
)

val defaultTextStyle = TextStyle(
    fontFamily = NotoSans,
    fontWeight = FontWeight.SemiBold,
    fontSize = 16.sp
)
val quesOptionTextStyle = TextStyle(
    fontFamily = NotoSans,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp
)

val smallTextStyle = TextStyle(
    fontFamily = NotoSans,
    fontWeight = FontWeight.SemiBold,
    fontSize = 14.sp
)

val smallTextStyleWithNormalWeight = TextStyle(
    fontFamily = NotoSans,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp
)

val smallTextStyleMediumWeight2 = TextStyle(
    fontFamily = NotoSans,
    fontWeight = FontWeight.Medium,
    fontSize = 14.sp
)

val smallTextStyleMediumWeight = TextStyle(
    fontFamily = NotoSans,
    fontWeight = FontWeight.Medium,
    fontSize = 15.sp
)

val smallTextStyleNormalWeight = TextStyle(
    fontFamily = NotoSans,
    fontWeight = FontWeight.Normal,
    fontSize = 15.sp
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

val didiDetailItemStyle = TextStyle(
    fontFamily = NotoSans,
    fontWeight = FontWeight.SemiBold,
    color = textColorDark80,
    fontSize = 16.sp
)

val didiDetailLabelStyle = TextStyle(
    fontFamily = NotoSans,
    fontWeight = FontWeight.Normal,
    color = textColorBlueLight,
    fontSize = 16.sp
)
val buttonTextStyle = TextStyle(
    fontFamily = NotoSans,
    fontWeight = FontWeight.SemiBold,
    fontSize = 16.sp
)
val mediumBoldTextStyle = TextStyle(
    fontFamily = NotoSans,
    fontWeight = FontWeight.Bold,
    fontSize = 18.sp
)

val h6 = TextStyle(
    fontFamily = NotoSans,
    fontWeight = FontWeight.Medium,
    fontSize = 15.sp,
    letterSpacing = 0.15.sp
)

val h6Bold = h6.copy(
    fontWeight = FontWeight.SemiBold
)