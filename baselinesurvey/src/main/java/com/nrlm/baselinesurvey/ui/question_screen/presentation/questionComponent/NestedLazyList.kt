package com.nrlm.baselinesurvey.ui.question_screen.presentation.questionComponent

import android.os.Build
import android.text.TextUtils
import android.util.Log
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nrlm.baselinesurvey.ARG_FROM_QUESTION_SCREEN
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.NO_SECTION
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.database.entity.InputTypeQuestionAnswerEntity
import com.nrlm.baselinesurvey.database.entity.OptionItemEntity
import com.nrlm.baselinesurvey.model.datamodel.SectionListItem
import com.nrlm.baselinesurvey.ui.Constants.QuestionType
import com.nrlm.baselinesurvey.ui.common_components.CalculationResultComponent
import com.nrlm.baselinesurvey.ui.common_components.ComplexSearchComponent
import com.nrlm.baselinesurvey.ui.common_components.DidiInfoCard
import com.nrlm.baselinesurvey.ui.common_components.GridTypeComponent
import com.nrlm.baselinesurvey.ui.common_components.ListTypeQuestion
import com.nrlm.baselinesurvey.ui.common_components.RadioQuestionBoxComponent
import com.nrlm.baselinesurvey.ui.common_components.common_events.EventWriterEvents
import com.nrlm.baselinesurvey.ui.question_screen.presentation.QuestionEntityState
import com.nrlm.baselinesurvey.ui.question_screen.presentation.QuestionScreenEvents
import com.nrlm.baselinesurvey.ui.question_screen.presentation.handleOnMediaTypeDescriptionActions
import com.nrlm.baselinesurvey.ui.question_screen.viewmodel.QuestionScreenViewModel
import com.nrlm.baselinesurvey.ui.question_type_screen.presentation.QuestionTypeEvent
import com.nrlm.baselinesurvey.ui.theme.defaultCardElevation
import com.nrlm.baselinesurvey.ui.theme.dimen_16_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_24_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_80_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_8_dp
import com.nrlm.baselinesurvey.ui.theme.h6Bold
import com.nrlm.baselinesurvey.ui.theme.roundedCornerRadiusDefault
import com.nrlm.baselinesurvey.ui.theme.textColorDark
import com.nrlm.baselinesurvey.ui.theme.white
import com.nrlm.baselinesurvey.utils.BaselineCore
import com.nrlm.baselinesurvey.utils.convertInputTypeQuestionToEventOptionItemDto
import com.nrlm.baselinesurvey.utils.convertOptionItemEntityToFormResponseEntityForFormWithNone
import com.nrlm.baselinesurvey.utils.convertToSaveAnswerEventOptionItemDto
import com.nrlm.baselinesurvey.utils.findOptionFromId
import com.nrlm.baselinesurvey.utils.mapToOptionItem
import com.nrlm.baselinesurvey.utils.numberInEnglishFormat
import com.nrlm.baselinesurvey.utils.states.SectionStatus
import com.nudge.core.model.QuestionStatusModel
import com.nudge.navigationmanager.graphs.navigateToBaseLineStartScreen
import com.nudge.navigationmanager.graphs.navigateToFormQuestionSummaryScreen
import com.nudge.navigationmanager.graphs.navigateToFormTypeQuestionScreen
import com.nudge.navigationmanager.graphs.navigateToSearchScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val NEED_TO_UPDATE_LIST_DEFAULT_VALUE = Pair(false, BLANK_STRING)

