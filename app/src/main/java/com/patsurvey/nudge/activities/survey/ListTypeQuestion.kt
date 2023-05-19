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
import com.patsurvey.nudge.model.dataModel.AnswerOptionModel


@Composable
fun ListTypeQuestion(
    modifier: Modifier,
    questionNumber: Int,
    question: String,
) {
    val optionList= mutableListOf<AnswerOptionModel>()
    for (i in 1..5){
        optionList.add(AnswerOptionModel(i,"Option Value $i",false))
    }
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



        }

    }
}

@Preview(showBackground = true)
@Composable
fun ListTypeQuestionPreview() {
    ListTypeQuestion(
       modifier = Modifier.padding(16.dp),
        questionNumber = 1,
        question = "This is a sample text. This is an example of adding border to text."
    )
}
