package com.nrlm.baselinesurvey.ui.common_components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.model.datamodel.OptionsItem
import com.nrlm.baselinesurvey.model.datamodel.QuestionEntity
import com.nrlm.baselinesurvey.ui.theme.NotoSans
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.ui.theme.defaultCardElevation
import com.nrlm.baselinesurvey.ui.theme.dimen_16_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_18_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_1_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_5_dp
import com.nrlm.baselinesurvey.ui.theme.languageItemActiveBg
import com.nrlm.baselinesurvey.ui.theme.lightGray2
import com.nrlm.baselinesurvey.ui.theme.redOffline
import com.nrlm.baselinesurvey.ui.theme.roundedCornerRadiusDefault
import com.nrlm.baselinesurvey.ui.theme.textColorDark
import com.nrlm.baselinesurvey.ui.theme.white
import com.patsurvey.nudge.customviews.htmltext.HtmlText

@Composable
fun GridTypeComponent(
    modifier: Modifier = Modifier,
    question: QuestionEntity,
    index: Int,
    isAnswerSelected: Boolean = false,
    onAnswerSelection: (Int) -> Unit,
    questionDetailExpanded: (index: Int) -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = defaultCardElevation
        ),
        shape = RoundedCornerShape(roundedCornerRadiusDefault),
        modifier = Modifier
            .fillMaxWidth()
            .background(white)
            .clickable {
            }
            .then(modifier)
    ) {
        Column(modifier = Modifier.background(white)) {
            Column(
                Modifier.padding(vertical = dimen_16_dp, horizontal = dimen_16_dp),
                verticalArrangement = Arrangement.spacedBy(
                    dimen_18_dp
                )
            ) {
                Row {
                    HtmlText(
                        text = "${question.questionId} .",
                        style = TextStyle(
                            fontFamily = NotoSans,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = textColorDark
                        ),
                    )

                    HtmlText(
                        text = "${question.questionDisplay}",
                        style = TextStyle(
                            fontFamily = NotoSans,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = textColorDark
                        ),
                    )
                }
                LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                    itemsIndexed(question.options?.sortedBy { it.optionValue }
                        ?: emptyList()) { index, option ->
                        OptionCard(
                            buttonTitle = option.display ?: BLANK_STRING,
                            index = index,
                            selectedIndex = index
                        ) {
                            if (!isAnswerSelected)
                                onAnswerSelection(index)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    item {
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 100.dp)
                        )
                    }
                }
            }
            Divider(thickness = dimen_1_dp, color = lightGray2, modifier = Modifier.fillMaxWidth())
            InfoComponent(questionDetailExpanded, index, question)
        }
    }

}

@Composable
private fun OptionCard(
    modifier: Modifier = Modifier,
    buttonTitle: String,
    index: Int,
    selectedIndex: Int,
    onOptionSelected: (Int) -> Unit
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = dimen_5_dp, vertical = dimen_5_dp)
        .clip(RoundedCornerShape(6.dp))
        .background(if (selectedIndex == index) blueDark else languageItemActiveBg)
        .clickable {
            onOptionSelected(index)
        }
        .then(modifier)) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HtmlText(
                    text = buttonTitle,
                    style = TextStyle(
                        color = if (selectedIndex == index) Color.White else redOffline,
                        fontFamily = NotoSans,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                    )
                )
            }
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
        )
    }

}

@Preview(showBackground = true)
@Composable
fun GridTypeQuestionPreview() {
    val optionList = mutableListOf<OptionsItem>()
    for (i in 1..5) {
        optionList.add(OptionsItem("Option Value $i", i + 1, i, 1, "Summery"))
    }

    val question = QuestionEntity(
        id = 3,
        questionId = 12,
        questionDisplay = "How much is your current savings? (Select all that apply)",
        questionSummary = "How much is your current savings? (Select all that apply)",
        order = 12,
        type = "Grid",
        gotoQuestionId = 13,
        options = listOf(
            OptionsItem(
                optionId = 1,
                display = "Bank",
                weight = 1,
                summary = "Bank",
                optionValue = 0,
                optionImage = 0,
                optionType = ""
            ),
            OptionsItem(
                optionId = 2,
                display = "Cash at home",
                weight = 2,
                summary = "Cash at home",
                optionValue = 1,
                optionImage = 0,
                optionType = ""
            ),
            OptionsItem(
                optionId = 3,
                display = "General",
                weight = 3,
                summary = "General",
                optionValue = 3,
                optionImage = 0,
                optionType = ""
            ),
            OptionsItem(
                optionId = 4,
                display = "Other",
                weight = 4,
                summary = "Other",
                optionValue = 4,
                optionImage = 0,
                optionType = ""
            )
        ),
        questionImageUrl = "Section1_ColourTV.webp",
    )

    GridTypeComponent(
        modifier = Modifier.padding(16.dp),
        question = question,
        onAnswerSelection = {},
        questionDetailExpanded = {},
        index = 1
    )
}

@Preview(showBackground = true)
@Composable
fun GridOptionCardPreview() {
    OptionCard(modifier = Modifier, "Option", index = 0, onOptionSelected = {}, selectedIndex = 0)
}