@RequiresApi(Build.VERSION_CODES.N)
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
    answeredQuestionCountIncreased: (question: QuestionEntityState, isQuestionResponseUnanswered: Boolean) -> Unit,
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


    val lifecycleOwner = LocalLifecycleOwner.current

    val mQuestionEntity =
        questionScreenViewModel.questionEntityStateList.distinctBy { it.questionId }
            .filter { it.showQuestion } ?: emptyList()


    DisposableEffect(key1 = context) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                questionScreenViewModel.getDidiInfoObject(surveyeeId)
                questionScreenViewModel.didiInfoObjectLive = questionScreenViewModel.getDidiInfoObjectLive(surveyeeId)
                withContext(Dispatchers.Main) {
                    questionScreenViewModel.didiInfoObjectLive.observe(lifecycleOwner) {
                        if (!it.isNullOrEmpty()) {
                            questionScreenViewModel.questionEntityStateList
                                .find { questionEntityState ->
                                    questionEntityState.questionEntity?.questionSummary.equals(
                                        context.getString(R.string.add_didi_details_label),
                                        true
                                    )
                                }?.let { it1 ->
                                    answeredQuestionCountIncreased(
                                        it1,
                                        false
                                    )
                                }
                        }
                    }
                }
            } catch (ex: Exception) {
                Log.e("TAG", "NestedLazyList -> DisposableEffect: exception -> ${ex.message}", ex)
            }
        }
        onDispose {
            questionScreenViewModel.didiInfoObjectLive.removeObservers(lifecycleOwner)
        }
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
                            navController.popBackStack()
                        })

                    Spacer(modifier = Modifier.size(dimen_8_dp))
                    if (!sectionDetails.sectionName.equals(NO_SECTION, true))
                        Text(
                            text = sectionDetails.sectionName,
                            color = textColorDark,
                            style = h6Bold,
                            textAlign = TextAlign.Center
                        )
                    if (!sectionDetails.contentData.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.size(dimen_8_dp))
                        Icon(
                            painterResource(id = R.drawable.info_icon),
                            null,
                            tint = textColorDark,
                            modifier = Modifier.clickable {
                                sectionInfoButtonClicked()
                            }
                        )
                    } else {
                        Spacer(modifier = Modifier.size(dimen_8_dp))
                        Spacer(modifier = Modifier.size(24.dp))
                    }

                }
            }
            item {
                ComplexSearchComponent {
                    navController.navigateToSearchScreen(
                        surveyeId = sectionDetails.surveyId,
                        sectionId = sectionDetails.sectionId,
                        surveyeeId = surveyeeId,
                        fromScreen = ARG_FROM_QUESTION_SCREEN
                    )
                }
            }
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
                        items = mQuestionEntity.sortedBy { it.questionEntity?.order }
                    ) { index, question ->
                        when (question?.questionEntity?.type) {
                            QuestionType.AutoCalculation.name -> {
                                Card(
                                    elevation = CardDefaults.cardElevation(
                                        defaultElevation = defaultCardElevation
                                    ),
                                    colors = CardDefaults.cardColors(
                                        containerColor = white
                                    ),
                                    shape = RoundedCornerShape(roundedCornerRadiusDefault),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(white)
                                        .then(modifier)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(dimen_16_dp)
                                            .background(Color.Transparent)
                                    ) {
                                        CalculationResultComponent(
                                            title = question.questionEntity.questionDisplay,
                                            showQuestion = question.optionItemEntityState.first(),
                                            defaultValue = questionScreenViewModel.calculatedResult.value[question.questionId!!]
                                                ?: BLANK_STRING
                                        )
                                    }
                                }

                            }

                            QuestionType.RadioButton.name -> {
                                val selectedOption =
                                    if (sectionDetails.questionAnswerMapping[question.questionId].isNullOrEmpty()) OptionItemEntity.getEmptyOptionItemEntity() else sectionDetails.questionAnswerMapping[question.questionId]?.first()
                                val optionList =
                                    sectionDetails.optionsItemMap[question.questionId]
                                val contentData =
                                    sectionDetails.questionContentMapping[question.questionId]
                                RadioQuestionBoxComponent(
                                    questionIndex = index,
                                    question = question.questionEntity,
                                    showQuestionState = question,
                                    maxCustomHeight = maxHeight,
                                    contests = contentData,
                                    optionItemEntityList = optionList!!,
                                    questionStatusModel = QuestionStatusModel(
                                        isEditAllowed = questionScreenViewModel.isEditAllowed,
                                        isDidiReassigned = questionScreenViewModel.isDidiReassigned.value
                                    ),
                                    selectedOptionIndex = optionList.indexOf(optionList.find { it.optionId == selectedOption?.optionId })
                                        ?: -1,
                                    onAnswerSelection = { questionIndex, optionItem ->

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
                                        questionScreenViewModel.onEvent(
                                            EventWriterEvents.SaveAnswerEvent(
                                                surveyId = sectionDetails.surveyId,
                                                sectionId = sectionDetails.sectionId,
                                                didiId = surveyeeId,
                                                questionId = question.questionId ?: 0,
                                                questionType = question.questionEntity?.type
                                                    ?: BLANK_STRING,
                                                questionTag = question.questionEntity.tag,
                                                questionDesc = question.questionEntity.questionDisplay
                                                    ?: BLANK_STRING,
                                                showConditionalQuestion = !optionItem.conditions.isNullOrEmpty(),
                                                saveAnswerEventOptionItemDtoList = optionItem.convertToSaveAnswerEventOptionItemDto(
                                                    QuestionType.getQuestionTypeFromName(
                                                        question.questionEntity.type
                                                            ?: BLANK_STRING
                                                    )!!
                                                )
                                            )
                                        )

                                        answeredQuestionCountIncreased(question, false)
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
                                    if (sectionDetails.questionAnswerMapping[question.questionId].isNullOrEmpty()) OptionItemEntity.getEmptyOptionItemEntity() else sectionDetails.questionAnswerMapping[question.questionId]?.first()
                                val optionList =
                                    sectionDetails.optionsItemMap[question.questionId]
                                val contentData =
                                    sectionDetails.questionContentMapping[question.questionId]

                                ListTypeQuestion(
                                    question = question.questionEntity,
                                    showQuestionState = question,
                                    contests = contentData,
                                    optionItemEntityList = optionList ?: listOf(),
                                    selectedOptionIndex = optionList?.find { it.optionId == selectedOption?.optionId }?.optionId
                                        ?: -1
                                    /*optionList?.indexOf(selectedOption)
                                        ?: -1*/,
                                    questionIndex = index,
                                    maxCustomHeight = maxHeight,
                                    questionStatusModel = QuestionStatusModel(
                                        isDidiReassigned = questionScreenViewModel.isDidiReassigned.value,
                                        isEditAllowed = questionScreenViewModel.isEditAllowed
                                    ),
                                    onAnswerSelection = { questionIndex, optionItem ->

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

                                        questionScreenViewModel.onEvent(
                                            EventWriterEvents.SaveAnswerEvent(
                                                surveyId = sectionDetails.surveyId,
                                                sectionId = sectionDetails.sectionId,
                                                didiId = surveyeeId,
                                                questionId = question.questionId ?: 0,
                                                questionType = question.questionEntity.type
                                                    ?: BLANK_STRING,
                                                questionTag = question.questionEntity.tag,
                                                questionDesc = question.questionEntity.questionDisplay
                                                    ?: BLANK_STRING,
                                                showConditionalQuestion = !optionItem.conditions.isNullOrEmpty(),
                                                saveAnswerEventOptionItemDtoList = optionItem.convertToSaveAnswerEventOptionItemDto(
                                                    QuestionType.getQuestionTypeFromName(
                                                        question.questionEntity.type
                                                            ?: BLANK_STRING
                                                    )!!
                                                )
                                            )
                                        )
                                        answeredQuestionCountIncreased(question, false)
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
                                    sectionDetails.optionsItemMap[question.questionId]
                                        ?: listOf()
                                val selectedIndices = mutableListOf<Int>()
                                selectedOption?.forEach { selectedItem ->
                                    selectedIndices.add(
                                        selectedOption.find { it.optionId == selectedItem.optionId }?.optionId
                                            ?: -1
                                    )
                                }
                                val contentData =
                                    sectionDetails.questionContentMapping[question.questionId]
                                GridTypeComponent(
                                    question = question.questionEntity,
                                    showQuestionState = question,
                                    questionIndex = index,
                                    contests = contentData,
                                    areOptionsEnabled = question.optionItemEntityState.filter { it.isOptionEnabled }.size > 1,
                                    optionItemEntityList = optionList,
                                    selectedOptionIndices = selectedIndices,
                                    maxCustomHeight = maxHeight,
                                    questionStatusModel = QuestionStatusModel(
                                        isEditAllowed = questionScreenViewModel.isEditAllowed,
                                        isDidiReassigned = questionScreenViewModel.isDidiReassigned.value
                                    ),
                                    onAnswerSelection = { questionIndex, optionItems, selectedIndeciesCount ->

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

                                        questionScreenViewModel.onEvent(
                                            EventWriterEvents.SaveAnswerEvent(
                                                surveyId = sectionDetails.surveyId,
                                                sectionId = sectionDetails.sectionId,
                                                didiId = surveyeeId,
                                                questionId = question.questionId ?: 0,
                                                questionType = question.questionEntity?.type
                                                    ?: BLANK_STRING,
                                                questionTag = question.questionEntity.tag,
                                                questionDesc = question.questionEntity.questionDisplay
                                                    ?: BLANK_STRING,
                                                showConditionalQuestion = optionItems.any { it.conditions.isNullOrEmpty() },
                                                saveAnswerEventOptionItemDtoList = optionItems.convertToSaveAnswerEventOptionItemDto(
                                                    QuestionType.getQuestionTypeFromName(
                                                        question.questionEntity.type
                                                            ?: BLANK_STRING
                                                    )!!
                                                )
                                            )
                                        )

                                        answeredQuestionCountIncreased(
                                            question,
                                            optionItems.isEmpty()
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

                            QuestionType.Form.name, //TODO handle customisation for no income type question.
                            QuestionType.DidiDetails.name -> {
                                val contentData =
                                    sectionDetails.questionContentMapping[question.questionId]
                                val itemCount =
                                    questionScreenViewModel.getFormResponseItemCountForQuestion(
                                        question.questionId
                                    )

                                val summaryValue =
                                    questionScreenViewModel.getTotalIncomeForLivelihoodQuestion(
                                        context,
                                        question.questionId ?: 0
                                    )

                                FormTypeQuestionComponent(
                                    question = question.questionEntity,
                                    showQuestionState = question,
                                    questionIndex = index,
                                    contests = contentData,
                                    itemCount = itemCount,
                                    maxCustomHeight = maxHeight,
                                    summaryValue = summaryValue.toString(),
                                    questionStatusModel = QuestionStatusModel(
                                        isDidiReassigned = questionScreenViewModel.isDidiReassigned.value,
                                        isEditAllowed = questionScreenViewModel.isEditAllowed
                                    ),
                                    onAnswerSelection = { questionIndex ->
                                        //TODO need to be dynamic.
                                        if (question.questionEntity.questionSummary.equals(
                                                context.getString(R.string.add_didi_details_label),
                                                true
                                            )
                                        ) {
                                            navController.navigateToBaseLineStartScreen(
                                                surveyeeId = surveyeeId,
                                                survyId = sectionDetails.surveyId,
                                                sectionId = sectionDetails.sectionId
                                            )
                                        } else {
                                            BaselineCore.setReferenceId(BLANK_STRING)
                                            navController.navigateToFormTypeQuestionScreen(
                                                questionDisplay = question.questionEntity.questionDisplay?: BLANK_STRING,
                                                questionId = question.questionId?:0,
                                                surveyId = sectionDetails.surveyId,
                                                sectionId = sectionDetails.sectionId,
                                                surveyeeId
                                            )
                                        }
                                    },
                                    questionDetailExpanded = {
                                        scope.launch {
                                            queLazyState.animateScrollToItem(it + 3, -10)
                                        }
                                    },
                                    onMediaTypeDescriptionAction = { descriptionContentType, contentLink -> },
                                    onViewSummaryClicked = { questionId ->
                                        navController.navigateToFormQuestionSummaryScreen(
                                            surveyId = sectionDetails.surveyId,
                                            sectionId = sectionDetails.sectionId,
                                            questionId = questionId,
                                            didiId = surveyeeId
                                        )
                                    }
                                )
                            }

                            QuestionType.FormWithNone.name -> {

                                val contentData =
                                    sectionDetails.questionContentMapping[question.questionId]
                                val itemCount =
                                    questionScreenViewModel.getFormResponseItemCountForQuestion(
                                        question.questionId
                                    )

                                val noneOptionItemEntity =
                                    question.optionItemEntityState.find { it.optionItemEntity?.optionType == QuestionType.FormWithNone.name }

                                val noneOptionResponse = questionScreenViewModel
                                    .formResponseEntityToQuestionMap.value[question.questionId]?.find { it.optionId == noneOptionItemEntity?.optionId }

                                if (noneOptionResponse!=null && (noneOptionResponse?.selectedValue == noneOptionItemEntity?.optionItemEntity?.values?.first()?.value || noneOptionResponse?.selectedValue == noneOptionItemEntity?.optionItemEntity?.values?.last()?.value))
                                    questionScreenViewModel.setReferenceIdForFormWithNoneQuestion(
                                        noneOptionResponse?.referenceId ?: BLANK_STRING
                                    )

                                val summaryValue =
                                    numberInEnglishFormat(
                                        questionScreenViewModel.getTotalIncomeForLivelihoodQuestion(
                                            context,
                                            question.questionId ?: 0
                                        ).toInt(), null
                                    )
                                if (itemCount == 0 && noneOptionResponse?.selectedValue.equals(
                                        stringResource(id = R.string.option_yes)
                                    )
                                ) {
                                    answeredQuestionCountIncreased(
                                        question,
                                        true
                                    )
                                }
                                FormWithNoneTypeQuestionComponent(
                                    question = question.questionEntity,
                                    showQuestionState = question,
                                    noneOptionValue = noneOptionResponse,
                                    questionIndex = index,
                                    contests = contentData,
                                    itemCount = itemCount,
                                    maxCustomHeight = maxHeight,
                                    summaryValue = summaryValue.toString(),
                                    questionStatusModel = QuestionStatusModel(
                                        isEditAllowed = questionScreenViewModel.isEditAllowed,
                                        isDidiReassigned = questionScreenViewModel.isDidiReassigned.value
                                    ),
                                    onAnswerSelection = { questionId, isNoneMarkedForForm, isFormOpened ->
                                        if (isNoneMarkedForForm && !isFormOpened) {

                                            val mOptionItem = question.optionItemEntityState.find {
                                                it
                                                    .optionItemEntity?.optionType == QuestionType.FormWithNone.name
                                            }?.optionItemEntity!!

                                            questionScreenViewModel.onEvent(
                                                QuestionTypeEvent.SaveCacheFormQuestionResponseToDbEvent(
                                                    surveyId = sectionDetails.surveyId,
                                                    sectionId = sectionDetails.sectionId,
                                                    questionId = question.questionId ?: 0,
                                                    subjectId = surveyeeId,
                                                    formQuestionResponseList = listOf(
                                                        mOptionItem.copy(selectedValue = mOptionItem.values?.last()?.value,
                                                            selectedValueId = mOptionItem.values?.last()?.id
                                                                ?: 0
                                                        ) //when marked NO
                                                            .convertOptionItemEntityToFormResponseEntityForFormWithNone(
                                                                userId = questionScreenViewModel.getUserId(),
                                                                didiId = surveyeeId,
                                                                referenceId = questionScreenViewModel.getReferenceIdForFormWithNoneQuestion()
                                                            )
                                                    )
                                                )
                                            )

                                            answeredQuestionCountIncreased(
                                                question,
                                                false
                                            )

                                        }

                                        if (!isNoneMarkedForForm && !isFormOpened) {

                                            val mOptionItem = question.optionItemEntityState.find {
                                                it
                                                    .optionItemEntity?.optionType == QuestionType.FormWithNone.name
                                            }?.optionItemEntity!!

                                            questionScreenViewModel.onEvent(
                                                QuestionTypeEvent.SaveCacheFormQuestionResponseToDbEvent(
                                                    surveyId = sectionDetails.surveyId,
                                                    sectionId = sectionDetails.sectionId,
                                                    questionId = question.questionId ?: 0,
                                                    subjectId = surveyeeId,
                                                    formQuestionResponseList = listOf(
                                                        mOptionItem.copy(selectedValue = mOptionItem.values?.first()?.value,
                                                            selectedValueId = mOptionItem.values?.first()?.id
                                                                ?: 0
                                                        ) //when marked YES
                                                            .convertOptionItemEntityToFormResponseEntityForFormWithNone(
                                                                userId = questionScreenViewModel.getUserId(),
                                                                didiId = surveyeeId,
                                                                referenceId = questionScreenViewModel.getReferenceIdForFormWithNoneQuestion()
                                                            )
                                                    )
                                                )
                                            )

                                            answeredQuestionCountIncreased(
                                                question,
                                                itemCount == 0
                                                )

                                        }

                                        if (!isNoneMarkedForForm && isFormOpened) {
//                                            BaselineCore.setReferenceId(questionScreenViewModel.getReferenceIdForFormWithNoneQuestion())
                                            navController.navigateToFormTypeQuestionScreen(
                                                questionId = question.questionId ?:0,
                                                questionDisplay = question.questionEntity.questionDisplay ?: BLANK_STRING,
                                                surveyId = sectionDetails.surveyId,
                                                sectionId = sectionDetails.sectionId,
                                                surveyeeId =  surveyeeId
                                            )
                                        }
                                    },

                                    onMediaTypeDescriptionAction = { descriptionContentType, contentLink ->
                                    },
                                    questionDetailExpanded = {
                                        scope.launch {
                                            queLazyState.animateScrollToItem(it + 3, -10)
                                        }
                                    },
                                    onViewSummaryClicked = { questionId ->
                                        navController.navigateToFormQuestionSummaryScreen(
                                            surveyId = sectionDetails.surveyId,
                                            sectionId = sectionDetails.sectionId,
                                            questionId = questionId,
                                            didiId = surveyeeId
                                        )
                                    }
                                )
                            }

                            QuestionType.Input.name,
                            QuestionType.InputText.name,
                            QuestionType.InputNumber.name,
                            QuestionType.InputNumberEditText.name,
                            QuestionType.SingleSelectDropdown.name,
                            QuestionType.SingleSelectDropDown.name,
                            QuestionType.HrsMinPicker.name,
                            QuestionType.YrsMonthPicker.name -> {
                                val selectedOption =
                                    if (sectionDetails.questionAnswerMapping[question.questionId].isNullOrEmpty()) OptionItemEntity.getEmptyOptionItemEntity() else sectionDetails.questionAnswerMapping[question.questionId]?.first()
                                val optionList =
                                    sectionDetails.optionsItemMap[question.questionId]
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
                                val contentData =
                                    sectionDetails.questionContentMapping[question.questionId]

                                MiscQuestionBoxComponent(
                                    question = question.questionEntity,
                                    showQuestionState = question,
                                    questionIndex = index,
                                    contests = contentData,
                                    selectedOptionMapForNumericInputTypeQuestions = selectedOptionMapForNumericInputTypeQuestions,
                                    selectedOption = selectedOption,
                                    maxCustomHeight = maxHeight,
                                    questionStatusModel = QuestionStatusModel(
                                        isEditAllowed = questionScreenViewModel.isEditAllowed,
                                        isDidiReassigned = questionScreenViewModel.isDidiReassigned.value
                                    ),
                                    onAnswerSelection = { questionIndex, optionItem, selectedValue, selectedId ->


                                        when (optionItem.optionType) {
                                            QuestionType.Input.name,
                                            QuestionType.InputText.name,
                                            QuestionType.InputNumberEditText.name,
                                            QuestionType.SingleSelectDropdown.name,
                                            QuestionType.SingleSelectDropDown.name,
                                            QuestionType.HrsMinPicker.name,
                                            QuestionType.YrsMonthPicker.name -> {
                                                val mOptionItem =
                                                    optionItem.copy(
                                                        selectedValue = selectedValue,
                                                        selectedValueId = if (TextUtils.equals(
                                                                optionItem.optionType!!.toLowerCase(),
                                                                QuestionType.SingleSelectDropdown.name.toLowerCase()
                                                            )
                                                        ) selectedId else 0
                                                    )

                                                questionScreenViewModel.onEvent(
                                                    QuestionTypeEvent.UpdateConditionQuestionStateForSingleOption(
                                                        question,
                                                        mOptionItem
                                                    )
                                                )
                                            }

                                            QuestionType.InputNumber.name -> {
                                                val mOptionItem =
                                                    if (selectedValue != BLANK_STRING) optionItem.copy(
                                                        selectedValue = selectedValue
                                                    ) else optionItem.copy(selectedValue = "0")


                                                questionScreenViewModel.saveInputNumberOptionResponse(
                                                    questionId = question.questionId!!,
                                                    mOptionItem.optionId!!,
                                                    selectedValue = selectedValue
                                                )

                                                inputTypeQuestionAnswerEntityList?.value?.filter { it.questionId == question.questionId }
                                                    ?.let { inputTypeQuestionAnswerEntitiesForQuestion ->
                                                        val mOptionList =
                                                            ArrayList<OptionItemEntity>()
                                                        inputTypeQuestionAnswerEntitiesForQuestion.forEach { inputTypeQuestionAnswerEntity ->

                                                            question.optionItemEntityState.find { it.optionId == inputTypeQuestionAnswerEntity.optionId }?.optionItemEntity
                                                                ?.copy(selectedValue = inputTypeQuestionAnswerEntity.inputValue)
                                                                ?.let {
                                                                    mOptionList.add(it)
                                                                }
                                                        }
                                                        if (!mOptionList.map { it.optionId }
                                                                .contains(mOptionItem.optionId)) {
                                                            mOptionList.add(mOptionItem)
                                                        } else {
                                                            mOptionList.removeIf { it.optionId == mOptionItem.optionId }
                                                            mOptionList.add(mOptionItem)
                                                        }

                                                        questionScreenViewModel.onEvent(
                                                            QuestionTypeEvent.UpdateConditionQuestionStateForInputNumberOptions(
                                                                questionEntityState = question,
                                                                optionItemEntityList = mOptionList,
                                                                inputTypeQuestionEntity = inputTypeQuestionAnswerEntitiesForQuestion
                                                            )
                                                        )

                                                    }

                                                /*questionScreenViewModel.onEvent(
                                                    QuestionTypeEvent.UpdateConditionQuestionStateForInputNumberOptions(
                                                        question,
                                                        mOptionItem,
                                                        questionScreenViewModel.inputNumberQuestionMap[question.questionId]
                                                            ?: emptyList()
                                                    )
                                                )*/
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
                                            optionItem.copy(
                                                selectedValue = selectedValue,
                                                selectedValueId = if (TextUtils.equals(
                                                        optionItem.optionType!!.toLowerCase(),
                                                        QuestionType.SingleSelectDropdown.name.toLowerCase()
                                                    )
                                                ) selectedId else 0
                                            )
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
                                                            questionId = question.questionId
                                                                ?: -1,
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
                                                        optionItemId = optionItem.optionId
                                                            ?: -1,
                                                        inputValue = selectedValue
                                                    )
                                                )
                                            }

                                            QuestionType.Input.name,
                                            QuestionType.InputText.name,
                                            QuestionType.InputNumberEditText.name,
                                            QuestionType.HrsMinPicker.name,
                                            QuestionType.YrsMonthPicker.name -> {
                                                questionScreenViewModel.onEvent(
                                                    QuestionScreenEvents.SaveMiscTypeQuestionAnswers(
                                                        surveyeeId = surveyeeId,
                                                        questionEntityState = question,
                                                        optionItemEntity = optionItem,
                                                        selectedValue = selectedValue
                                                    )
                                                )
                                            }

                                            QuestionType.SingleSelectDropdown.name,
                                            QuestionType.SingleSelectDropDown.name -> {
                                                val mOption = optionItem.copy(
                                                    selectedValue = selectedValue,
                                                    selectedValueId = selectedId
                                                )
                                                questionScreenViewModel.onEvent(
                                                    QuestionScreenEvents.SaveMiscTypeQuestionAnswers(
                                                        surveyeeId = surveyeeId,
                                                        questionEntityState = question,
                                                        optionItemEntity = mOptionItem,
                                                        selectedValue = selectedValue
                                                    )
                                                )
                                            }
                                        }

                                        if (question.questionEntity.type == QuestionType.InputNumber.name) {
                                            questionScreenViewModel.onEvent(
                                                EventWriterEvents.SaveAnswerEvent(
                                                    surveyId = sectionDetails.surveyId,
                                                    sectionId = sectionDetails.sectionId,
                                                    didiId = surveyeeId,
                                                    questionId = question.questionId ?: 0,
                                                    questionType = question.questionEntity.type
                                                        ?: BLANK_STRING,
                                                    questionTag = question.questionEntity.tag,
                                                    questionDesc = question.questionEntity.questionDisplay
                                                        ?: BLANK_STRING,
                                                    showConditionalQuestion = !optionItem.conditions.isNullOrEmpty(),
                                                    saveAnswerEventOptionItemDtoList = inputTypeQuestionAnswerEntityList.value
                                                        .convertInputTypeQuestionToEventOptionItemDto(
                                                            question.questionId ?: 0,
                                                            QuestionType.InputNumber,
                                                            question.optionItemEntityState
                                                        )
                                                )
                                            )
                                        } else {
                                            questionScreenViewModel.onEvent(
                                                EventWriterEvents.SaveAnswerEvent(
                                                    surveyId = sectionDetails.surveyId,
                                                    sectionId = sectionDetails.sectionId,
                                                    didiId = surveyeeId,
                                                    questionId = question.questionId ?: 0,
                                                    questionType = question.questionEntity.type
                                                        ?: BLANK_STRING,
                                                    questionTag = question.questionEntity.tag,
                                                    questionDesc = question.questionEntity.questionDisplay
                                                        ?: BLANK_STRING,
                                                    showConditionalQuestion = !optionItem.conditions.isNullOrEmpty(),
                                                    saveAnswerEventOptionItemDtoList = mOptionItem.convertToSaveAnswerEventOptionItemDto(
                                                        QuestionType.getQuestionTypeFromName(
                                                            question.questionEntity.type
                                                                ?: BLANK_STRING
                                                        )!!
                                                    )
                                                )
                                            )
                                        }

                                        when (question.questionEntity.type) {
                                            QuestionType.Input.name,
                                            QuestionType.InputText.name,
                                            QuestionType.InputNumberEditText.name -> {
                                                answeredQuestionCountIncreased(
                                                    question,
                                                    selectedValue == BLANK_STRING
                                                )
                                            }

                                            else -> {
                                                answeredQuestionCountIncreased(question, false)
                                            }
                                        }

                                    },
                                    onMediaTypeDescriptionAction = { descriptionContentType, contentLink ->

                                    },
                                    questionDetailExpanded = {
                                        scope.launch {
                                            queLazyState.animateScrollToItem(it + 3, -10)
                                        }
                                    }
                                )
                            }
                        }
                    }
//                    }
                    if (sectionDetails.sectionName.contains(
                            context.getString(R.string.didi_info),
                            true
                        )
                    ) {
                        item {
                            Column {
                                if (questionScreenViewModel.didiInfoState.value != null) {
                                    DidiInfoCard(
                                        didiInfoEntity = questionScreenViewModel.didiInfoState.value!!,
                                        didiDetails = questionScreenViewModel.didiDetails.value,
                                        questionStatusModel = QuestionStatusModel(
                                            isEditAllowed = questionScreenViewModel.isEditAllowed,
                                            isDidiReassigned = questionScreenViewModel.isDidiReassigned.value
                                        ),
                                        onUpdate = {
                                            navController.navigateToBaseLineStartScreen(
                                                surveyeeId = surveyeeId,
                                                survyId = sectionDetails.surveyId,
                                                sectionId = sectionDetails.sectionId
                                            )
                                        }
                                    )

                                    Spacer(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(dimen_8_dp)
                                    )

                                }
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

}