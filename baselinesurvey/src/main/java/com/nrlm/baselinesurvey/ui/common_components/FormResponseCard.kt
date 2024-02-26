package com.nrlm.baselinesurvey.ui.common_components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
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
import com.nrlm.baselinesurvey.ui.theme.dimen_16_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_1_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_8_dp
import com.nrlm.baselinesurvey.ui.theme.roundedCornerRadiusDefault
import com.nrlm.baselinesurvey.ui.theme.smallTextStyleWithNormalWeight
import com.nrlm.baselinesurvey.ui.theme.white
import java.util.Locale
import com.nrlm.baselinesurvey.R

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
            Spacer(modifier = Modifier.width(dimen_14_dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimen_8_dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(dimen_14_dp))
                if (isPictureRequired) {
                    Box(modifier = Modifier.padding(start = 16.dp)) {
                        CircularImageViewComponent(
                            modifier = Modifier
                                .height(45.dp)
                                .width(45.dp)
                        )
                    }

                }
                Spacer(modifier = Modifier.width(dimen_14_dp))
                Column {
                    Text(text = buildAnnotatedString {
                        if (householdMemberDto.questionTag.equals("Household information")) {
                            append(householdMemberDto.memberDetailsMap[optionItemListWithConditionals.find {
                                it.display?.contains(
                                    stringResource(id = R.string.name_comparision),
                                    ignoreCase = true
                                )!!
                            }?.optionId] ?: BLANK_STRING)
                        } else if (householdMemberDto.questionTag.equals("Livelihood sources")) {
                            append(
                                householdMemberDto.memberDetailsMap[optionItemListWithConditionals.find {
                                    it.display?.contains(
                                        stringResource(id = R.string.income_source_comparision),
                                        ignoreCase = true
                                    )!!
                                }?.optionId]  ?: BLANK_STRING
                            )


                            var income = householdMemberDto.memberDetailsMap[optionItemListWithConditionals.find {
                                it.display?.contains(
                                    stringResource(id = R.string.agriculture_produce_comparision),
                                    ignoreCase = true
                                )!!
                            }?.optionId] ?: BLANK_STRING

                            if (income == BLANK_STRING)
                                income = householdMemberDto.memberDetailsMap[optionItemListWithConditionals.find {
                                    it.display?.contains(
                                        stringResource(id = R.string.livestock_comparision),
                                        ignoreCase = true
                                    )!!
                                }?.optionId] ?: BLANK_STRING

                            if (income == BLANK_STRING)
                                income = householdMemberDto.memberDetailsMap[optionItemListWithConditionals.find {
                                    it.display?.equals(
                                        stringResource(id = R.string.income_frequency_comparision),
                                        ignoreCase = true
                                    )!!
                                }?.optionId] ?: BLANK_STRING

                            if (income != BLANK_STRING) {
                                append(" | ")
                            }

                            append(income)
                        } else if (householdMemberDto.questionTag.equals("Public Infra")) {
                            val questionState = fromTypeQuestionList.filter { it.questionEntity?.type == QuestionType.Form.name }.find { it.questionId == householdMemberDto.questionId }
                            var source = when (questionState?.questionEntity?.questionDisplay) {
                                stringResource(R.string.to_the_block_office_comparision),
                                stringResource(R.string.to_the_nearest_primary_health_care_centre_comparision),
                                stringResource(R.string.to_the_nearest_government_school_comparision),
                                stringResource(R.string.to_the_nearest_permanent_market_comparision),
                                stringResource(R.string.to_the_nearest_bank_comparsion) -> {
                                    questionState.questionEntity.questionDisplay?.replace(
                                        stringResource(R.string.to_the_replacement_string), BLANK_STRING)?.capitalize(Locale.ROOT)
                                }

                                else -> {
                                    BLANK_STRING
                                }
                            }

                            append(source)

                            var mode = BLANK_STRING
                            mode = householdMemberDto.memberDetailsMap[optionItemListWithConditionals.find {
                                it.display?.contains(
                                    stringResource(R.string.acess_to_public_transportation_comparision),
                                    ignoreCase = true
                                )!!
                            }?.optionId] ?: BLANK_STRING

                            if (mode != BLANK_STRING)
                                append(" | ")

                            append(mode)

                        } else if (householdMemberDto.questionTag.contains("key programme", true)) {
                            var name = BLANK_STRING
                            name = householdMemberDto.memberDetailsMap[optionItemListWithConditionals.find {
                                it.display?.contains(
                                    stringResource(id = R.string.name_comparision),
                                    ignoreCase = true
                                )!!
                            }?.optionId] ?: BLANK_STRING

                            append("${stringResource(id = R.string.name_comparision)}: $name")

                        }
                        else append(BLANK_STRING)
                    }, style = smallTextStyleWithNormalWeight)

                    Text(text = buildAnnotatedString {
                        if (householdMemberDto.questionTag.equals("Household information")) {
                            this.append(householdMemberDto.memberDetailsMap[optionItemListWithConditionals.find {
                                it.display?.contains(
                                    stringResource(id = R.string.relationship_comparision),
                                    ignoreCase = true
                                )!!
                            }?.optionId] ?: BLANK_STRING)
                            this.append(" | ")
                            this.append(householdMemberDto.memberDetailsMap[optionItemListWithConditionals.find {
                                it.display?.contains(
                                    stringResource(id = R.string.age_comparision),
                                    ignoreCase = true
                                )!!
                            }?.optionId] ?: BLANK_STRING)
                            this.append(" yrs")
                        } else if (householdMemberDto.questionTag.equals("Livelihood sources")) {
                            if ((householdMemberDto.memberDetailsMap[optionItemListWithConditionals.find {
                                    it.display?.contains(
                                        stringResource(id = R.string.income_source_comparision),
                                        ignoreCase = true
                                    )!!
                                }?.optionId]  ?: BLANK_STRING) != stringResource(id = R.string.no_income_comparision)) {
                                append(stringResource(R.string.total_income_lable))
                                var income =
                                    householdMemberDto.memberDetailsMap[optionItemListWithConditionals.find {
                                        it.display?.contains(
                                            stringResource(id = R.string.total_income_comparision),
                                            ignoreCase = true
                                        )!!
                                    }?.optionId] ?: BLANK_STRING

                                if (income == BLANK_STRING)
                                    income =
                                        householdMemberDto.memberDetailsMap[optionItemListWithConditionals.find {
                                            it.display?.contains(
                                                stringResource(id = R.string.total_unit_comparision),
                                                ignoreCase = true
                                            )!!
                                        }?.optionId] ?: BLANK_STRING


                                if (income == BLANK_STRING)
                                    optionItemListWithConditionals.filter {
                                        it.display?.equals(
                                            stringResource(id = R.string.income_comparision),
                                            ignoreCase = true
                                        )!!
                                    }.forEach {
                                        if (income == BLANK_STRING) {
                                            income =
                                                householdMemberDto.memberDetailsMap[it.optionId] ?: BLANK_STRING
                                        }
                                        return@forEach
                                    }

                                if (income == BLANK_STRING)
                                    income =
                                        householdMemberDto.memberDetailsMap[optionItemListWithConditionals.find {
                                            it.display?.contains(
                                                stringResource(id = R.string.small_business_comparision),
                                                ignoreCase = true
                                            )!!
                                        }?.optionId] ?: BLANK_STRING

                                append(income)
                            }
                        } else if (householdMemberDto.questionTag.equals("Public Infra")) {
                            val avgCost = householdMemberDto.memberDetailsMap[optionItemListWithConditionals.find {
                                it.display?.trim()?.contains(
                                    stringResource(R.string.average_travel_cost_comparision).trim(),
                                    ignoreCase = true
                                )!!
                            }?.optionId] ?: BLANK_STRING

                            append("Average Cost: ₹ $avgCost")

                        } else if (householdMemberDto.questionTag.contains("key programme", true)) {
                            var influenceType = BLANK_STRING
                            influenceType = householdMemberDto.memberDetailsMap[optionItemListWithConditionals.find {
                                it.display?.contains(
                                    stringResource(R.string.influence_type_comparision_and_label),
                                    ignoreCase = true
                                )!!
                            }?.optionId] ?: BLANK_STRING

                            append("${stringResource(R.string.influence_type_comparision_and_label)}: $influenceType")

                        } else {
                            append(BLANK_STRING)
                        }
                    }, style = smallTextStyleWithNormalWeight)
                }
            }
            Spacer(modifier = Modifier.height(dimen_16_dp))
            Divider(thickness = dimen_1_dp, modifier = Modifier.fillMaxWidth(), color = borderGreyLight)
            Row(modifier = Modifier
                .fillMaxWidth()
            ) {
                /*TextButton(onClick = { onUpdate(householdMemberDto.referenceId) }, modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = blueDark)
                ) {
                    Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Edit Button", tint = blueDark)
                }*/
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
