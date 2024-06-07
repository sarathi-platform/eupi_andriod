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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nudge.core.ui.events.theme.dimen_16_dp
import com.nudge.core.ui.events.theme.dimen_8_dp
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.model.uiModel.OptionsUiModel
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.dataloadingmangement.util.event.LoaderEvent
import com.sarathi.surveymanager.constants.QuestionType
import com.sarathi.surveymanager.ui.component.AddImageComponent
import com.sarathi.surveymanager.ui.component.ButtonPositive
import com.sarathi.surveymanager.ui.component.DatePickerComponent
import com.sarathi.surveymanager.ui.component.InputComponent
import com.sarathi.surveymanager.ui.component.ToolBarWithMenuComponent
import com.sarathi.surveymanager.ui.component.TypeDropDownComponent
import com.sarathi.surveymanager.ui.component.TypeMultiSelectedDropDownComponent
import kotlinx.coroutines.launch

@Composable
fun SurveyScreen(
    navController: NavController = rememberNavController(),
    viewModel: SurveyScreenViewModel,
    surveyId: Int, sectionId: Int, taskId: Int, subjectType: String, referenceId: String
) {
    val outerState = rememberLazyListState()
    val innerState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = true) {
        viewModel.setPreviousScreenData(surveyId, sectionId, taskId, subjectType, referenceId)
        viewModel.onEvent(LoaderEvent.UpdateLoaderState(true))
        viewModel.onEvent(InitDataEvent.InitDataState)
    }
    ToolBarWithMenuComponent(
        title = "Receipt of funds - Ganbari" +
                "Sikla (VO)",
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        onBackIconClick = { navController.popBackStack() },
        isSearch = false,
        isDataAvailable = viewModel.questionUiModel.value.isEmpty(),
        onSearchValueChange = {

        },
        onBottomUI = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                ButtonPositive(
                    buttonTitle = "Submit",
                    isActive = viewModel.isButtonEnable.value,
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
                    userScrollEnabled = false,
                    state = outerState,

                    modifier = Modifier
                        .heightIn(maxHeight)
                        .padding(
                            horizontal = dimen_16_dp
                        ), verticalArrangement = Arrangement.spacedBy(dimen_8_dp)
                ) {
                    itemsIndexed(
                        items = viewModel.questionUiModel.value
                    ) { index, question ->

                        when (question.type) {
                            QuestionType.InputNumber.name -> {
                                InputComponent(
                                    isMandatory = question.isMandatory,
                                    isEditable = viewModel.isTaskCompleted.value,
                                    defaultValue = question.options?.firstOrNull()?.selectedValue
                                        ?: BLANK_STRING,
                                    title = question.questionDisplay,
                                    isOnlyNumber = true,
                                    hintText = question.display
                                ) { selectedValue ->
                                    saveInputTypeAnswer(selectedValue, question, viewModel)
                                }
                            }

                            QuestionType.DateType.name -> {
                                DatePickerComponent(
                                    isMandatory = question.isMandatory,
                                    defaultValue = question.options?.firstOrNull()?.selectedValue
                                        ?: BLANK_STRING,
                                    title = question.questionDisplay,
                                    isEditable = viewModel.isTaskCompleted.value,
                                    hintText = question.display,
                                ) { selectedValue ->
                                    saveInputTypeAnswer(selectedValue, question, viewModel)

                                }
                            }

                            QuestionType.MultiImage.name -> {
                                AddImageComponent(
                                    filePaths = commaSeparatedStringToList(
                                        question.options?.firstOrNull()?.selectedValue
                                            ?: BLANK_STRING
                                    ),
                                    isMandatory = question.isMandatory,
                                    title = question.questionDisplay,
                                    isEditable = true,
                                    maxCustomHeight = maxHeight,

                                    ) { selectedValue ->
                                    saveMultiImageTypeAnswer(selectedValue, question.options)
                                    viewModel.checkButtonValidation()
                                }
                            }

                            QuestionType.SingleSelectDropDown.name -> {
                                TypeDropDownComponent(
                                    title = question.questionDisplay,
                                    isMandatory = question.isMandatory,
                                    sources = listOf()
                                ) {
                                }
                            }

                            QuestionType.MultiSelectDropDown.name -> {
                                TypeMultiSelectedDropDownComponent(
                                    title = question.questionDisplay,
                                    isMandatory = question.isMandatory,
                                    sources = listOf(),
                                    selectOptionText = BLANK_STRING
                                ) {
                                }
                            }
                        }
                    }


                }
            }
        },
        onSettingClick = {}
    )
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

fun saveMultiImageTypeAnswer(filePath: String, options: List<OptionsUiModel>?) {
    val list: ArrayList<String> = ArrayList<String>()
    list.add(filePath)
    list.addAll(commaSeparatedStringToList(options?.firstOrNull()?.selectedValue ?: BLANK_STRING))
    options?.firstOrNull()?.selectedValue = listToCommaSeparatedString(list)
    options?.firstOrNull()?.isSelected = true
}


fun listToCommaSeparatedString(list: List<String>): String {
    return list.joinToString(",")
}

fun commaSeparatedStringToList(commaSeparatedString: String): List<String> {
    return commaSeparatedString.split(",")
}


