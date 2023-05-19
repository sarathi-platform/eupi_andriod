package com.patsurvey.nudge.activities.survey

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patsurvey.nudge.activities.ui.theme.*
import com.patsurvey.nudge.utils.ButtonOutlineWithTopIcon
import com.patsurvey.nudge.R


@Composable
fun YesNoQuestion(
    modifier: Modifier,
    questionNumber: Int,
    question: String,
    answer: Boolean = false,
    answered: Boolean = false,
    onYesClicked: ()-> Unit,
    onNoClicked: ()-> Unit
) {
    /*button width will be the half size of device width, after remove padding(start, end, between)*/
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val buttonWidth = (screenWidth - ((16.dp) * 3)) / 2

    Column(modifier = modifier) {

        Text(
            modifier = Modifier
                .border(
                    BorderStroke(1.dp, lightGray2),
                    shape = RoundedCornerShape(6.dp)
                )
                .padding(14.dp)
                .fillMaxWidth(),
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = textColorBlueLight,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = NotoSans
                    )
                ) {
                    append("$questionNumber.")
                }
                append(" $question")
            },
            style = TextStyle(
                fontFamily = NotoSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            ),
            color = textColorDark
        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
        ) {
            ButtonOutlineWithTopIcon(
                modifier = Modifier.size(buttonWidth, 110.dp),
                buttonTitle = stringResource(id = R.string.option_no),
                textColor = if (isNoSelected(answered, answer)) {
                    white
                } else {
                    textColorDark
                },
                buttonBackgroundColor = if (isNoSelected(answered, answer)) {
                    blueDark
                } else {
                    white
                },
                buttonBorderColor = if (isNoSelected(answered, answer)) {
                    blueDark
                } else {
                    lightGray2
                },
                iconTintColor = if (isNoSelected(answered, answer)) {
                    white
                } else {
                    red
                },
                icon = Icons.Default.Close
            ) {
                onNoClicked()
                isNoSelected(true, false)
            }

            ButtonOutlineWithTopIcon(
                modifier = Modifier.size(buttonWidth, 110.dp),
                buttonTitle = stringResource(id = R.string.option_yes),
                textColor = if (isYesSelected(answered, answer)) {
                    white
                } else {
                    textColorDark
                },
                buttonBackgroundColor = if (isYesSelected(answered, answer)) {
                    blueDark
                } else {
                    white
                },
                buttonBorderColor = if (isYesSelected(answered, answer)) {
                    blueDark
                } else {
                    lightGray2
                },
                iconTintColor = if (isYesSelected(answered, answer)) {
                    white
                } else {
                    greenActiveIcon
                },
                icon = Icons.Default.Check
            ) {
                onYesClicked()
                isYesSelected(true, true)
            }

        }

    }
}

private fun isYesSelected(answered: Boolean, isYes: Boolean): Boolean {
    if(answered) {
        if(isYes) {
            return true
        }
    }
    return false
}

private fun isNoSelected(answered: Boolean, isYes: Boolean): Boolean {
    if(answered) {
        if(!isYes) {
            return true
        }
    }
    return false
}

@Preview(showBackground = true)
@Composable
fun YesNoQuestionPreview() {
    YesNoQuestion(
       modifier = Modifier.padding(16.dp),
        questionNumber = 1,
        question = "This is a sample text. This is an example of adding border to text.",
        answer = false,
        answered = true,
        onNoClicked = {},
        onYesClicked = {}
    )
}
