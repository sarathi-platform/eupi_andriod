package com.nrlm.baselinesurvey.ui.common_components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.database.entity.OptionItemEntity
import com.nrlm.baselinesurvey.model.FormResponseObjectDto
import com.nrlm.baselinesurvey.ui.Constants.QuestionType
import com.nrlm.baselinesurvey.ui.question_screen.viewmodel.QuestionScreenViewModel
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.ui.theme.borderGreyLight
import com.nrlm.baselinesurvey.ui.theme.defaultCardElevation
import com.nrlm.baselinesurvey.ui.theme.dimen_14_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_1_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_56_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_8_dp
import com.nrlm.baselinesurvey.ui.theme.greyLightColor
import com.nrlm.baselinesurvey.ui.theme.roundedCornerRadiusDefault
import com.nrlm.baselinesurvey.ui.theme.white
import java.util.Locale

@Composable
fun FormResponseCard(
    modifier: Modifier = Modifier,
    householdMemberDto: FormResponseObjectDto,
    optionItemListWithConditionals: List<OptionItemEntity>,
    viewModel: BaseViewModel,
    isPictureRequired: Boolean = true,
    onDelete: (referenceId: String) -> Unit,
    onUpdate: (referenceId: String) -> Unit
) {

    val questionScreenViewModel = viewModel as QuestionScreenViewModel

    val fromTypeQuestionList = questionScreenViewModel.questionEntityStateList.filter { it.questionEntity?.type == QuestionType.Form.name }.toList()

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

        val dividerHeight = remember {
            mutableStateOf(0.dp)
        }

        Column(modifier = Modifier
            .background(white)
            .padding(vertical = dimen_8_dp)) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimen_8_dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isPictureRequired) {
                    CircularImageViewComponent(
                        modifier = Modifier
                            .height(dimen_56_dp)
                            .width(dimen_56_dp)
                    )
                }
                Spacer(modifier = Modifier.width(dimen_14_dp))
                Column {
                    Log.d("TAG", "FormResponseCard: optionItemListWithConditionals -> $optionItemListWithConditionals")
                    Text(text = buildAnnotatedString {
                        if (householdMemberDto.questionTag.equals("Household information")) {
                            append(householdMemberDto.memberDetailsMap[optionItemListWithConditionals.find {
                                it.display?.contains(
                                    "Name",
                                    ignoreCase = true
                                )!!
                            }?.optionId] ?: BLANK_STRING)
                        } else if (householdMemberDto.questionTag.equals("Livelihood sources")) {
                            append(householdMemberDto.memberDetailsMap[optionItemListWithConditionals.find {
                                it.display?.contains(
                                    "sources of income",
                                    ignoreCase = true
                                )!!
                            }?.optionId]  ?: BLANK_STRING)


                            var income = householdMemberDto.memberDetailsMap[optionItemListWithConditionals.find {
                                it.display?.contains(
                                    "(A) Agricultural produce - ",
                                    ignoreCase = true
                                )!!
                            }?.optionId] ?: BLANK_STRING

                            if (income == BLANK_STRING)
                                income = householdMemberDto.memberDetailsMap[optionItemListWithConditionals.find {
                                    it.display?.contains(
                                        "(A) Type of livestock - ",
                                        ignoreCase = true
                                    )!!
                                }?.optionId] ?: BLANK_STRING

                            if (income == BLANK_STRING)
                                income = householdMemberDto.memberDetailsMap[optionItemListWithConditionals.find {
                                    it.display?.contains(
                                        "Income frequency",
                                        ignoreCase = true
                                    )!!
                                }?.optionId] ?: BLANK_STRING

                            if (income != BLANK_STRING) {
                                append(" | ")
                            }

                            append(income)
                        } else if (householdMemberDto.questionTag.equals("Public Infra")) {
                            val questionState = fromTypeQuestionList.find { it.questionId == householdMemberDto.questionId }
                            var source = when (questionState?.questionEntity?.questionDisplay) {
                                "To the block office",
                                "To the nearest primary health care centre",
                                "To the nearest government school",
                                "To the nearest permanent market",
                                "To the nearest bank"-> {
                                    questionState.questionEntity.questionDisplay?.replace("To the ", BLANK_STRING)?.capitalize(Locale.ROOT)
                                }
                                else -> {
                                    BLANK_STRING
                                }
                            }

                            append(source)

                            var mode = BLANK_STRING
                            mode = householdMemberDto.memberDetailsMap[optionItemListWithConditionals.find {
                                it.display?.contains(
                                    "Acess to public transportation",
                                    ignoreCase = true
                                )!!
                            }?.optionId] ?: BLANK_STRING

                            if (mode != BLANK_STRING)
                                append("|")

                            append(mode)

                        }
                        else append(BLANK_STRING)
                    })

                    Text(text = buildAnnotatedString {
                        if (householdMemberDto.questionTag.equals("Household information")) {
                            this.append(householdMemberDto.memberDetailsMap[optionItemListWithConditionals.find {
                                it.display?.contains(
                                    "Relationship",
                                    ignoreCase = true
                                )!!
                            }?.optionId] ?: BLANK_STRING)
                            this.append(" | ")
                            this.append(householdMemberDto.memberDetailsMap[optionItemListWithConditionals.find {
                                it.display?.contains(
                                    "Age",
                                    ignoreCase = true
                                )!!
                            }?.optionId] ?: BLANK_STRING)
                        } else if (householdMemberDto.questionTag.equals("Livelihood sources")) {
                            append("Total Income: ")
                            var income = householdMemberDto.memberDetailsMap[optionItemListWithConditionals.find {
                                it.display?.contains(
                                    "(E) Total Income",
                                    ignoreCase = true
                                )!!
                            }?.optionId] ?: BLANK_STRING

                            if (income == BLANK_STRING)
                                income = householdMemberDto.memberDetailsMap[optionItemListWithConditionals.find {
                                    it.display?.contains(
                                        "(D) Total units",
                                        ignoreCase = true
                                    )!!
                                }?.optionId] ?: BLANK_STRING


                            if (income == BLANK_STRING)
                                income = householdMemberDto.memberDetailsMap[optionItemListWithConditionals.find {
                                    it.display?.contains(
                                        "Average Income",
                                        ignoreCase = true
                                    )!!
                                }?.optionId] ?: BLANK_STRING

                            if (income == BLANK_STRING)
                                income = householdMemberDto.memberDetailsMap[optionItemListWithConditionals.find {
                                    it.display?.contains(
                                        "What is the household income from small business in the last 12 months?",
                                        ignoreCase = true
                                    )!!
                                }?.optionId] ?: BLANK_STRING

                            append(income)
                        } else if (householdMemberDto.questionTag.equals("Public Infra")) {
                            val avgCost = householdMemberDto.memberDetailsMap[optionItemListWithConditionals.find {
                                it.display?.trim()?.contains(
                                    "Average travel cost ".trim(),
                                    ignoreCase = true
                                )!!
                            }?.optionId] ?: BLANK_STRING

                            append("Average Cost: $avgCost")

                        }
                        else {
                            append(BLANK_STRING)
                        }
                    })
                }
            }
            Spacer(modifier = Modifier.height(dimen_8_dp))
            Divider(thickness = dimen_1_dp, modifier = Modifier.fillMaxWidth(), color = borderGreyLight)
            Row(modifier = Modifier
                .fillMaxWidth()
            ) {
                TextButton(onClick = { onUpdate(householdMemberDto.referenceId) }, modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = blueDark)
                ) {
                    Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Edit Button", tint = blueDark)
                }
                Divider(
                    color = borderGreyLight,
                    modifier = Modifier
                        .fillMaxHeight()  //fill the max height
                        .width(1.dp)
                )
                TextButton(onClick = { onDelete(householdMemberDto.referenceId) }, modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = blueDark)
                ) {
                    Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Delete Button", tint = blueDark)
                }
            }
        }
    }
}

/*
@Preview(showSystemUi = true, showBackground = true)
@Composable
fun FormResponseCardPreview() {

}*/
