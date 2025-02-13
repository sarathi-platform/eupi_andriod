package com.sarathi.surveymanager.ui.screen

import android.content.Context
import android.text.TextUtils
import android.widget.Toast
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import com.nudge.core.ACTIVITY_COMPLETED_ERROR
import com.nudge.core.DEFAULT_ID
import com.nudge.core.FORM_RESPONSE_LIMIT_ERROR
import com.nudge.core.enums.ActivityTypeEnum
import com.nudge.core.getQuestionNumber
import com.nudge.core.ui.commonUi.BasicCardView
import com.nudge.core.ui.commonUi.CustomVerticalSpacer
import com.nudge.core.ui.commonUi.SubmitButtonBottomUi
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.defaultTextStyle
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_20_dp
import com.nudge.core.ui.theme.dimen_2_dp
import com.nudge.core.ui.theme.dimen_56_dp
import com.nudge.core.ui.theme.dimen_5_dp
import com.nudge.core.ui.theme.dimen_6_dp
import com.nudge.core.ui.theme.dimen_8_dp
import com.nudge.core.ui.theme.eventTextColor
import com.nudge.core.ui.theme.greyColor
import com.nudge.core.ui.theme.languageItemActiveBg
import com.nudge.core.ui.theme.quesOptionTextStyle
import com.nudge.core.ui.theme.redOffline
import com.nudge.core.ui.theme.summaryCardViewBlue
import com.nudge.core.ui.theme.white
import com.nudge.core.value
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.DISBURSED_AMOUNT_TAG
import com.sarathi.dataloadingmangement.NUMBER_ZERO
import com.sarathi.dataloadingmangement.model.survey.response.ContentList
import com.sarathi.dataloadingmangement.model.survey.response.ValuesDto
import com.sarathi.dataloadingmangement.model.uiModel.OptionsUiModel
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import com.sarathi.dataloadingmangement.model.uiModel.SurveyConfigCardSlots
import com.sarathi.dataloadingmangement.ui.component.LinkTextButtonWithIcon
import com.sarathi.dataloadingmangement.util.constants.OptionType
import com.sarathi.dataloadingmangement.util.constants.QuestionType
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.dataloadingmangement.util.event.LoaderEvent
import com.sarathi.surveymanager.R
import com.sarathi.surveymanager.constants.DELIMITER_MULTISELECT_OPTIONS
import com.sarathi.surveymanager.ui.component.AddImageComponent
import com.sarathi.surveymanager.ui.component.CalculationResultComponent
import com.sarathi.surveymanager.ui.component.ContentBottomViewComponent
import com.sarathi.surveymanager.ui.component.DatePickerComponent
import com.sarathi.surveymanager.ui.component.DropDownTypeComponent
import com.sarathi.surveymanager.ui.component.GridTypeComponent
import com.sarathi.surveymanager.ui.component.HrsMinRangePickerComponent
import com.sarathi.surveymanager.ui.component.IncrementDecrementCounterList
import com.sarathi.surveymanager.ui.component.InputComponent
import com.sarathi.surveymanager.ui.component.QuestionComponent
import com.sarathi.surveymanager.ui.component.RadioQuestionBoxComponent
import com.sarathi.surveymanager.ui.component.SingleImageComponent
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
    navigateToMediaPlayerScreen: (content: ContentList) -> Unit = {},
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
            maxHeight,
            navigateToMediaPlayerScreen = { content ->
                navigateToMediaPlayerScreen(content)
            }
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
                isButtonActive = viewModel.isButtonEnable.value && !viewModel.isActivityCompleted.value,
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
    maxHeight: Dp,
    navigateToMediaPlayerScreen: (content: ContentList) -> Unit = {}

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
            index,
            navigateToMediaPlayerScreen = { content ->
                navigateToMediaPlayerScreen(content)
            }
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
    index: Int,
    navigateToMediaPlayerScreen: (content: ContentList) -> Unit = {}
) {
    val context = LocalContext.current
    Column {
        val showCardView = grantType.equals(
            ActivityTypeEnum.SURVEY.name,
            ignoreCase = true
        ) || grantType.equals(
            ActivityTypeEnum.BASIC.name,
            ignoreCase = true
        )
        when (question.type) {
            QuestionType.InputNumber.name,
            QuestionType.TextField.name,
            QuestionType.NumericField.name,
            QuestionType.InputText.name -> {
                InputComponent(
                    content = question.contentEntities,
                    questionIndex = index,
                    maxLength = getMaxInputLength(
                        questionId = question.questionId,
                        viewModel.sectionId,
                        question.type,
                        validations = viewModel.validations.orEmpty()
                    ),
                    isZeroNotAllowed = question.tagId.contains(DISBURSED_AMOUNT_TAG),
                    sanctionedAmount = sanctionedAmount,
                    totalSubmittedAmount = totalSubmittedAmount,
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
                    isEditable = !viewModel.isActivityCompleted.value,
                    defaultValue = question.options?.firstOrNull()?.selectedValue ?: BLANK_STRING,
                    optionsItem = question.options?.firstOrNull(),
                    title = question.questionDisplay,
                    isOnlyNumber = question.type == QuestionType.InputNumber.name || question.type == QuestionType.NumericField.name,
                    isError = !viewModel.fieldValidationAndMessageMap.get(question.questionId)?.first.value(
                        true
                    ),
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
                ) { selectedValue, totalSubmittedAmount ->
                    viewModel.totalRemainingAmount = totalSubmittedAmount
                    saveInputTypeAnswer(selectedValue, question)
                    onAnswerSelect(question)
                }
            }

            QuestionType.DateType.name -> {

                DatePickerComponent(
                    contents = question.contentEntities,
                    questionIndex = index,
                    isMandatory = question.isMandatory,
                    defaultValue = question.options?.firstOrNull()?.selectedValue
                        ?: BLANK_STRING,
                    title = question.questionDisplay,
                    isEditable = !viewModel.isActivityCompleted.value,
                    showCardView = showCardView,
                    navigateToMediaPlayerScreen = { contentList ->
                        handleContentClick(
                            viewModel = viewModel,
                            context = context,
                            navigateToMediaPlayerScreen = { navigateToMediaPlayerScreen(it) },
                            contentList = contentList
                        )
                    },
                    hintText = question.options?.firstOrNull()?.description
                        ?: BLANK_STRING,
                    isFutureDateDisable = true
                ) { selectedValue ->
                    saveInputTypeAnswer(selectedValue, question)
                    onAnswerSelect(question)
                }
            }

            QuestionType.MultiImage.name -> {
                AddImageComponent(
                    contents = question.contentEntities,
                    fileNamePrefix = viewModel.getPrefixFileName(question),
                    filePaths = commaSeparatedStringToList(
                        question.options?.firstOrNull()?.selectedValue
                            ?: BLANK_STRING
                    ),
                    isMandatory = question.isMandatory,
                    title = question.questionDisplay,
                    isEditable = !viewModel.isActivityCompleted.value,
                    maxCustomHeight = maxHeight,
                    navigateToMediaPlayerScreen = { contentList ->
                        handleContentClick(
                            viewModel = viewModel,
                            context = context,
                            navigateToMediaPlayerScreen = { navigateToMediaPlayerScreen(it) },
                            contentList = contentList
                        )
                    },
                    subtitle = question.display
                ) { selectedValue, isDeleted ->
                    saveMultiImageTypeAnswer(
                        selectedValue,
                        question.options,
                        isDeleted
                    )
                    onAnswerSelect(question)

                }
            }
            QuestionType.SingleImage.name -> {
                SingleImageComponent(
                    content = question.contentEntities,
                    fileNamePrefix = viewModel.getPrefixFileName(question),
                    filePaths =
                    question.options?.firstOrNull()?.selectedValue
                        ?: com.nudge.core.BLANK_STRING,
                    isMandatory = question.isMandatory,
                    title = question.questionDisplay,
                    isEditable = !viewModel.isActivityCompleted.value,
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
                    contents = question.contentEntities,
                    questionIndex = index,
                    isEditAllowed = !viewModel.isActivityCompleted.value,
                    title = question.questionDisplay,
                    isMandatory = question.isMandatory,
                    showQuestionInCard = showCardView,
                    sources = getOptionsValueDto(question.options ?: listOf()),
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
                    questionIndex = index,
                    title = question.questionDisplay,
                    isMandatory = question.isMandatory,
                    sources = getOptionsValueDto(question.options ?: listOf()),
                    isEditAllowed = !viewModel.isActivityCompleted.value,
                    maxCustomHeight = maxHeight,
                    showCardView = showCardView,
                    optionStateMap = viewModel.getOptionStateMapForMutliSelectDropDownQuestion(
                        question.questionId
                    ),
                    navigateToMediaPlayerScreen = { contentList ->
                        handleContentClick(
                            viewModel = viewModel,
                            context = context,
                            navigateToMediaPlayerScreen = { navigateToMediaPlayerScreen(it) },
                            contentList = contentList
                        )
                    },
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
                        val noneOptionCheckResult = viewModel.runNoneOptionCheck(question)
                        runNoneCheckForMultiSelectDropDownQuestions(noneOptionCheckResult, question)
                        onAnswerSelect(question)

                    }
                )
            }

            QuestionType.AutoCalculation.name -> {
                CalculationResultComponent(
                    title = question.questionDisplay,
                    defaultValue = viewModel.autoCalculateQuestionResultMap[question.questionId].value(),
                    showCardView = showCardView
                )
            }

            QuestionType.RadioButton.name -> {
                RadioQuestionBoxComponent(
                    content = question.contentEntities,
                    questionIndex = index,
                    questionDisplay = question.questionDisplay,
                    isRequiredField = question.isMandatory,
                    maxCustomHeight = maxHeight,
                    isQuestionTypeToggle = false,
                    showCardView = showCardView,
                    isEditAllowed = !viewModel.isActivityCompleted.value,
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
                    content = question.contentEntities,
                    questionIndex = index,
                    questionDisplay = question.questionDisplay,
                    isRequiredField = question.isMandatory,
                    maxCustomHeight = maxHeight,
                    optionUiModelList = question.options.value(),
                    showCardView = showCardView,
                    optionStateMap = viewModel.optionStateMap,
                    isEditAllowed = !viewModel.isActivityCompleted.value,
                    navigateToMediaPlayerScreen = { contentList ->
                        handleContentClick(
                            viewModel = viewModel,
                            context = context,
                            navigateToMediaPlayerScreen = { navigateToMediaPlayerScreen(it) },
                            contentList = contentList
                        )
                    },
                    onAnswerSelection = { selectedOptionIndex, isSelected ->
                        question.options?.get(selectedOptionIndex)?.isSelected =
                            isSelected

                        val noneOptionCheckResult = viewModel.runNoneOptionCheck(question)
                        if (noneOptionCheckResult) {
                            question.options?.forEach {
                                it.isSelected = false
                                it.selectedValue = BLANK_STRING
                            }

                            question.options?.get(selectedOptionIndex)?.isSelected =
                                isSelected
                        }


                        onAnswerSelect(question)
                    },
                    questionDetailExpanded = {

                    }
                )
            }

            QuestionType.Toggle.name -> {
                ToggleQuestionBoxComponent(
                    content = question.contentEntities,
                    questionIndex = index,
                    questionDisplay = question.questionDisplay,
                    isRequiredField = question.isMandatory,
                    maxCustomHeight = maxHeight,
                    showCardView = showCardView,
                    isEditAllowed = !viewModel.isActivityCompleted.value,
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

            QuestionType.IncrementDecrementList.name -> {
                IncrementDecrementCounterList(
                    content = question.contentEntities,
                    title = question.questionDisplay,
                    optionList = question.options,
                    isMandatory = question.isMandatory,
                    isEditAllowed = !viewModel.isActivityCompleted.value,
                    showCardView = showCardView,
                    editNotAllowedMsg = stringResource(R.string.edit_disable_message),
                    navigateToMediaPlayerScreen = { contentList ->
                        handleContentClick(
                            viewModel = viewModel,
                            context = context,
                            navigateToMediaPlayerScreen = { navigateToMediaPlayerScreen(it) },
                            contentList = contentList
                        )
                    },
                    onAnswerSelection = { optionId, mSelectedValue ->

                        question.options?.find { it.optionId == optionId }?.apply {
                            selectedValue = mSelectedValue
                            isSelected = true
                        }
                        onAnswerSelect(question)
                    }
                )
            }

            QuestionType.InputHrsMinutes.name, QuestionType.InputYrsMonths.name -> {
                HrsMinRangePickerComponent(
                    content = question.contentEntities,
                    isMandatory = question.isMandatory,
                    showCardView = showCardView,
                    title = question.questionDisplay,
                    isEditAllowed = !viewModel.isActivityCompleted.value,
                    typePicker = question.type,
                    navigateToMediaPlayerScreen = { contentList ->
                        handleContentClick(
                            viewModel = viewModel,
                            context = context,
                            navigateToMediaPlayerScreen = { navigateToMediaPlayerScreen(it) },
                            contentList = contentList
                        )
                    },
                    defaultValue = question.options?.firstOrNull()?.selectedValue
                        ?: BLANK_STRING
                ) { selectValue, selectedValueId ->
                    question.options?.firstOrNull()?.selectedValue = selectValue
                    question.options?.firstOrNull()?.isSelected = true
                    onAnswerSelect(question)
                }
            }

        }
        viewModel.fieldValidationAndMessageMap[question.questionId]?.second?.let {
            if (it != BLANK_STRING) {
                Text(
                    text = it,
                    modifier = Modifier
                        .padding(horizontal = dimen_5_dp)
                        .padding(top = dimen_8_dp, bottom = dimen_10_dp),
                    style = quesOptionTextStyle.copy(
                        color = getValidationMessageColor(
                            question,
                            viewModel.fieldValidationAndMessageMap[question.questionId]
                        )
                    )
                )
                CustomVerticalSpacer()
            } else {
                CustomVerticalSpacer(size = dimen_20_dp)
            }
        } ?: CustomVerticalSpacer(size = dimen_20_dp)
    }
}

@Composable
fun getValidationMessageColor(
    question: QuestionUiModel,
    fieldValidationAndMessageMap: Triple<Boolean, String, String?>?
): Color {
    return if (question.options?.all { op -> (op.isSelected == true) && op.selectedValue != com.nudge.core.BLANK_STRING } == true) {
        if (fieldValidationAndMessageMap?.first.value(
                true
            )
        ) eventTextColor else redOffline
    } else {
        eventTextColor
    }
}
fun runNoneCheckForMultiSelectDropDownQuestions(
    noneOptionCheckResult: Boolean,
    question: QuestionUiModel
) {
    if (noneOptionCheckResult) {
        question.options?.forEach {
            it.isSelected = false
            it.selectedValue = BLANK_STRING
        }

        question.options?.forEach { options ->
            if (options.optionType.equals(OptionType.None.name, true)) {
                options.isSelected = true
            }
        }
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
    showEditErrorToast: (context: Context, editErrorType: String) -> Unit,
    navigateToMediaPlayerScreen: (content: ContentList) -> Unit = {}
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
                        title = viewModel.surveyConfig[question.formId]?.get(SurveyConfigCardSlots.FORM_QUESTION_CARD_TITLE.name)
                            ?.firstOrNull()?.value.value(),
                        questionNumber = getQuestionNumber(index),
                        isRequiredField = question.isMandatory
                    )

                    Row(modifier = Modifier
                        .clickable(enabled = true) {
                            runEditCheck(
                                viewModel.isActivityCompleted.value,
                                viewModel.isFormEntryAllowed(question.formId)
                            ) { isEditAllowed: Boolean, errorType: String ->
                                if (isEditAllowed)
                                    onClick()
                                else
                                    showEditErrorToast(context, errorType)
                            }
                        }
                        .fillMaxWidth()
                        .background(if (viewModel.isFormEntryAllowed(question.formId)) blueDark else languageItemActiveBg)
                        .padding(dimen_10_dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically

                    ) {
                        Text(
                            text = viewModel.surveyConfig[question.formId]?.get(
                                SurveyConfigCardSlots.FORM_QUESTION_CARD_BUTTON.name
                            )?.firstOrNull()?.value.value(),
                            style = defaultTextStyle.copy(
                                color = if (viewModel.isFormEntryAllowed(
                                        question.formId
                                    )
                                ) white else greyColor
                            )
                        )
                    }

                    if (viewModel.showSummaryView.get(question.formId)
                            .value() > NUMBER_ZERO
                    ) {
                        viewModel.filteredSurveyModels[question.formId]?.forEach { model ->
                            CustomVerticalSpacer()
                            SubContainerView(
                                model,
                                isNumberFormattingRequired = false
                            )
                        }
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

                    if (question.formContent?.isNotEmpty() == true) {
                        CustomVerticalSpacer(size = dimen_6_dp)
                        ContentBottomViewComponent(
                            contents = question.formContent,
                            questionIndex = 0,
                            showCardView = true,
                            questionDetailExpanded = {},
                            navigateToMediaPlayerScreen = { contentList ->
                                handleContentClick(
                                    viewModel = viewModel,
                                    context = context,
                                    navigateToMediaPlayerScreen = { navigateToMediaPlayerScreen(it) },
                                    contentList = contentList
                                )
                            }
                        )
                    }
                }
            }

        }
        CustomVerticalSpacer()
    }
}

