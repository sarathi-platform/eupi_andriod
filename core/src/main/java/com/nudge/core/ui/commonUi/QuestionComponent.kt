package com.nudge.core.ui.commonUi

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.nudge.core.BLANK_STRING
import com.nudge.core.ui.theme.NotoSans
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.dimen_4_dp
import com.nudge.core.ui.theme.grayColor
import com.nudge.core.ui.theme.newMediumTextStyle
import com.nudge.core.ui.theme.red

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun QuestionComponent(
    isRequiredField: Boolean = true,
    title: String = BLANK_STRING,
    subTitle: String = BLANK_STRING
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimen_4_dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(modifier = Modifier.fillMaxWidth(.9f),
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = blueDark,
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
        if (subTitle.isNotBlank()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = subTitle,
                    style = newMediumTextStyle.copy(color = grayColor)
                )
            }
        }

    }

}