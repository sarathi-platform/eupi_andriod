package com.sarathi.surveymanager.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.nudge.core.ui.events.theme.NotoSans
import com.nudge.core.ui.events.theme.red
import com.nudge.core.ui.events.theme.textColorDark

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun QuestionComponent(isRequiredField: Boolean = true, title: String = "") {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(modifier = Modifier.fillMaxWidth(.9f),
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = textColorDark,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = NotoSans
                    )
                ) {
                    append(title)
                }
                if (isRequiredField) {
                    withStyle(
                        style = SpanStyle(
                            color = red,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = NotoSans
                        )
                    ) {
                        append("*")
                    }
                }
            }
        )
    }
}