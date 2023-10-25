package com.nrlm.baselinesurvey.ui.common_components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nrlm.baselinesurvey.model.datamodel.OptionsItem
import com.nrlm.baselinesurvey.model.datamodel.QuestionEntity
import com.nrlm.baselinesurvey.ui.theme.defaultTextStyle
import com.nrlm.baselinesurvey.ui.theme.defaultCardElevation
import com.nrlm.baselinesurvey.ui.theme.roundedCornerRadiusDefault
import com.nrlm.baselinesurvey.ui.theme.textColorDark

@Composable
fun RadioQuestionBoxComponent(
    modifier: Modifier = Modifier,
    index: Int,
    question: QuestionEntity
) {

    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = defaultCardElevation
        ),
        shape = RoundedCornerShape(roundedCornerRadiusDefault),
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                //Handle Click if any
            }
            .then(modifier)
    ) {
        Column {
            Text(text = "${index + 1}. ${question.questionDisplay}", style = defaultTextStyle, color = textColorDark)
        }
    }

}


@Preview(showSystemUi = true, showBackground = true)
@Composable
fun RadioQuestionBoxComponentPreview(
    modifier: Modifier = Modifier,

) {
    val question = QuestionEntity(
        id = 1,
        questionId = 1,
        questionDisplay = "Did everyone in your family have at least 2 meals per day in the last 1 month?",
        questionSummary = "Did everyone in your family have at least 2 meals per day in the last 1 month?",
        order = 1,
        type = "RadioButton",
        gotoQuestionId = 2,
        options = listOf(
            OptionsItem(
                optionId = 1,
                display = "YES",
                weight = 1,
                summary = "YES",
                optionValue = 1,
                optionImage = "",
                optionType = ""
            ),
            OptionsItem(
                optionId = 2,
                display = "NO",
                weight = 0,
                summary = "NO",
                optionValue = 0,
                optionImage = "",
                optionType = ""
            )
        ),
        questionImageUrl = "Section1_GovtService.webp",
    )
    Surface {
        Column (Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
            RadioQuestionBoxComponent(index = 1, question = question)
        }
    }
}