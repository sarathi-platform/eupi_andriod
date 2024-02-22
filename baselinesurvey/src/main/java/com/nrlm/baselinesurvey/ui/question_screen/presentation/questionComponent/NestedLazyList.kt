package com.nrlm.baselinesurvey.ui.question_screen.presentation.questionComponent

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.NO_SECTION
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.database.entity.InputTypeQuestionAnswerEntity
import com.nrlm.baselinesurvey.database.entity.OptionItemEntity
import com.nrlm.baselinesurvey.model.FormResponseObjectDto
import com.nrlm.baselinesurvey.model.datamodel.SectionListItem
import com.nrlm.baselinesurvey.navigation.home.HomeScreens
import com.nrlm.baselinesurvey.navigation.home.navigateToBaseLineStartScreen
import com.nrlm.baselinesurvey.navigation.home.navigateToFormTypeQuestionScreen
import com.nrlm.baselinesurvey.navigation.home.navigateToSearchScreen
import com.nrlm.baselinesurvey.ui.Constants.QuestionType
import com.nrlm.baselinesurvey.ui.common_components.ComplexSearchComponent
import com.nrlm.baselinesurvey.ui.common_components.FormResponseCard
import com.nrlm.baselinesurvey.ui.common_components.GridTypeComponent
import com.nrlm.baselinesurvey.ui.common_components.ListTypeQuestion
import com.nrlm.baselinesurvey.ui.common_components.RadioQuestionBoxComponent
import com.nrlm.baselinesurvey.ui.question_screen.presentation.QuestionEntityState
import com.nrlm.baselinesurvey.ui.question_screen.presentation.QuestionScreenEvents
import com.nrlm.baselinesurvey.ui.question_screen.presentation.handleOnMediaTypeDescriptionActions
import com.nrlm.baselinesurvey.ui.question_screen.viewmodel.QuestionScreenViewModel
import com.nrlm.baselinesurvey.ui.question_type_screen.presentation.QuestionTypeEvent
import com.nrlm.baselinesurvey.ui.theme.dimen_16_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_24_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_80_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_8_dp
import com.nrlm.baselinesurvey.ui.theme.h6
import com.nrlm.baselinesurvey.ui.theme.textColorDark
import com.nrlm.baselinesurvey.ui.theme.white
import com.nrlm.baselinesurvey.utils.findOptionFromId
import com.nrlm.baselinesurvey.utils.mapFormQuestionResponseToFromResponseObjectDto
import com.nrlm.baselinesurvey.utils.mapToOptionItem
import com.nrlm.baselinesurvey.utils.states.SectionStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val NEED_TO_UPDATE_LIST_DEFAULT_VALUE = Pair(false, BLANK_STRING)

