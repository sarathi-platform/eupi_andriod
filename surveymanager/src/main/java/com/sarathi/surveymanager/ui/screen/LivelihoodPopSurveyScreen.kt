package com.sarathi.surveymanager.ui.screen

import android.text.TextUtils
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nudge.core.enums.ActivityTypeEnum
import com.nudge.core.getQuestionNumber
import com.nudge.core.ui.theme.dimen_14_dp
import com.nudge.core.ui.theme.dimen_1_dp
import com.nudge.core.ui.theme.greyBorderColor
import com.nudge.core.ui.theme.roundedCornerRadiusDefault
import com.nudge.core.value
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.DISBURSED_AMOUNT_TAG
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import com.sarathi.dataloadingmangement.util.constants.QuestionType
import com.sarathi.surveymanager.constants.DELIMITER_MULTISELECT_OPTIONS
import com.sarathi.surveymanager.ui.component.AddImageComponent
import com.sarathi.surveymanager.ui.component.CalculationResultComponent
import com.sarathi.surveymanager.ui.component.DatePickerComponent
import com.sarathi.surveymanager.ui.component.DropDownTypeComponent
import com.sarathi.surveymanager.ui.component.GridTypeComponent
import com.sarathi.surveymanager.ui.component.InputComponent
import com.sarathi.surveymanager.ui.component.RadioQuestionBoxComponent
import com.sarathi.surveymanager.ui.component.TypeMultiSelectedDropDownComponent

@Composable
fun LivelihoodPopSurveyScreen(
    navController: NavController = rememberNavController(),
    viewModel: LivelihoodPopSurveyScreenViewModel,
    surveyId: Int,
    sectionId: Int,
    taskId: Int,
    subjectType: String,
    referenceId: String,
    toolbarTitle: String,
    activityConfigId: Int,
    grantId: Int,
    activityType: String,
    sanctionedAmount: Int,
    totalSubmittedAmount: Int,
    onSettingClick: () -> Unit
) {
    BaseSurveyScreen(
        viewModel = viewModel,
        navController = navController,
        surveyId = surveyId,
        sectionId = sectionId,
        taskId = taskId,
        subjectType = subjectType,
        referenceId = referenceId,
        toolbarTitle = toolbarTitle,
        activityConfigId = activityConfigId,
        grantId = grantId,
        grantType = activityType,
        sanctionedAmount = sanctionedAmount,
        totalSubmittedAmount = totalSubmittedAmount,
        onSettingClick = onSettingClick,
        onAnswerSelect = { questionUiModel ->
            viewModel.saveSingleAnswerIntoDb(questionUiModel)
        },
        onSubmitButtonClick = {
            viewModel.updateTaskStatus(taskId, true)
        },
        surveyQuestionContent = { maxHeight ->

            LivelihoodPopSurveyQuestionContent(
                viewModel = viewModel,
                sanctionedAmount = sanctionedAmount,
                totalSubmittedAmount = totalSubmittedAmount,
                onAnswerSelect = { questionUiModel ->
                    viewModel.saveSingleAnswerIntoDb(questionUiModel)
                },
                activityType = activityType,
                maxHeight = maxHeight
            )

        }
    )
}

