package com.sarathi.surveymanager.ui.screen

import android.text.TextUtils
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nudge.core.DEFAULT_ID
import com.nudge.core.showCustomToast
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_56_dp
import com.nudge.core.ui.theme.dimen_8_dp
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.DISBURSED_AMOUNT_TAG
import com.sarathi.dataloadingmangement.model.survey.response.ValuesDto
import com.sarathi.dataloadingmangement.model.uiModel.OptionsUiModel
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import com.sarathi.dataloadingmangement.util.constants.QuestionType
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.dataloadingmangement.util.event.LoaderEvent
import com.sarathi.surveymanager.R
import com.sarathi.surveymanager.constants.DELIMITER_MULTISELECT_OPTIONS
import com.sarathi.surveymanager.ui.component.AddImageComponent
import com.sarathi.surveymanager.ui.component.ButtonPositive
import com.sarathi.surveymanager.ui.component.CalculationResultComponent
import com.sarathi.surveymanager.ui.component.DatePickerComponent
import com.sarathi.surveymanager.ui.component.GridTypeComponent
import com.sarathi.surveymanager.ui.component.InputComponent
import com.sarathi.surveymanager.ui.component.RadioQuestionBoxComponent
import com.sarathi.surveymanager.ui.component.ToolBarWithMenuComponent
import com.sarathi.surveymanager.ui.component.TypeDropDownComponent
import com.sarathi.surveymanager.ui.component.TypeMultiSelectedDropDownComponent
import kotlinx.coroutines.launch

@Composable
fun SurveyScreen(
    navController: NavController = rememberNavController(),
    viewModel: SurveyScreenViewModel,
    surveyId: Int,
    sectionId: Int,
    taskId: Int,
    subjectType: String,
    referenceId: String,
    subjectName: String,
    activityConfigId: Int,
    grantId: Int,
    grantType: String,
    sanctionedAmount: Int,
    totalSubmittedAmount: Int,
    onSettingClick: () -> Unit
) {
    val outerState = rememberLazyListState()
    val innerState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
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
        title = subjectName,
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        onBackIconClick = { navController.popBackStack() },
        isSearch = false,
        onSearchValueChange = {

        },
        onBottomUI = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                ButtonPositive(
                    buttonTitle = stringResource(R.string.submit),
                    isActive = viewModel.isButtonEnable.value && viewModel.isActivityNotCompleted.value,
                    isLeftArrow = false,
                    onClick = {
                            viewModel.saveButtonClicked()
                            navController.popBackStack()
                    }
                )
            }
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
                    verticalArrangement = Arrangement.spacedBy(dimen_8_dp)
                ) {
                    itemsIndexed(
                        items = viewModel.questionUiModel.value
                    ) { index, question ->

                        when (question.type) {
                            QuestionType.InputNumber.name -> {
                                InputComponent(
                                    maxLength = 7,
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
                                ) { selectedValue, isDeleted ->
                                    saveMultiImageTypeAnswer(
                                        selectedValue,
                                        question.options,
                                        isDeleted
                                    )
                                    viewModel.checkButtonValidation()
                                }
                            }

                            QuestionType.SingleSelectDropDown.name -> {
                                TypeDropDownComponent(
                                    isEditAllowed = viewModel.isActivityNotCompleted.value,
                                    title = question.questionDisplay,
                                    isMandatory = question.isMandatory,
                                    sources = getOptionsValueDto(question.options ?: listOf()),
                                    onAnswerSelection = { selectedValue ->
                                        question.options?.forEach { option ->
                                            option.isSelected = selectedValue.id == option.optionId
                                        }
                                        viewModel.checkButtonValidation()
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
                                            } }
                                        viewModel.checkButtonValidation()
                                    }
                                )
                            }

                            QuestionType.AutoCalculation.name -> {
                                CalculationResultComponent(
                                    title = question.questionDisplay,
                                )
                            }
                            QuestionType.RadioButton.name ->
                                { RadioQuestionBoxComponent(
                                questionIndex = index,
                                maxCustomHeight = maxHeight,
                                optionItemEntityList = listOf()!!,
                                onAnswerSelection = { questionIndex, optionItem ->
                                },
                                )
                            }
                            QuestionType.MultiSelect.name,
                                QuestionType.Grid.name ->
                            {
                                GridTypeComponent(
                                    questionIndex = index,
                                    maxCustomHeight = maxHeight,
                                    optionItemEntityList = listOf(),
                                    selectedOptionIndices =listOf() ,
                                    onAnswerSelection = { questionIndex, optionItems, selectedIndeciesCount ->
                                    }
                                ) {}
                            }
                            QuestionType.Toggle.name -> {
                            }
                        }
                    }
                }
            }
        },
        onSettingClick = onSettingClick
    )
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

private fun saveInputTypeAnswer(
    selectedValue: String,
    question: QuestionUiModel,
    viewModel: SurveyScreenViewModel
) {
    if (TextUtils.isEmpty(selectedValue)) {
        question.options?.firstOrNull()?.isSelected = false
    } else {
        question.options?.firstOrNull()?.isSelected = true
    }
    question.options?.firstOrNull()?.selectedValue = selectedValue
    viewModel.checkButtonValidation()
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

private fun getSelectedValueInInt(selectedValue: String, sanctionedAmount: Int): Int {
    return if (selectedValue.isNotBlank()) selectedValue.toInt() else sanctionedAmount
}