@Composable
fun NestedLazyList(
    modifier: Modifier = Modifier,
    outerState: LazyListState = rememberLazyListState(),
    innerState: LazyListState = rememberLazyListState(),
    queLazyState: LazyListState = rememberLazyListState(),
    surveyeeId: Int,
    navController: NavController,
    viewModel: BaseViewModel,
    sectionDetails: SectionListItem,
    sectionInfoButtonClicked: () -> Unit,
    answeredQuestionCountIncreased: (question: QuestionEntityState) -> Unit,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val questionScreenViewModel = (viewModel as QuestionScreenViewModel)

    val inputTypeQuestionAnswerEntityList =
        questionScreenViewModel.inputTypeQuestionAnswerEntityList

    val innerQueState: LazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val innerFirstVisibleItemIndex by remember {
        derivedStateOf {
            innerState.firstVisibleItemIndex
        }
    }

    /*val answeredQuestionCount = remember {
        mutableIntStateOf(sectionDetails.questionAnswerMapping.size)
    }

    val answeredQuestionIndices = remember {
        mutableStateOf(mutableListOf<Int>())
    }

    val curPercentage = animateFloatAsState(
        targetValue = (if (answeredQuestionCount.intValue.toFloat() > questionScreenViewModel.questionEntityStateList.getSizeOfVisibleQuestions()
                .toFloat()
        ) questionScreenViewModel.questionEntityStateList.getSizeOfVisibleQuestions()
            .toFloat()
        else answeredQuestionCount.intValue.toFloat()) / questionScreenViewModel.questionEntityStateList.getSizeOfVisibleQuestions()
            .toFloat(),
        label = "",
        animationSpec = tween()
    )*/

    val lifecycleOwner = LocalLifecycleOwner.current

    val householdMemberDtoList = remember {
        mutableStateOf(mutableListOf<FormResponseObjectDto>())
    }

    DisposableEffect(key1 = context) {

        sectionDetails.questionList.find { it.type == QuestionType.Form.name }?.let { question ->
            coroutineScope.launch(Dispatchers.IO) {
                val optionItemEntityList =
                    questionScreenViewModel.getFormQuestionsOptionsItemEntityList(
                        sectionDetails.surveyId,
                        sectionDetails.sectionId,
                        question.questionId!!
                    )
                questionScreenViewModel.optionItemEntityList = optionItemEntityList
                questionScreenViewModel.formResponsesForQuestionLive =
                    questionScreenViewModel.getFormQuestionResponseEntityLive(
                        sectionDetails.surveyId,
                        sectionDetails.sectionId,
                        question.questionId!!,
                        surveyeeId
                    )
                withContext(Dispatchers.Main) {
                    questionScreenViewModel.formResponsesForQuestionLive.observe(lifecycleOwner) {
                        householdMemberDtoList.value.addAll(
                            it.mapFormQuestionResponseToFromResponseObjectDto(
                                optionItemEntityList,
                                question.tag
                            )
                        )
                        if (it.isNotEmpty()) {
                            answeredQuestionCountIncreased(
                                questionScreenViewModel.questionEntityStateList.find { questionEntityState -> questionEntityState.questionId == it.first().questionId }!!
                            )
                        }
                    }
                }
            }
        }
        onDispose {
            questionScreenViewModel.formResponsesForQuestionLive.removeObservers(lifecycleOwner)
        }
    }

    DisposableEffect(key1 = context) {
        coroutineScope.launch(Dispatchers.IO) {
            questionScreenViewModel.didiInfoObjectLive = questionScreenViewModel.getDidiInfoObjectLive(surveyeeId)
            withContext(Dispatchers.Main) {
                questionScreenViewModel.didiInfoObjectLive.observe(lifecycleOwner) {
                    if (!it.isNullOrEmpty()) {
                        questionScreenViewModel.questionEntityStateList
                            .find { questionEntityState ->
                                questionEntityState.questionEntity?.questionSummary.equals(
                                    "Add didi details",
                                    false
                                )
                            }?.let { it1 ->
                                answeredQuestionCountIncreased(
                                    it1
                                )
                            }
                    }
                }
            }
        }
        onDispose {
            questionScreenViewModel.didiInfoObjectLive.removeObservers(lifecycleOwner)
        }
    }

    val needToUpdateList = remember {
        mutableStateOf(NEED_TO_UPDATE_LIST_DEFAULT_VALUE)
    }


    SideEffect {
        if (outerState.layoutInfo.visibleItemsInfo.size == 2 && innerState.layoutInfo.totalItemsCount == 0)
            scope.launch { outerState.scrollToItem(outerState.layoutInfo.totalItemsCount) }
    }

    BoxWithConstraints(
        modifier = modifier
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
                        } else {
                            if (innerFirstVisibleItemIndex == 0 && innerState.firstVisibleItemScrollOffset == 0) {
                                outerState.scrollBy(-it)
                            } else {
                                innerState.scrollBy(-it)
                            }
                        }
                    }
                    it
                },
                Orientation.Vertical,
            )
    ) {
        LazyColumn(
            userScrollEnabled = false,
            state = outerState,
            modifier = Modifier
                .heightIn(maxHeight)
                .padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(dimen_8_dp)
        ) {
            item {
                ComplexSearchComponent {
                    navigateToSearchScreen(navController, sectionDetails.surveyId)
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(white),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.ArrowBack,
                        null,
                        tint = textColorDark,
                        modifier = Modifier.clickable {
                            if (!sectionDetails.sectionName.equals(NO_SECTION, true))
                                navController.popBackStack()
                            else
                                navController.popBackStack(
                                    HomeScreens.SURVEYEE_LIST_SCREEN.route,
                                    false
                                )
                        })

                    Spacer(modifier = Modifier.size(dimen_8_dp))
                    if (!sectionDetails.sectionName.equals(NO_SECTION, true))
                        Text(
                            text = sectionDetails.sectionName,
                            color = textColorDark,
                            style = h6,
                            textAlign = TextAlign.Center
                        )
                    Spacer(modifier = Modifier.size(dimen_8_dp))
                    Spacer(modifier = Modifier.size(24.dp))
                }
                /*TopAppBar(
                    title = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            if (!sectionDetails.sectionName.equals(NO_SECTION, true))
                                Text(
                                    text = sectionDetails.sectionName,
                                    color = textColorDark
                                )
                        }
                    },
                    navigationIcon = {

                        Icon(
                            Icons.Filled.ArrowBack,
                            null,
                            tint = textColorDark,
                            modifier = Modifier.clickable {
                                if (!sectionDetails.sectionName.equals(NO_SECTION, true))
                                    navController.popBackStack()
                                else
                                    navController.popBackStack(
                                        HomeScreens.SURVEYEE_LIST_SCREEN.route,
                                        false
                                    )
                            })

                    },
                    actions = {

                              Spacer(modifier = Modifier.size(24.dp))
//                        Icon(
//                            painterResource(id = R.drawable.info_icon),
//                            null,
//                            tint = textColorDark,
//                            modifier = Modifier.clickable {
//                                sectionInfoButtonClicked()
//                            }
//                        )
                    },
                    backgroundColor = white,
                    elevation = 0.dp,

                    )*/
            }

            // TODO Commenting this until it is fixed.
            /*item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .weight(1f)
                            .height(dimen_8_dp)
                            .padding(top = 1.dp)
                            .clip(RoundedCornerShape(14.dp)),
                        color = progressIndicatorColor,
                        trackColor = trackColor,
                        progress = curPercentage.value
                    )
                    Spacer(modifier = Modifier.width(dimen_8_dp))
                    Text(
                        text = "${answeredQuestionCount.value}/${sectionDetails.questionSize}",
                        color = textColorDark,
                        style = smallTextStyle
                    )
                }
            }
            item {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimen_8_dp)
                )
            }*/
            item {
                LazyColumn(
                    state = innerState,
                    userScrollEnabled = false,
                    modifier = Modifier
                        .height(maxHeight), verticalArrangement = Arrangement.spacedBy(dimen_8_dp)

                ) {
                    item {
                        Spacer(modifier = Modifier.width(dimen_24_dp))
                    }

                    itemsIndexed(
                        items = questionScreenViewModel.questionEntityStateList.distinctBy { it.questionId }
                            .filter { it.showQuestion } ?: emptyList()
                    ) { index, question ->

                        when (question?.questionEntity?.type) {
                            QuestionType.RadioButton.name -> {
                                val selectedOption =
                                    sectionDetails.questionAnswerMapping[question.questionId]?.first()
                                val optionList = sectionDetails.optionsItemMap[question.questionId]
                                RadioQuestionBoxComponent(
                                    questionIndex = index,
                                    question = question.questionEntity,
                                    showQuestionState = question,
                                    maxCustomHeight = maxHeight,
                                    optionItemEntityList = optionList!!,
                                    selectedOptionIndex = optionList.indexOf(optionList.find { it.optionId == selectedOption?.optionId })
                                        ?: -1,
                                    onAnswerSelection = { questionIndex, optionItem ->

                                        answeredQuestionCountIncreased(question)

                                        questionScreenViewModel.onEvent(
                                            QuestionScreenEvents.UpdateQuestionAnswerMappingForUi(
                                                question,
                                                listOf(optionItem)
                                            )
                                        )

                                        questionScreenViewModel.onEvent(
                                            QuestionTypeEvent.UpdateConditionQuestionStateForSingleOption(
                                                question,
                                                optionItem
                                            )
                                        )

                                        questionScreenViewModel.onEvent(
                                            QuestionScreenEvents.SectionProgressUpdated(
                                                surveyId = sectionDetails.surveyId,
                                                sectionId = sectionDetails.sectionId,
                                                didiId = surveyeeId,
                                                SectionStatus.INPROGRESS
                                            )
                                        )
                                        questionScreenViewModel.onEvent(
                                            QuestionScreenEvents.RatioTypeQuestionAnswered(
                                                surveyId = sectionDetails.surveyId,
                                                sectionId = sectionDetails.sectionId,
                                                didiId = surveyeeId,
                                                questionId = question.questionId ?: 0,
                                                optionItemId = optionItem.optionId ?: 0,
                                                questionEntity = question.questionEntity,
                                                optionItemEntity = optionItem
                                            )
                                        )
                                    },
                                    questionDetailExpanded = {
                                        scope.launch {
                                            queLazyState.animateScrollToItem(it + 3, -10)
                                        }
                                    },
                                    onMediaTypeDescriptionAction = { descriptionContentType, contentLink ->
                                        handleOnMediaTypeDescriptionActions(
                                            viewModel,
                                            navController,
                                            descriptionContentType,
                                            contentLink
                                        )
                                    }
                                )
                            }

                            QuestionType.SingleSelect.name,
                            QuestionType.List.name -> {
                                val selectedOption =
                                    sectionDetails.questionAnswerMapping[question.questionId]?.first()
                                val optionList = sectionDetails.optionsItemMap[question.questionId]

                                ListTypeQuestion(
                                    question = question.questionEntity,
                                    showQuestionState = question,
                                    optionItemEntityList = optionList ?: listOf(),
                                    selectedOptionIndex = optionList?.find { it.optionId == selectedOption?.optionId }?.optionId
                                        ?: -1
                                    /*optionList?.indexOf(selectedOption)
                                        ?: -1*/,
                                    questionIndex = index,
                                    maxCustomHeight = maxHeight,
                                    onAnswerSelection = { questionIndex, optionItem ->

                                        answeredQuestionCountIncreased(question)

                                        questionScreenViewModel.onEvent(
                                            QuestionScreenEvents.UpdateQuestionAnswerMappingForUi(
                                                question,
                                                listOf(optionItem)
                                            )
                                        )

                                        questionScreenViewModel.onEvent(
                                            QuestionTypeEvent.UpdateConditionQuestionStateForSingleOption(
                                                question,
                                                optionItem
                                            )
                                        )

                                        questionScreenViewModel.onEvent(
                                            QuestionScreenEvents.SectionProgressUpdated(
                                                surveyId = sectionDetails.surveyId,
                                                sectionId = sectionDetails.sectionId,
                                                didiId = surveyeeId,
                                                SectionStatus.INPROGRESS
                                            )
                                        )
                                        questionScreenViewModel.onEvent(
                                            QuestionScreenEvents.ListTypeQuestionAnswered(
                                                surveyId = sectionDetails.surveyId,
                                                sectionId = sectionDetails.sectionId,
                                                didiId = surveyeeId,
                                                questionId = question.questionId ?: 0,
                                                optionItemId = optionItem.optionId ?: 0,
                                                optionItemEntity = optionItem,
                                                questionEntity = question.questionEntity
                                            )
                                        )

                                    },
                                    questionDetailExpanded = {
                                        scope.launch {
                                            queLazyState.animateScrollToItem(it + 3, -10)
                                        }
                                    },
                                    onMediaTypeDescriptionAction = { descriptionContentType, contentLink ->
                                        handleOnMediaTypeDescriptionActions(
                                            viewModel,
                                            navController,
                                            descriptionContentType,
                                            contentLink
                                        )
                                    }
                                )
                            }

                            QuestionType.MultiSelect.name,
                            QuestionType.Grid.name -> {
                                val selectedOption =
                                    sectionDetails.questionAnswerMapping[question.questionId]
                                val optionList =
                                    sectionDetails.optionsItemMap[question.questionId] ?: listOf()
                                val selectedIndices = mutableListOf<Int>()
                                selectedOption?.forEach { selectedItem ->
                                    selectedIndices.add(
                                        selectedOption.find { it.optionId == selectedItem.optionId }?.optionId
                                            ?: -1
                                    )
                                }
                                GridTypeComponent(
                                    question = question.questionEntity,
                                    showQuestionState = question,
                                    questionIndex = index,
                                    optionItemEntityList = optionList,
                                    selectedOptionIndices = selectedIndices,
                                    maxCustomHeight = maxHeight,
                                    onAnswerSelection = { questionIndex, optionItems, selectedIndeciesCount ->

                                        answeredQuestionCountIncreased(question)

                                        questionScreenViewModel.onEvent(
                                            QuestionScreenEvents.UpdateQuestionAnswerMappingForUi(
                                                question,
                                                optionItems
                                            )
                                        )

                                        questionScreenViewModel.onEvent(
                                            QuestionTypeEvent.UpdateConditionQuestionStateForMultipleOption(
                                                questionEntityState = question,
                                                optionItemEntityList = optionItems
                                            )
                                        )

                                        questionScreenViewModel.onEvent(
                                            QuestionScreenEvents.SectionProgressUpdated(
                                                surveyId = sectionDetails.surveyId,
                                                sectionId = sectionDetails.sectionId,
                                                didiId = surveyeeId,
                                                sectionStatus = SectionStatus.INPROGRESS
                                            )
                                        )

                                        questionScreenViewModel.onEvent(
                                            QuestionScreenEvents.GridTypeQuestionAnswered(
                                                surveyId = sectionDetails.surveyId,
                                                sectionId = sectionDetails.sectionId,
                                                didiId = surveyeeId,
                                                questionId = question.questionId ?: 0,
                                                optionItemList = optionItems,
                                                questionEntity = question.questionEntity
                                            )
                                        )
                                    },
                                    questionDetailExpanded = {
                                        scope.launch {
                                            queLazyState.animateScrollToItem(it + 3, -10)
                                        }
                                    },
                                    onMediaTypeDescriptionAction = { descriptionContentType, contentLink ->
                                        handleOnMediaTypeDescriptionActions(
                                            viewModel,
                                            navController,
                                            descriptionContentType,
                                            contentLink
                                        )
                                    }
                                )
                            }

                            QuestionType.Form.name -> {
                                FormTypeQuestionComponent(
                                    question = question.questionEntity,
                                    showQuestionState = question,
                                    questionIndex = index,
                                    maxCustomHeight = maxHeight,
                                    onAnswerSelection = { questionIndex ->
                                        //TODO need to be dynamic..
                                        if (question.questionEntity.questionSummary.equals(
                                                "Add didi details",
                                                false
                                            )
                                        ) {
                                            navigateToBaseLineStartScreen(
                                                surveyeeId,
                                                sectionDetails.surveyId,
                                                navController
                                            )
                                        } else {
                                            /*if (householdMemberDtoList.value.size > 0 || !answeredQuestionIndices.value.contains(
                                                    questionIndex
                                                )
                                            ) {
                                                answeredQuestionIndices.value.add(questionIndex)
                                                answeredQuestionCount.value =
                                                    answeredQuestionCount.value.inc()
                                                        .coerceIn(
                                                            0,
                                                            sectionDetails.questionList.size
                                                        )
                                                answeredQuestionCountIncreased(answeredQuestionCount.value)
                                            }*/
                                            navigateToFormTypeQuestionScreen(
                                                navController,
                                                question.questionEntity,
                                                sectionDetails,
                                                surveyeeId
                                            )
                                        }
//                                        navController.navigate("$FORM_TYPE_QUESTION_SCREEN_ROUTE_NAME/${question.questionDisplay}/${sectionDetails.surveyId}/${sectionDetails.sectionId}/${question.questionId}/${surveyeeId}")
                                    },
                                    questionDetailExpanded = {

                                    },
                                    onMediaTypeDescriptionAction = { descriptionContentType, contentLink -> }
                                )
                            }

                            QuestionType.Input.name,
                            QuestionType.InputText.name,
                            QuestionType.InputNumber.name,
                            QuestionType.SingleSelectDropdown.name,
                            QuestionType.SingleSelectDropDown.name -> {
                                val selectedOption =
                                    sectionDetails.questionAnswerMapping[question.questionId]?.first()
                                val optionList = sectionDetails.optionsItemMap[question.questionId]
                                val selectedOptionMapForNumericInputTypeQuestions =
                                    mutableMapOf<Int, InputTypeQuestionAnswerEntity>()
                                if (question.questionEntity.type == QuestionType.InputNumber.name) {
                                    val selectedInputQuestionOptionItemEntityList =
                                        if (optionList != null) {
                                            inputTypeQuestionAnswerEntityList.value.mapToOptionItem(
                                                optionList
                                            )
                                        } else emptyList()
                                    selectedInputQuestionOptionItemEntityList.forEach { option ->
                                        option.optionId?.let {
                                            selectedOptionMapForNumericInputTypeQuestions[it] =
                                                inputTypeQuestionAnswerEntityList.value.findOptionFromId(
                                                    option
                                                )!!
                                        }
                                    }
                                }



                                MiscQuestionBoxComponent(
                                    question = question.questionEntity,
                                    showQuestionState = question,
                                    questionIndex = index,
                                    selectedOptionMapForNumericInputTypeQuestions = selectedOptionMapForNumericInputTypeQuestions,
                                    selectedOption = selectedOption,
                                    maxCustomHeight = maxHeight,
                                    onAnswerSelection = { questionIndex, optionItem, selectedValue ->

                                        answeredQuestionCountIncreased(question)


                                        when (optionItem.optionType) {
                                            QuestionType.SingleSelectDropdown.name,
                                            QuestionType.SingleSelectDropDown.name -> {
                                                val mOptionItem =
                                                    optionItem.copy(selectedValue = selectedValue)
                                                questionScreenViewModel.onEvent(
                                                    QuestionTypeEvent.UpdateConditionQuestionStateForSingleOption(
                                                        question,
                                                        mOptionItem
                                                    )
                                                )
                                            }
                                        }


                                        questionScreenViewModel.onEvent(
                                            QuestionScreenEvents.SectionProgressUpdated(
                                                surveyId = sectionDetails.surveyId,
                                                sectionId = sectionDetails.sectionId,
                                                didiId = surveyeeId,
                                                sectionStatus = SectionStatus.INPROGRESS
                                            )
                                        )

                                        val mOptionItem =
                                            optionItem.copy(selectedValue = selectedValue)
                                        questionScreenViewModel.onEvent(
                                            QuestionScreenEvents.UpdateQuestionAnswerMappingForUi(
                                                question,
                                                listOf(mOptionItem)
                                            )
                                        )

                                        when (question.questionEntity.type) {
                                            QuestionType.InputNumber.name -> {
                                                val mOptItem =
                                                    optionItem.copy(selectedValue = selectedValue)
                                                var mInputTypeQuestionAnswerEntity: InputTypeQuestionAnswerEntity? =
                                                    selectedOptionMapForNumericInputTypeQuestions[mOptItem.optionId]
                                                if (mInputTypeQuestionAnswerEntity == null) {
                                                    mInputTypeQuestionAnswerEntity =
                                                        InputTypeQuestionAnswerEntity(
                                                            id = 0,
                                                            didiId = surveyeeId,
                                                            sectionId = sectionDetails.sectionId,
                                                            questionId = question.questionId ?: -1,
                                                            optionId = mOptItem.optionId ?: -1,
                                                            surveyId = sectionDetails.surveyId,
                                                            inputValue = mOptItem.selectedValue!!
                                                        )
                                                } else {
                                                    mInputTypeQuestionAnswerEntity =
                                                        mInputTypeQuestionAnswerEntity.copy(
                                                            inputValue = mOptItem.selectedValue!!
                                                        )
                                                }
                                                questionScreenViewModel.onEvent(
                                                    mInputTypeQuestionAnswerEntity.let {
                                                        QuestionScreenEvents.UpdateInputTypeQuestionAnswerEntityForUi(
                                                            it
                                                        )
                                                    })

                                                questionScreenViewModel.onEvent(
                                                    QuestionScreenEvents.UpdateQuestionAnswerMappingForUi(
                                                        question,
                                                        listOf(mOptionItem)
                                                    )
                                                )
                                                questionScreenViewModel.onEvent(
                                                    QuestionScreenEvents.InputTypeQuestionAnswered(
                                                        surveyId = sectionDetails.surveyId,
                                                        sectionId = sectionDetails.sectionId,
                                                        didiId = surveyeeId,
                                                        questionId = question.questionId ?: -1,
                                                        optionItemId = optionItem.optionId ?: -1,
                                                        inputValue = selectedValue
                                                    )
                                                )
                                            }

                                            QuestionType.Input.name,
                                            QuestionType.InputText.name,
                                            QuestionType.SingleSelectDropdown.name,
                                            QuestionType.SingleSelectDropDown.name -> {
                                                questionScreenViewModel.onEvent(
                                                    QuestionScreenEvents.SaveMiscTypeQuestionAnswers(
                                                        surveyeeId = surveyeeId,
                                                        questionEntityState = question,
                                                        optionItemEntity = optionItem,
                                                        selectedValue = selectedValue
                                                    )
                                                )
                                            }
                                        }
                                    },
                                    onMediaTypeDescriptionAction = { descriptionContentType, contentLink ->

                                    },
                                    questionDetailExpanded = {}
                                )
                            }
                        }
                    }
                    item {
                        Column {
                            val optionItemListWithConditionals: List<OptionItemEntity> =
                                questionScreenViewModel.getOptionItemListWithConditionals()
                            householdMemberDtoList.value.distinctBy { it.referenceId }
                                .forEach { householdMemberDto ->
                                    FormResponseCard(
                                        householdMemberDto = householdMemberDto,
                                        optionItemListWithConditionals = optionItemListWithConditionals,
                                        isPictureRequired = householdMemberDto.questionTag.equals(
                                            "Household information",
                                            true
                                        ),
                                        viewModel = questionScreenViewModel,
                                        onDelete = {
                                            questionScreenViewModel.onEvent(
                                                QuestionTypeEvent.DeleteFormQuestionResponseEvent(
                                                    householdMemberDto.referenceId
                                                )
                                            )
                                            needToUpdateList.value =
                                                Pair(true, householdMemberDto.referenceId)
                                            Log.d(
                                                "TAG", "onEvent: onDelete -> " +
                                                        "otalQuestionCount.intValue = ${questionScreenViewModel.totalQuestionCount.intValue}, answeredQuestionCount: ${questionScreenViewModel.answeredQuestionCount.size}" +
                                                        " isSectionCompleted.value = ${
                                                            questionScreenViewModel.answeredQuestionCount.size == questionScreenViewModel.totalQuestionCount.intValue
                                                                    || questionScreenViewModel.answeredQuestionCount.size > questionScreenViewModel.totalQuestionCount.intValue
                                                        }"
                                            )
                                        },
                                        onUpdate = {
                                            sectionDetails.questionList.find { it.questionId == householdMemberDto.questionId }
                                                ?.let { it1 ->
                                                    navigateToFormTypeQuestionScreen(
                                                        navController = navController,
                                                        question = it1,
                                                        sectionDetails = sectionDetails,
                                                        surveyeeId = surveyeeId,
                                                        referenceId = householdMemberDto.referenceId
                                                    )
                                                }
                                        })
                                    Spacer(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(dimen_8_dp)
                                    )
                                }
                        }
                    }
                    item {
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(dimen_80_dp + dimen_16_dp)
                        )
                    }

                }
            }
        }
    }

    LaunchedEffect(key1 = needToUpdateList.value) {
        if (needToUpdateList.value.first) {
            householdMemberDtoList.value = householdMemberDtoList.value.apply {
                this.remove(this.find { it.referenceId == needToUpdateList.value.second })
            }
            needToUpdateList.value = NEED_TO_UPDATE_LIST_DEFAULT_VALUE
            Log.d(
                "TAG", "onEvent: LaunchedEffect -> " +
                        "otalQuestionCount.intValue = ${questionScreenViewModel.totalQuestionCount.intValue}, answeredQuestionCount: ${questionScreenViewModel.answeredQuestionCount.size}" +
                        " isSectionCompleted.value = ${
                            questionScreenViewModel.answeredQuestionCount.size == questionScreenViewModel.totalQuestionCount.intValue
                                    || questionScreenViewModel.answeredQuestionCount.size > questionScreenViewModel.totalQuestionCount.intValue
                        }"
            )
        }
    }

}