package com.sarathi.surveymanager.ui.screen

import android.text.TextUtils
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nudge.core.BLANK_STRING
import com.nudge.core.ui.commonUi.SubmitButtonBottomUi
import com.nudge.core.ui.commonUi.customVerticalSpacer
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_56_dp
import com.nudge.core.value
import com.sarathi.dataloadingmangement.DISBURSED_AMOUNT_TAG
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import com.sarathi.dataloadingmangement.model.uiModel.SurveyConfigCardSlots
import com.sarathi.dataloadingmangement.util.constants.QuestionType
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.surveymanager.R
import com.sarathi.surveymanager.constants.DELIMITER_MULTISELECT_OPTIONS
import com.sarathi.surveymanager.ui.component.AddImageComponent
import com.sarathi.surveymanager.ui.component.CalculationResultComponent
import com.sarathi.surveymanager.ui.component.DatePickerComponent
import com.sarathi.surveymanager.ui.component.DropDownTypeComponent
import com.sarathi.surveymanager.ui.component.GridTypeComponent
import com.sarathi.surveymanager.ui.component.InputComponent
import com.sarathi.surveymanager.ui.component.RadioQuestionBoxComponent
import com.sarathi.surveymanager.ui.component.ToggleQuestionBoxComponent
import com.sarathi.surveymanager.ui.component.ToolBarWithMenuComponent
import com.sarathi.surveymanager.ui.component.TypeMultiSelectedDropDownComponent

@Composable
fun FormQuestionScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: FormQuestionScreenViewModel = hiltViewModel(),
    taskId: Int,
    sectionId: Int,
    surveyId: Int,
    formId: Int,
    activityId: Int,
    activityConfigId: Int,
    missionId: Int,
    referenceId: String,
    subjectType: String,
    onNavigateBack: () -> Unit
) {

    LaunchedEffect(key1 = Unit) {
        viewModel.setPreviousScreenData(
            taskId,
            sectionId,
            surveyId,
            formId,
            activityId,
            activityConfigId,
            missionId,
            referenceId,
            subjectType = subjectType
        )
        viewModel.onEvent(InitDataEvent.InitFormQuestionScreenState)

    }

    ToolBarWithMenuComponent(
        title = viewModel.surveyConfig[SurveyConfigCardSlots.FORM_QUESTION_CARD_TITLE.name]?.value.value(),
        modifier = modifier,
        onBackIconClick = { navController.navigateUp() },
        onSearchValueChange = {},
        onBottomUI = {
            SubmitButtonBottomUi(
                isButtonActive = viewModel.isButtonEnable.value && viewModel.isActivityNotCompleted.value,
                buttonTitle = stringResource(R.string.submit),
                onSubmitButtonClick = {
                    viewModel.saveAllAnswers()
                    onNavigateBack()
                }
            )
        },
        onSettingClick = {},
        onContentUI = {

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxHeight()
            ) {
                LazyColumn(
                    /*verticalArrangement = Arrangement.spacedBy(dimen_10_dp),*/ modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimen_16_dp)
                ) {

                    itemsIndexed(viewModel.questionUiModel.value) { index, question ->

                        FormScreenQuestionUiContent(
                            index = index,
                            question = question,
                            viewModel = viewModel,
                            maxHeight,
                            onAnswerSelect = {
                                viewModel.updateQuestionResponseMap(question)
                                viewModel.runConditionCheck(question)
                            }
                        )
                    }

                    customVerticalSpacer(size = dimen_56_dp)
                }
            }


        }
    )

}