fun LazyListScope.LivelihoodPopSurveyQuestionContent(
    viewModel: BaseSurveyScreenViewModel,
    sanctionedAmount: Int,
    totalSubmittedAmount: Int,
    onAnswerSelect: (QuestionUiModel) -> Unit,
    activityType: String,
    maxHeight: Dp
) {

    itemsIndexed(viewModel.questionUiModel.value) { index, question ->

        Box(
            modifier = Modifier
                .border(
                    width = dimen_1_dp,
                    color = greyBorderColor,
                    shape = RoundedCornerShape(
                        roundedCornerRadiusDefault
                    )
                )
                .padding(vertical = dimen_14_dp)
        ) {
            when (question.type) {
                QuestionType.InputNumber.name -> {

                    InputComponent(
                        maxLength = 7,
                        isZeroNotAllowed = question.tagId.contains(DISBURSED_AMOUNT_TAG),
                        sanctionedAmount = sanctionedAmount,
                        remainingAmount = getSanctionedAmountMessage(
                            question,
                            sanctionedAmount = sanctionedAmount,
                            remainingAmount = totalSubmittedAmount - getSelectedValueInInt(
                                question.options?.firstOrNull()?.selectedValue
                                    ?: BLANK_STRING, 0
                            )
                        ),
                        isMandatory = question.isMandatory,
                        isEditable = viewModel.isActivityNotCompleted.value,
                        defaultValue = question.options?.firstOrNull()?.selectedValue
                            ?: BLANK_STRING,
                        title = question.questionDisplay,
                        isOnlyNumber = true,
                        hintText = question.options?.firstOrNull()?.description
                            ?: BLANK_STRING
                    ) { selectedValue, remainingAmout ->
                        viewModel.totalRemainingAmount = remainingAmout
                        saveInputTypeAnswer(selectedValue, question, viewModel)
                        onAnswerSelect(question)
                    }

                }

                QuestionType.DateType.name -> {

                    DatePickerComponent(
                        isMandatory = question.isMandatory,
                        defaultValue = question.options?.firstOrNull()?.selectedValue
                            ?: BLANK_STRING,
                        title = question.questionDisplay,
                        isEditable = viewModel.isActivityNotCompleted.value,
                        hintText = question.options?.firstOrNull()?.description
                            ?: BLANK_STRING
                    ) { selectedValue ->
                        saveInputTypeAnswer(selectedValue, question, viewModel)
                        onAnswerSelect(question)

                    }
                }

                QuestionType.MultiImage.name -> {
                    AddImageComponent(
                        fileNamePrefix = viewModel.getPrefixFileName(question),
                        filePaths = commaSeparatedStringToList(
                            question.options?.firstOrNull()?.selectedValue
                                ?: BLANK_STRING
                        ),
                        isMandatory = question.isMandatory,
                        title = question.questionDisplay,
                        isEditable = viewModel.isActivityNotCompleted.value,
                        maxCustomHeight = maxHeight,
                        subtitle = question.display
                    ) { selectedValue, isDeleted ->
                        saveMultiImageTypeAnswer(
                            selectedValue,
                            question.options,
                            isDeleted
                        )
                        viewModel.checkButtonValidation()
                        onAnswerSelect(question)

                    }
                }

                QuestionType.SingleSelectDropDown.name,
                QuestionType.DropDown.name -> {
                    DropDownTypeComponent(
                        isEditAllowed = viewModel.isActivityNotCompleted.value,
                        title = question.questionDisplay,
                        questionNumber = if (TextUtils.equals(
                                activityType.toLowerCase(),
                                ActivityTypeEnum.SURVEY.name.toLowerCase()
                            )
                        ) getQuestionNumber(index) else BLANK_STRING,
                        isMandatory = question.isMandatory,
                        showQuestionInCard = activityType.toLowerCase() == ActivityTypeEnum.SURVEY.name.toLowerCase(),
                        sources = getOptionsValueDto(question.options ?: listOf()),
                        onAnswerSelection = { selectedValue ->
                            question.options?.forEach { option ->
                                option.isSelected = selectedValue.id == option.optionId
                            }
                            viewModel.checkButtonValidation()
                            onAnswerSelect(question)

                        }
                    )
                }

                QuestionType.MultiSelectDropDown.name -> {
                    TypeMultiSelectedDropDownComponent(
                        title = question.questionDisplay,
                        isMandatory = question.isMandatory,
                        sources = getOptionsValueDto(question.options ?: listOf()),
                        isEditAllowed = viewModel.isActivityNotCompleted.value,
                        onAnswerSelection = { selectedItems ->
                            val selectedOptions =
                                selectedItems.split(DELIMITER_MULTISELECT_OPTIONS)
                            question.options?.forEach { options ->
                                if (selectedOptions.find { it == options.description.toString() } != null) {
                                    options.isSelected = true
                                } else {
                                    options.isSelected = false
                                }
                            }
                            viewModel.checkButtonValidation()
                            onAnswerSelect(question)

                        }
                    )
                }

                QuestionType.AutoCalculation.name -> {
                    CalculationResultComponent(
                        title = question.questionDisplay,
                    )
                }

                QuestionType.RadioButton.name -> {
                    RadioQuestionBoxComponent(
                        questionIndex = index,
                        questionDisplay = question.questionDisplay,
                        isRequiredField = question.isMandatory,
                        maxCustomHeight = maxHeight,
                        isQuestionTypeToggle = false,
                        showCardView = activityType.equals(
                            ActivityTypeEnum.SURVEY.name,
                            ignoreCase = true
                        ),
                        optionUiModelList = question.options.value(),
                        onAnswerSelection = { questionIndex, optionItemIndex ->
                            question.options?.forEachIndexed { index, _ ->
                                question.options?.get(index)?.isSelected = false
                            }
                            question.options?.get(optionItemIndex)?.isSelected = true
                            onAnswerSelect(question)
                            viewModel.checkButtonValidation()
                        }
                    )
                }

                QuestionType.MultiSelect.name,
                QuestionType.Grid.name -> {
                    GridTypeComponent(
                        questionIndex = index,
                        questionDisplay = question.questionDisplay,
                        isRequiredField = question.isMandatory,
                        maxCustomHeight = maxHeight,
                        optionUiModelList = question.options.value(),
                        onAnswerSelection = { selectedOptionIndex, isSelected ->

                            question.options?.get(selectedOptionIndex)?.isSelected =
                                isSelected

                            onAnswerSelect(question)
                            viewModel.checkButtonValidation()
                        },
                        questionDetailExpanded = {

                        }
                    )
                }

                QuestionType.Toggle.name -> {

                    RadioQuestionBoxComponent(
                        questionIndex = index,
                        questionDisplay = question.questionDisplay,
                        isRequiredField = question.isMandatory,
                        maxCustomHeight = maxHeight,
                        isQuestionTypeToggle = true,
                        showCardView = false,
                        optionUiModelList = question.options.value(),
                        onAnswerSelection = { questionIndex, optionItemIndex ->
                            question.options?.forEachIndexed { index, _ ->
                                question.options?.get(index)?.isSelected = false
                            }
                            question.options?.get(optionItemIndex)?.isSelected = true
                            onAnswerSelect(question)
                            viewModel.checkButtonValidation()
                        }
                    )

                }
            }
        }

    }

}
