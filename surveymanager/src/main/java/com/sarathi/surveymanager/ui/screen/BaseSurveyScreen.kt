package com.sarathi.surveymanager.ui.screen

import android.text.TextUtils
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import com.nudge.core.DEFAULT_ID
import com.nudge.core.enums.ActivityTypeEnum
import com.nudge.core.getQuestionNumber
import com.nudge.core.showCustomToast
import com.nudge.core.ui.commonUi.BasicCardView
import com.nudge.core.ui.commonUi.CustomVerticalSpacer
import com.nudge.core.ui.commonUi.SubmitButtonBottomUi
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.defaultTextStyle
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_2_dp
import com.nudge.core.ui.theme.dimen_56_dp
import com.nudge.core.ui.theme.dimen_5_dp
import com.nudge.core.ui.theme.eventTextColor
import com.nudge.core.ui.theme.greyColor
import com.nudge.core.ui.theme.languageItemActiveBg
import com.nudge.core.ui.theme.quesOptionTextStyle
import com.nudge.core.ui.theme.summaryCardViewBlue
import com.nudge.core.ui.theme.white
import com.nudge.core.value
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.DISBURSED_AMOUNT_TAG
import com.sarathi.dataloadingmangement.NUMBER_ZERO
import com.sarathi.dataloadingmangement.model.survey.response.ValuesDto
import com.sarathi.dataloadingmangement.model.uiModel.OptionsUiModel
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import com.sarathi.dataloadingmangement.model.uiModel.SurveyConfigCardSlots
import com.sarathi.dataloadingmangement.ui.component.LinkTextButtonWithIcon
import com.sarathi.dataloadingmangement.util.constants.QuestionType
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.dataloadingmangement.util.event.LoaderEvent
import com.sarathi.surveymanager.R
import com.sarathi.surveymanager.constants.DELIMITER_MULTISELECT_OPTIONS
import com.sarathi.surveymanager.ui.component.AddImageComponent
import com.sarathi.surveymanager.ui.component.CalculationResultComponent
import com.sarathi.surveymanager.ui.component.DatePickerComponent
import com.sarathi.surveymanager.ui.component.DropDownTypeComponent
import com.sarathi.surveymanager.ui.component.GridTypeComponent
import com.sarathi.surveymanager.ui.component.IncrementDecrementCounterList
import com.sarathi.surveymanager.ui.component.InputComponent
import com.sarathi.surveymanager.ui.component.QuestionComponent
import com.sarathi.surveymanager.ui.component.RadioQuestionBoxComponent
import com.sarathi.surveymanager.ui.component.SubContainerView
import com.sarathi.surveymanager.ui.component.ToggleQuestionBoxComponent
import com.sarathi.surveymanager.ui.component.ToolBarWithMenuComponent
import com.sarathi.surveymanager.ui.component.TypeMultiSelectedDropDownComponent
import com.sarathi.surveymanager.utils.getMaxInputLength
import kotlinx.coroutines.launch
import com.nudge.core.R as CoreRes

@Composable
fun BaseSurveyScreen(
    navController: NavController,
    viewModel: BaseSurveyScreenViewModel,
    surveyId: Int,
    sectionId: Int,
    taskId: Int,
    subjectType: String,
    referenceId: String,
    toolbarTitle: String,
    activityConfigId: Int,
    grantId: Int,
    grantType: String,
    sanctionedAmount: Int,
    onAnswerSelect: (QuestionUiModel) -> Unit,
    totalSubmittedAmount: Int,
    onSubmitButtonClick: () -> Unit,
    onSettingClick: () -> Unit,
    onBackClicked: () -> Unit = {
        navController.popBackStack()
        navController.popBackStack()
    },
    surveyQuestionContent: (LazyListScope.(maxHeight: Dp) -> Unit) = { maxHeight ->
        BaseSurveyQuestionContent(
            viewModel,
            sanctionedAmount,
            totalSubmittedAmount,
            onAnswerSelect,
            grantType,
            maxHeight
        )
    }
) {
    val outerState = rememberLazyListState()
    val innerState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    BackHandler {
        onBackClicked()
    }

    LaunchedEffect(key1 = true) {
        viewModel.setPreviousScreenData(
            surveyId,
            sectionId,
            taskId,
            subjectType,
            referenceId,
            activityConfigId,
            grantId = grantId,
            grantType = grantType.toString(),
            sanctionedAmount,
            totalSubmittedAmount
        )
        viewModel.onEvent(LoaderEvent.UpdateLoaderState(true))
        viewModel.onEvent(InitDataEvent.InitDataState)
    }
    ToolBarWithMenuComponent(
        title = toolbarTitle,
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        onBackIconClick = {
            if (grantType.toLowerCase() == ActivityTypeEnum.SURVEY.name.toLowerCase())
                navController.popBackStack()
            else {
                navController.popBackStack()
                navController.popBackStack()
            }
        },
        isSearch = false,
        onSearchValueChange = {

        },
        onBottomUI = {
            SubmitButtonBottomUi(
                isButtonActive = viewModel.isButtonEnable.value && viewModel.isActivityNotCompleted.value,
                buttonTitle = stringResource(R.string.submit),
                onSubmitButtonClick = onSubmitButtonClick
            )
        },
        onContentUI = {
            BoxWithConstraints(
                modifier = Modifier
                    .scrollable(
                        state = rememberScrollableState {
                            scope.launch {
                                val toDown = it <= 0
                                if (toDown) {
                                    if (outerState.run { firstVisibleItemIndex == layoutInfo.totalItemsCount - 1 }) {
                                        innerState.scrollBy(-it)
                                    } else {
                                        outerState.scrollBy(-it)
                                    }
                                }
                            }
                            it
                        },
                        Orientation.Vertical,
                    )
                    .fillMaxHeight()
            ) {
                LazyColumn(
                    state = outerState,
                    modifier = Modifier
                        .heightIn(maxHeight)
                        .padding(start = dimen_16_dp, end = dimen_16_dp, bottom = dimen_56_dp),
                ) {
                    if (!grantType.equals(ActivityTypeEnum.BASIC.name, ignoreCase = true)) {
                        item { CustomVerticalSpacer() }
                    }

                    surveyQuestionContent(maxHeight)

                    item { CustomVerticalSpacer() }
                }
            }
        },
        onSettingClick = onSettingClick
    )
}

