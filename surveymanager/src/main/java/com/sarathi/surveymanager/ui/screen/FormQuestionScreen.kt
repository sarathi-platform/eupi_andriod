package com.sarathi.surveymanager.ui.screen

import android.content.Context
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nudge.core.BLANK_STRING
import com.nudge.core.CoreDispatchers
import com.nudge.core.ui.commonUi.AlertDialogComponent
import com.nudge.core.ui.commonUi.CustomVerticalSpacer
import com.nudge.core.ui.commonUi.SubmitButtonBottomUi
import com.nudge.core.ui.commonUi.customVerticalSpacer
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.defaultTextStyle
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_18_dp
import com.nudge.core.ui.theme.dimen_1_dp
import com.nudge.core.ui.theme.dimen_20_dp
import com.nudge.core.ui.theme.dimen_24_dp
import com.nudge.core.ui.theme.dimen_45_dp
import com.nudge.core.ui.theme.dimen_60_dp
import com.nudge.core.ui.theme.dimen_8_dp
import com.nudge.core.ui.theme.eventTextColor
import com.nudge.core.ui.theme.lightGray2
import com.nudge.core.ui.theme.newMediumTextStyle
import com.nudge.core.ui.theme.quesOptionTextStyle
import com.nudge.core.ui.theme.textColorDark
import com.nudge.core.ui.theme.white
import com.nudge.core.value
import com.sarathi.dataloadingmangement.DISBURSED_AMOUNT_TAG
import com.sarathi.dataloadingmangement.model.survey.response.ContentList
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import com.sarathi.dataloadingmangement.model.uiModel.SurveyConfigCardSlots.Companion.CONFIG_SLOT_TYPE_PREPOPULATED
import com.sarathi.dataloadingmangement.model.uiModel.UiConfigAttributeType
import com.sarathi.dataloadingmangement.util.constants.QuestionType
import com.sarathi.dataloadingmangement.util.constants.SurveyStatusEnum
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.dataloadingmangement.util.event.LoaderEvent
import com.sarathi.surveymanager.R
import com.sarathi.surveymanager.constants.DELIMITER_MULTISELECT_OPTIONS
import com.sarathi.surveymanager.ui.component.CalculationResultComponent
import com.sarathi.surveymanager.ui.component.DatePickerComponent
import com.sarathi.surveymanager.ui.component.DropDownTypeComponent
import com.sarathi.surveymanager.ui.component.GridTypeComponent
import com.sarathi.surveymanager.ui.component.HrsMinRangePickerComponent
import com.sarathi.surveymanager.ui.component.InputComponent
import com.sarathi.surveymanager.ui.component.RadioQuestionBoxComponent
import com.sarathi.surveymanager.ui.component.SingleImageComponent
import com.sarathi.surveymanager.ui.component.SubContainerView
import com.sarathi.surveymanager.ui.component.ToggleQuestionBoxComponent
import com.sarathi.surveymanager.ui.component.ToolBarWithMenuComponent
import com.sarathi.surveymanager.ui.component.TypeMultiSelectedDropDownComponent
import com.sarathi.surveymanager.ui.description_component.presentation.ModelBottomSheetDescriptionContentComponent
import com.sarathi.surveymanager.utils.DescriptionContentState
import com.sarathi.surveymanager.utils.getMaxInputLength
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterialApi::class)
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
    onNavigateBack: () -> Unit,
    onSettingClick: () -> Unit,
    onNavigateToMediaScreen: (
        contentData: ContentList
    ) -> Unit,
) {
    val sheetState =
        rememberModalBottomSheetState(ModalBottomSheetValue.Hidden, skipHalfExpanded = false)
    val selectedSectionDescription = remember {
        mutableStateOf(DescriptionContentState())
    }
    val coroutineScope = rememberCoroutineScope()

    val showAlertDialog = remember { mutableStateOf(false) }

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

    BackHandler {
        showAlertDialog.value = true
    }

    if (showAlertDialog.value) {
        AlertDialogComponent(
            onDismissRequest = { showAlertDialog.value = false },
            onConfirmation = {
                showAlertDialog.value = false
                navController.navigateUp()
            },
            dialogTitle = stringResource(R.string.alert_dialog_title_text),
            dialogText = stringResource(R.string.form_alert_dialog_message),
            confirmButtonText = stringResource(R.string.proceed),
            dismissButtonText = stringResource(R.string.cancel_text)
        )
    }

    if (viewModel.showLoader.value) {
        Dialog(
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            ), onDismissRequest = {}
        ) {
            Box(
                modifier = Modifier
                    .background(white)
                    .padding(dimen_16_dp)
                    .size(dimen_45_dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(dimen_24_dp),
                    color = textColorDark,
                    backgroundColor = Color.Transparent
                )
            }
        }
    }

    ModelBottomSheetDescriptionContentComponent(
        modifier = Modifier
            .fillMaxSize(),
        sheetContent = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = {
                    coroutineScope.launch {
                        sheetState.hide()
                    }
                }) {
                    androidx.compose.material3.Icon(
                        painter = painterResource(id = R.drawable.info_icon),
                        contentDescription = "question info button",
                        Modifier.size(dimen_18_dp),
                        tint = blueDark
                    )
                }
                if (sheetState.isVisible) {
                    Divider(
                        thickness = dimen_1_dp,
                        color = lightGray2,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                com.sarathi.surveymanager.ui.component.DescriptionContentComponent(
                    buttonClickListener = {
                        coroutineScope.launch {
                            sheetState.hide()
                        }
                    },
                    navigateToMediaPlayerScreen = { contentList ->
                        coroutineScope.launch {
                            if (sheetState.isVisible)
                                sheetState.hide()
                        }
                        onNavigateToMediaScreen(contentList)
                    },
                    descriptionContentState = selectedSectionDescription.value
                )
            }
        },
        sheetState = sheetState,
        sheetElevation = dimen_20_dp,
        sheetBackgroundColor = Color.White,
        sheetShape = RoundedCornerShape(topStart = dimen_10_dp, topEnd = dimen_10_dp)
    ) {
        ToolBarWithMenuComponent(
            title = viewModel.formTitle.value,
            modifier = modifier,
            onBackIconClick = { showAlertDialog.value = true },
            onSearchValueChange = {},
            onBottomUI = {
                Box(
                    modifier = Modifier
                        .background(white)
                        .fillMaxWidth()
                ) {
                    SubmitButtonBottomUi(
                        isButtonActive = viewModel.isButtonEnable.value && viewModel.isActivityNotCompleted.value && !viewModel.isSubmitButtonClicked.value,
                        buttonTitle = stringResource(R.string.submit),
                        onSubmitButtonClick = {
                            if (!viewModel.isSubmitButtonClicked.value) {
                                viewModel.isSubmitButtonClicked.value = true
                            viewModel.saveAllAnswers {
                                viewModel.updateTaskStatus(taskId)
                                viewModel.updateSectionStatus(
                                    missionId,
                                    surveyId,
                                    sectionId,
                                    taskId,
                                    SurveyStatusEnum.INPROGRESS.name
                                )
                                withContext(CoreDispatchers.mainDispatcher) {
                                    onNavigateBack()
                                    viewModel.onEvent(LoaderEvent.UpdateLoaderState(false))

                                }
                            }
                            }
                        }
                    )
                }
            },
            onSettingClick = {
                onSettingClick()
            },
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
                    viewModel.surveyConfig
                        .filter {
                            it.value.type.equals(UiConfigAttributeType.DYNAMIC.name, true)
                                    && it.value.componentType.equals(
                                CONFIG_SLOT_TYPE_PREPOPULATED,
                                true
                            )
                        }.forEach { mapEntry ->
                            item {
                                SubContainerView(
                                    mapEntry.value,
                                    isNumberFormattingRequired = false,
                                    labelStyle = newMediumTextStyle,
                                    valueStyle = defaultTextStyle.copy(fontWeight = FontWeight.Bold)
                                )
                                CustomVerticalSpacer()
                            }
                        }

                        itemsIndexed(viewModel.questionUiModel.value.sortedBy { it.order }) { index, question ->

                            FormScreenQuestionUiContent(
                                bottomSheetState = sheetState,
                                coroutineScope = coroutineScope,
                                index = index,
                                question = question,
                                navigateToMediaPlayerScreen = { contentList ->
                                    coroutineScope.launch {
                                        if (sheetState.isVisible)
                                            sheetState.hide()
                                    }
                                    onNavigateToMediaScreen(contentList)
                                },
                                viewModel = viewModel,
                                maxHeight = maxHeight,
                                onDetailIconClicked = {
                                    coroutineScope.launch {
                                        selectedSectionDescription.value =
                                            selectedSectionDescription.value.copy(
                                                contentDescription = question.contentEntities
                                            )

                                        delay(100)
                                        if (!sheetState.isVisible) {
                                            sheetState.show()
                                        } else {
                                            sheetState.hide()
                                        }
                                    }

                                },
                                onAnswerSelect = {
                                    viewModel.updateQuestionResponseMap(question)
                                    viewModel.runConditionCheck(question)
                                    viewModel.runValidationCheck(question.questionId) { isValid, message ->
                                        viewModel.fieldValidationAndMessageMap[question.questionId] =
                                            Pair(isValid, message)
                                    }
                                }
                            )
                        }

                    customVerticalSpacer(size = dimen_60_dp)
                }
            }


            }
        )
    }


}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FormScreenQuestionUiContent(
    bottomSheetState: ModalBottomSheetState,
    coroutineScope: CoroutineScope,
    index: Int,
    question: QuestionUiModel,
    viewModel: FormQuestionScreenViewModel,
    maxHeight: Dp,
    onAnswerSelect: (QuestionUiModel) -> Unit,
    navigateToMediaPlayerScreen: (ContentList) -> Unit,
    onDetailIconClicked: () -> Unit = {}, // Default empty lambda

) {
    val context = LocalContext.current
    if (viewModel.visibilityMap[question.questionId].value()) {
        Column {
            when (question.type) {
                QuestionType.InputNumber.name,
                QuestionType.TextField.name,
                QuestionType.NumericField.name,
                QuestionType.InputText.name -> {
                    InputComponent(
                        content = question.contentEntities,
                        isFromTypeQuestion = true,
                        questionIndex = index,
                        maxLength = getMaxInputLength(
                            questionId = question.questionId,
                            viewModel.sectionId,
                            type = question.type,
                            validations = viewModel.validations.orEmpty()
                        ),
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
                            ?: BLANK_STRING,
                        navigateToMediaPlayerScreen = { contentList ->
                            handleContentClick(
                                viewModel = viewModel,
                                context = context,
                                navigateToMediaPlayerScreen = { navigateToMediaPlayerScreen(it) },
                                contentList = contentList
                            )
                        },
                        onDetailIconClicked = { onDetailIconClicked() }
                    ) { selectedValue, remainingAmout ->
                        saveInputTypeAnswer(selectedValue, question)
                        onAnswerSelect(question)
                    }
                }

                QuestionType.DateType.name -> {

                    DatePickerComponent(
                        contents = question.contentEntities,
                        isFromTypeQuestion = true,
                        questionIndex = index,
                        isMandatory = question.isMandatory,
                        defaultValue = question.options?.firstOrNull()?.selectedValue
                            ?: BLANK_STRING,
                        title = question.questionDisplay,
                        onDetailIconClicked = { onDetailIconClicked() },
                        isEditable = viewModel.isActivityNotCompleted.value,
                        navigateToMediaPlayerScreen = { contentList ->
                            handleContentClick(
                                viewModel = viewModel,
                                context = context,
                                navigateToMediaPlayerScreen = { navigateToMediaPlayerScreen(it) },
                                contentList = contentList
                            )
                        },
                        hintText = question.options?.firstOrNull()?.description
                            ?: BLANK_STRING
                    ) { selectedValue ->
                        saveInputTypeAnswer(selectedValue, question)
                        onAnswerSelect(question)

                    }
                }

                QuestionType.MultiImage.name,
                QuestionType.SingleImage.name -> {
                    SingleImageComponent(
                        content = question.contentEntities,
                        isFromTypeQuestion = true,
                        onDetailIconClicked = { onDetailIconClicked() },
                        fileNamePrefix = viewModel.getPrefixFileName(question),
                        filePaths =
                        question.options?.firstOrNull()?.selectedValue
                            ?: BLANK_STRING,
                        isMandatory = question.isMandatory,
                        title = question.questionDisplay,
                        isEditable = viewModel.isActivityNotCompleted.value,
                        maxCustomHeight = maxHeight,
                        subtitle = question.display,

                        ) { selectedValue, isDeleted ->
                        saveSingleImage(isDeleted, question.options, selectedValue)
                        onAnswerSelect(question)


                    }
                }
                QuestionType.SingleSelectDropDown.name,
                QuestionType.DropDown.name -> {
                    DropDownTypeComponent(
                        isFromTypeQuestion = true,
                        contents = question.contentEntities,
                        questionIndex = index,
                        isEditAllowed = viewModel.isActivityNotCompleted.value,
                        title = question.questionDisplay,
                        isMandatory = question.isMandatory,
                        showQuestionInCard = false,
                        sources = getOptionsValueDto(question.options ?: listOf()),
                        onDetailIconClicked = { onDetailIconClicked() },
                        navigateToMediaPlayerScreen = { contentList ->
                            handleContentClick(
                                viewModel = viewModel,
                                context = context,
                                navigateToMediaPlayerScreen = { navigateToMediaPlayerScreen(it) },
                                contentList = contentList
                            )
                        },
                        onAnswerSelection = { selectedValue ->
                            question.options?.forEach { option ->
                                option.isSelected = selectedValue.id == option.optionId
                            }
                            onAnswerSelect(question)

                        }
                    )
                }

                QuestionType.MultiSelectDropDown.name -> {
                    TypeMultiSelectedDropDownComponent(
                        content = question.contentEntities,
                        isFromTypeQuestion = true,
                        questionIndex = index,
                        title = question.questionDisplay,
                        isMandatory = question.isMandatory,
                        sources = getOptionsValueDto(question.options ?: listOf()),
                        isEditAllowed = viewModel.isActivityNotCompleted.value,
                        showCardView = false,
                        maxCustomHeight = maxHeight,
                        navigateToMediaPlayerScreen = { contentList ->
                            handleContentClick(
                                viewModel = viewModel,
                                context = context,
                                navigateToMediaPlayerScreen = { navigateToMediaPlayerScreen(it) },
                                contentList = contentList
                            )
                        },
                        onDetailIconClicked = { onDetailIconClicked() },
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
                        isFromTypeQuestion = true,
                        content = question.contentEntities,
                        questionIndex = index,
                        questionDisplay = question.questionDisplay,
                        isRequiredField = question.isMandatory,
                        maxCustomHeight = maxHeight,
                        isQuestionTypeToggle = false,
                        showCardView = false,
                        onDetailIconClicked = { onDetailIconClicked() },
                        optionUiModelList = question.options.value(),
                        navigateToMediaPlayerScreen = { contentList ->
                            handleContentClick(
                                viewModel = viewModel,
                                context = context,
                                navigateToMediaPlayerScreen = { navigateToMediaPlayerScreen(it) },
                                contentList = contentList
                            )
                        },
                        onAnswerSelection = { questionIndex, optionItemIndex ->
                            question.options?.forEachIndexed { index, _ ->
                                question.options?.get(index)?.isSelected = false
                            }
                            question.options?.get(optionItemIndex)?.isSelected = true
                            onAnswerSelect(question)
                        }
                    )
                }

                QuestionType.MultiSelect.name,
                QuestionType.Grid.name -> {
                    GridTypeComponent(
                        isFromTypeQuestion = true,
                        content = question.contentEntities,
                        questionIndex = index,
                        questionDisplay = question.questionDisplay,
                        isRequiredField = question.isMandatory,
                        maxCustomHeight = maxHeight,
                        onDetailIconClicked = { onDetailIconClicked() },
                        optionUiModelList = question.options.value(),
                        navigateToMediaPlayerScreen = { contentList ->
                            handleContentClick(
                                viewModel = viewModel,
                                context = context,
                                navigateToMediaPlayerScreen = { navigateToMediaPlayerScreen(it) },
                                contentList = contentList
                            )
                        },
                        onAnswerSelection = { selectedOptionIndex, isSelected ->

                            question.options?.get(selectedOptionIndex)?.isSelected = isSelected
                            onAnswerSelect(question)
                        },
                        questionDetailExpanded = {

                        }
                    )
                }

                QuestionType.Toggle.name -> {
                    ToggleQuestionBoxComponent(
                        isFromTypeQuestion = true,
                        content = question.contentEntities,
                        questionIndex = index,
                        questionDisplay = question.questionDisplay,
                        isRequiredField = question.isMandatory,
                        maxCustomHeight = maxHeight,
                        showCardView = false,
                        onDetailIconClicked = { onDetailIconClicked() },
                        optionUiModelList = question.options.value(),
                        navigateToMediaPlayerScreen = { contentList ->
                            handleContentClick(
                                viewModel = viewModel,
                                context = context,
                                navigateToMediaPlayerScreen = { navigateToMediaPlayerScreen(it) },
                                contentList = contentList
                            )
                        },
                        onAnswerSelection = { questionIndex, optionItemIndex ->
                            question.options?.forEachIndexed { index, _ ->
                                question.options?.get(index)?.isSelected = false
                            }
                            question.options?.get(optionItemIndex)?.isSelected = true
                            onAnswerSelect(question)
                        }
                    )
                }

                QuestionType.InputHrsMinutes.name, QuestionType.InputYrsMonths.name -> {
                    HrsMinRangePickerComponent(
                        isFromTypeQuestion = true,
                        content = question.contentEntities,
                        isMandatory = question.isMandatory,
                        title = question.questionDisplay,
                        isEditAllowed = viewModel.isActivityNotCompleted.value,
                        typePicker = question.type,
                        onDetailIconClicked = { onDetailIconClicked() },
                        navigateToMediaPlayerScreen = { contentList ->
                            handleContentClick(
                                viewModel = viewModel,
                                context = context,
                                navigateToMediaPlayerScreen = { navigateToMediaPlayerScreen(it) },
                                contentList = contentList
                            )
                        },
                        defaultValue = question.options?.firstOrNull()?.selectedValue
                            ?: com.sarathi.dataloadingmangement.BLANK_STRING
                    ) { selectValue, selectedValueId ->
                        question.options?.firstOrNull()?.selectedValue = selectValue
                        question.options?.firstOrNull()?.isSelected = true

                        onAnswerSelect(question)
                    }
                }
            }
            if (viewModel.fieldValidationAndMessageMap[question.questionId]?.second != BLANK_STRING) {
                Text(
                    text = viewModel.fieldValidationAndMessageMap[question.questionId]?.second
                        ?: com.sarathi.dataloadingmangement.BLANK_STRING,
                    modifier = Modifier.padding(end = dimen_16_dp, top = dimen_8_dp),
                    style = quesOptionTextStyle.copy(color = eventTextColor)
                )
                CustomVerticalSpacer()
            }
        }

    }

}

fun saveInputTypeAnswer(
    selectedValue: String,
    question: QuestionUiModel,
) {
    if (TextUtils.isEmpty(selectedValue)) {
        question.options?.firstOrNull()?.isSelected = false
    } else {
        question.options?.firstOrNull()?.isSelected = true
    }
    question.options?.firstOrNull()?.selectedValue = selectedValue
}

fun handleContentClick(
    viewModel: FormQuestionScreenViewModel,
    context: Context,
    navigateToMediaPlayerScreen: (ContentList) -> Unit,
    contentList: ContentList
) {
    if (viewModel.isFilePathExists(
            contentList.contentValue ?: com.sarathi.dataloadingmangement.BLANK_STRING
        )
    ) {
        navigateToMediaPlayerScreen(contentList)
    } else {
        Toast.makeText(
            context,
            context.getString(R.string.file_not_exists),
            Toast.LENGTH_SHORT
        ).show()
    }
}