@Composable
fun FormScreenQuestionUiContent(
    index: Int,
    question: QuestionUiModel,
    viewModel: FormQuestionScreenViewModel,
    maxHeight: Dp,
    onAnswerSelect: (QuestionUiModel) -> Unit,
) {

    if (viewModel.visibilityMap[question.questionId].value()) {
        when (question.type) {
            QuestionType.InputNumber.name,
            QuestionType.TextField.name,
            QuestionType.NumericField.name,
            QuestionType.InputText.name -> {
                InputComponent(
                    questionIndex = index,
                    maxLength = 7,
                    isZeroNotAllowed = question.tagId.contains(DISBURSED_AMOUNT_TAG),
                    sanctionedAmount = 0,
                    remainingAmount = 0,
                    isMandatory = question.isMandatory,
                    isEditable = viewModel.isActivityNotCompleted.value,
                    showCardView = false,
                    defaultValue = question.options?.firstOrNull()?.selectedValue
                        ?: BLANK_STRING,
                    title = question.questionDisplay,
                    isOnlyNumber = question.type == QuestionType.NumericField.name || question.type == QuestionType.InputNumber.name,
                    hintText = question.options?.firstOrNull()?.description
                        ?: BLANK_STRING
                ) { selectedValue, remainingAmout ->
                    saveInputTypeAnswer(selectedValue, question, viewModel)
                    onAnswerSelect(question)
                }
            }

            QuestionType.DateType.name -> {

                DatePickerComponent(
                    questionIndex = index,
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
                    fileNamePrefix = /*viewModel.getPrefixFileName(question)*/ BLANK_STRING,
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
                    onAnswerSelect(question)
                    viewModel.checkButtonValidation()

                }
            }

            QuestionType.SingleSelectDropDown.name,
            QuestionType.DropDown.name -> {
                DropDownTypeComponent(
                    questionIndex = index,
                    isEditAllowed = viewModel.isActivityNotCompleted.value,
                    title = question.questionDisplay,
                    isMandatory = question.isMandatory,
                    showQuestionInCard = false,
                    sources = getOptionsValueDto(question.options ?: listOf()),
                    onAnswerSelection = { selectedValue ->
                        question.options?.forEach { option ->
                            option.isSelected = selectedValue.id == option.optionId
                        }
                        onAnswerSelect(question)
                        viewModel.checkButtonValidation()

                    }
                )
            }

            QuestionType.MultiSelectDropDown.name -> {
                TypeMultiSelectedDropDownComponent(
                    questionIndex = index,
                    title = question.questionDisplay,
                    isMandatory = question.isMandatory,
                    sources = getOptionsValueDto(question.options ?: listOf()),
                    isEditAllowed = viewModel.isActivityNotCompleted.value,
                    showCardView = false,
                    maxCustomHeight = maxHeight,
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
                        onAnswerSelect(question)
                        viewModel.checkButtonValidation()

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

            QuestionType.MultiSelect.name,
            QuestionType.Grid.name -> {
                GridTypeComponent(
                    questionIndex = index,
                    questionDisplay = question.questionDisplay,
                    isRequiredField = question.isMandatory,
                    maxCustomHeight = maxHeight,
                    optionUiModelList = question.options.value(),
                    onAnswerSelection = { selectedOptionIndex, isSelected ->

                        question.options?.get(selectedOptionIndex)?.isSelected = isSelected
                        onAnswerSelect(question)
//                                    viewModel.checkButtonValidation()
                    },
                    questionDetailExpanded = {

                    }
                )
            }

            QuestionType.Toggle.name -> {
                ToggleQuestionBoxComponent(
                    questionIndex = index,
                    questionDisplay = question.questionDisplay,
                    isRequiredField = question.isMandatory,
                    maxCustomHeight = maxHeight,
                    showCardView = false,
                    optionUiModelList = question.options.value(),
                    onAnswerSelection = { questionIndex, optionItemIndex ->
                        question.options?.forEachIndexed { index, _ ->
                            question.options?.get(index)?.isSelected = false
                        }
                        question.options?.get(optionItemIndex)?.isSelected = true
                        onAnswerSelect(question)
//                                    viewModel.checkButtonValidation()
                    }
                )
            }
        }
    }

}

fun saveInputTypeAnswer(
    selectedValue: String,
    question: QuestionUiModel,
    viewModel: FormQuestionScreenViewModel
) {
    if (TextUtils.isEmpty(selectedValue)) {
        question.options?.firstOrNull()?.isSelected = false
    } else {
        question.options?.firstOrNull()?.isSelected = true
    }
    question.options?.firstOrNull()?.selectedValue = selectedValue
    viewModel.checkButtonValidation()
}