fun LazyListScope.BaseSurveyQuestionContent(
    viewModel: BaseSurveyScreenViewModel,
    sanctionedAmount: Int,
    totalSubmittedAmount: Int,
    onAnswerSelect: (QuestionUiModel) -> Unit,
    grantType: String,
    maxHeight: Dp
) {

    itemsIndexed(
        items = viewModel.questionUiModel.value.sortedBy { it.order }
    ) { index, question ->

        QuestionUiContent(
            question,
            sanctionedAmount,
            totalSubmittedAmount,
            viewModel,
            onAnswerSelect,
            maxHeight,
            grantType,
            index
        )
    }

}

@Composable
fun QuestionUiContent(
    question: QuestionUiModel,
    sanctionedAmount: Int,
    totalSubmittedAmount: Int,
    viewModel: BaseSurveyScreenViewModel,
    onAnswerSelect: (QuestionUiModel) -> Unit,
    maxHeight: Dp,
    grantType: String,
    index: Int
) {

    Column {
        val showCardView = grantType.equals(
            ActivityTypeEnum.SURVEY.name,
            ignoreCase = true
        )
        when (question.type) {
            QuestionType.InputNumber.name,
            QuestionType.TextField.name,
            QuestionType.NumericField.name,
            QuestionType.InputText.name -> {
                InputComponent(
                    questionIndex = index,
                    maxLength = getMaxInputLength(
                        questionId = question.questionId,
                        viewModel.sectionId,
                        question.type,
                        validations = viewModel.validations.orEmpty()
                    ),
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
                    showCardView = showCardView,
                    isEditable = viewModel.isActivityNotCompleted.value,
                    defaultValue = question.options?.firstOrNull()?.selectedValue
                        ?: BLANK_STRING,
                    title = question.questionDisplay,
                    isOnlyNumber = question.type == QuestionType.InputNumber.name || question.type == QuestionType.NumericField.name,
                    hintText = question.options?.firstOrNull()?.description
                        ?: BLANK_STRING
                ) { selectedValue, remainingAmout ->
                    viewModel.totalRemainingAmount = remainingAmout
                    saveInputTypeAnswer(selectedValue, question, viewModel)
                    onAnswerSelect(question)
                    viewModel.runValidationCheck(questionId = question.questionId) { isValid, message ->
                        viewModel.fieldValidationAndMessageMap[question.questionId] =
                            Pair(isValid, message)
                    }
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
                    showCardView = showCardView,
                    hintText = question.options?.firstOrNull()?.description
                        ?: BLANK_STRING,
                    isFutureDateDisable = true
                ) { selectedValue ->
                    saveInputTypeAnswer(selectedValue, question, viewModel)
                    onAnswerSelect(question)
                    viewModel.runValidationCheck(questionId = question.questionId) { isValid, message ->
                        viewModel.fieldValidationAndMessageMap[question.questionId] =
                            Pair(isValid, message)
                    }
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
                    viewModel.runValidationCheck(questionId = question.questionId) { isValid, message ->
                        viewModel.fieldValidationAndMessageMap[question.questionId] =
                            Pair(isValid, message)
                    }
                    onAnswerSelect(question)

                }
            }

            QuestionType.SingleSelectDropDown.name,
            QuestionType.DropDown.name -> {
                DropDownTypeComponent(
                    questionIndex = index,
                    isEditAllowed = viewModel.isActivityNotCompleted.value,
                    title = question.questionDisplay,
                    isMandatory = question.isMandatory,
                    showQuestionInCard = showCardView,
                    sources = getOptionsValueDto(question.options ?: listOf()),
                    onAnswerSelection = { selectedValue ->
                        question.options?.forEach { option ->
                            option.isSelected = selectedValue.id == option.optionId
                        }
                        viewModel.runValidationCheck(questionId = question.questionId) { isValid, message ->
                            viewModel.fieldValidationAndMessageMap[question.questionId] =
                                Pair(isValid, message)
                        }
                        onAnswerSelect(question)

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
                    maxCustomHeight = maxHeight,
                    showCardView = showCardView,
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
                        viewModel.runValidationCheck(questionId = question.questionId) { isValid, message ->
                            viewModel.fieldValidationAndMessageMap[question.questionId] =
                                Pair(isValid, message)
                        }
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
                    showCardView = showCardView,
                    optionUiModelList = question.options.value(),
                    onAnswerSelection = { questionIndex, optionItemIndex ->
                        question.options?.forEachIndexed { index, _ ->
                            question.options?.get(index)?.isSelected = false
                        }
                        question.options?.get(optionItemIndex)?.isSelected = true
                        onAnswerSelect(question)
                        viewModel.runValidationCheck(questionId = question.questionId) { isValid, message ->
                            viewModel.fieldValidationAndMessageMap[question.questionId] =
                                Pair(isValid, message)
                        }
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
                    showCardView = showCardView,
                    onAnswerSelection = { selectedOptionIndex, isSelected ->

                        question.options?.get(selectedOptionIndex)?.isSelected =
                            isSelected

                        onAnswerSelect(question)
                        viewModel.runValidationCheck(questionId = question.questionId) { isValid, message ->
                            viewModel.fieldValidationAndMessageMap[question.questionId] =
                                Pair(isValid, message)
                        }
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
                    showCardView = showCardView,
                    optionUiModelList = question.options.value(),
                    onAnswerSelection = { questionIndex, optionItemIndex ->
                        question.options?.forEachIndexed { index, _ ->
                            question.options?.get(index)?.isSelected = false
                        }
                        question.options?.get(optionItemIndex)?.isSelected = true
                        onAnswerSelect(question)
                        viewModel.runValidationCheck(questionId = question.questionId) { isValid, message ->
                            viewModel.fieldValidationAndMessageMap[question.questionId] =
                                Pair(isValid, message)
                        }
                    }
                )
            }

            QuestionType.IncrementDecrementList.name -> {
                IncrementDecrementCounterList(
                    title = question.questionDisplay,
                    optionList = question.options,
                    isMandatory = question.isMandatory,
                    isEditAllowed = viewModel.isActivityNotCompleted.value,
                    showCardView = showCardView,
                    onAnswerSelection = { optionId, mSelectedValue ->

                        question.options?.find { it.optionId == optionId }?.apply {
                            selectedValue = mSelectedValue
                            isSelected = true
                        }
                        onAnswerSelect(question)
                        viewModel.runValidationCheck(questionId = question.questionId) { isValid, message ->
                            viewModel.fieldValidationAndMessageMap[question.questionId] =
                                Pair(isValid, message)
                        }
                    }
                )
            }

        }
        Text(
            text = viewModel.fieldValidationAndMessageMap[question.questionId]?.second
                ?: BLANK_STRING,
            modifier = Modifier.padding(horizontal = dimen_5_dp),
            style = quesOptionTextStyle.copy(color = eventTextColor)
        )
    }
}

@Composable
fun FormQuestionUiContent(
    question: QuestionUiModel,
    viewModel: BaseSurveyScreenViewModel,
    maxHeight: Dp,
    grantType: String,
    index: Int,
    onClick: () -> Unit,
    onAnswerSelect: (QuestionUiModel) -> Unit,
    onViewSummaryClicked: (QuestionUiModel) -> Unit,
) {

    val context = LocalContext.current

    Column(
        verticalArrangement = Arrangement.spacedBy(dimen_2_dp)
    ) {
        BasicCardView(

        ) {

            Box(
                modifier = Modifier
                    .padding(dimen_16_dp)
            ) {
                Column {
                    QuestionComponent(
                        title = viewModel.surveyConfig[question.formId]?.get(SurveyConfigCardSlots.FORM_QUESTION_CARD_TITLE.name)?.value.value(),
                        questionNumber = getQuestionNumber(index),
                        isRequiredField = question.isMandatory
                    )

                    Row(modifier = Modifier
                        .clickable(enabled = true) {
                            if (viewModel.isActivityNotCompleted.value) { // TODO: change this check to use isFormEntryAllowed Method and test it to limit number of form responses.
                                onClick()
                            } else {
                                showCustomToast(
                                    context = context,
                                    context.getString(R.string.edit_disable_message)
                                )
                            }
                        }
                        .fillMaxWidth()
                        .background(if (true) blueDark else languageItemActiveBg)
                        .padding(dimen_10_dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically

                    ) {
                        Text(
                            text = viewModel.surveyConfig[question.formId]?.get(
                                SurveyConfigCardSlots.FORM_QUESTION_CARD_BUTTON.name
                            )?.value.value(),
                            style = defaultTextStyle.copy(color = if (true) white else greyColor)
                        )
                    }

                    if (viewModel.showSummaryView.get(question.formId)
                            .value() > NUMBER_ZERO
                    ) {
                        viewModel.surveyConfig[question.formId]?.get(SurveyConfigCardSlots.FORM_QUESTION_CARD_TOTAL_COUNT.name)
                            ?.let {
                                CustomVerticalSpacer()
                                val updatedTotalCountText =
                                    it.value + ": " + viewModel.showSummaryView[question.formId].value()
                                val updatedModel = it.copy(value = updatedTotalCountText)
                                SubContainerView(updatedModel, isNumberFormattingRequired = false)
                                CustomVerticalSpacer()
                                LinkTextButtonWithIcon(
                                    modifier = Modifier
                                        .align(Alignment.Start),
                                    title = stringResource(CoreRes.string.view_summary),
                                    isIconRequired = true,
                                    textColor = summaryCardViewBlue,
                                    iconTint = summaryCardViewBlue
                                ) {
                                    onViewSummaryClicked(question)
                                }
                            }

                    }
                }
            }

        }
        CustomVerticalSpacer()
    }
}

@Composable
fun getSanctionedAmountMessage(
    question: QuestionUiModel,
    sanctionedAmount: Int,
    remainingAmount: Int
): Int {
    if (sanctionedAmount != 0 && question.tagId.contains(DISBURSED_AMOUNT_TAG)) {
        return remainingAmount
    }
    return 0

}

fun getSelectedOptionId(options: List<OptionsUiModel>?): String {
    var selectedItems = ""
    options?.forEach { it ->
        if (it.isSelected == true) {
            selectedItems = "" + it.optionId + DELIMITER_MULTISELECT_OPTIONS
        }
    }
    return selectedItems
}

fun saveInputTypeAnswer(
    selectedValue: String,
    question: QuestionUiModel,
    viewModel: BaseSurveyScreenViewModel
) {
    if (TextUtils.isEmpty(selectedValue)) {
        question.options?.firstOrNull()?.isSelected = false
    } else {
        question.options?.firstOrNull()?.isSelected = true
    }
    question.options?.firstOrNull()?.selectedValue = selectedValue
    viewModel.runValidationCheck(questionId = question.questionId) { isValid, message ->

    }
}

fun saveMultiImageTypeAnswer(filePath: String, options: List<OptionsUiModel>?, isDeleted: Boolean) {
    val savedOptions =
        commaSeparatedStringToList(options?.firstOrNull()?.selectedValue ?: BLANK_STRING)
    val list: ArrayList<String> = ArrayList<String>()
    list.addAll(savedOptions)

    if (isDeleted) {
        list.remove(filePath)
    } else {
        list.add(filePath)

    }
    options?.firstOrNull()?.selectedValue = listToCommaSeparatedString(list)
    if (list.isEmpty()) {

        options?.firstOrNull()?.isSelected = false

    } else {
        options?.firstOrNull()?.isSelected = true
    }


}


fun listToCommaSeparatedString(list: List<String>): String {
    return list.joinToString(",")
}

fun commaSeparatedStringToList(commaSeparatedString: String): List<String> {
    if (commaSeparatedString.isEmpty()) {
        return listOf()
    }
    return commaSeparatedString.split(",")


}

fun getOptionsValueDto(options: List<OptionsUiModel>): List<ValuesDto> {
    val valuesDtoList = ArrayList<ValuesDto>()
    options.forEach {
        valuesDtoList.add(
            ValuesDto(
                id = it.optionId ?: DEFAULT_ID,
                value = it.description ?: BLANK_STRING,
                isSelected = it.isSelected
            )
        )
    }
    return valuesDtoList


}

fun getSelectedValueInInt(selectedValue: String, sanctionedAmount: Int): Int {
    return if (selectedValue.isNotBlank()) selectedValue.toInt() else sanctionedAmount
}


