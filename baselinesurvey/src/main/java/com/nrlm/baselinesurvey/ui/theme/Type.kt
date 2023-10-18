package com.nrlm.baselinesurvey.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.nrlm.baselinesurvey.R

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