fun runEditCheck(
    isActivityCompleted: Boolean,
    formEntryAllowed: Boolean,
    editCheckResult: (Boolean, String) -> Unit
) {

    if (isActivityCompleted) {
        editCheckResult(false, ACTIVITY_COMPLETED_ERROR)
        return
    }

    if (!formEntryAllowed) {
        editCheckResult(false, FORM_RESPONSE_LIMIT_ERROR)
        return
    }

    editCheckResult(true, BLANK_STRING)
    return

}

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

fun saveSingleImage(isDeleted:Boolean, options: List<OptionsUiModel>?,filePath: String)
{
    if (isDeleted)
    {
        options?.firstOrNull()?.isSelected = false
        options?.firstOrNull()?.selectedValue =BLANK_STRING

    }else {
        options?.firstOrNull()?.isSelected = true
        options?.firstOrNull()?.selectedValue = filePath
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
    return if (selectedValue.isNotBlank()) selectedValue.toIntOrNull()
        .value(NUMBER_ZERO) else sanctionedAmount
}

fun handleContentClick(
    viewModel: BaseSurveyScreenViewModel,
    context: Context,
    navigateToMediaPlayerScreen: (ContentList) -> Unit,
    contentList: ContentList
) {
    if (viewModel.isFilePathExists(contentList.contentValue ?: BLANK_STRING)) {
        navigateToMediaPlayerScreen(contentList)
    } else {
        Toast.makeText(
            context,
            context.getString(R.string.file_not_exists),
            Toast.LENGTH_SHORT
        ).show()
    }
}



