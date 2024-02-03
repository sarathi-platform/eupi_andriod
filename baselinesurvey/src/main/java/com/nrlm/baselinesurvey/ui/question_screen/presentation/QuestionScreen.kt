package com.nrlm.baselinesurvey.ui.question_screen.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.NO_SECTION
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.model.datamodel.SectionListItem
import com.nrlm.baselinesurvey.navigation.home.AddIncome_SCREEN_ROUTE_NAME
import com.nrlm.baselinesurvey.navigation.home.HomeScreens
import com.nrlm.baselinesurvey.navigation.home.VIDEO_PLAYER_SCREEN_ROUTE_NAME
import com.nrlm.baselinesurvey.ui.Constants.QuestionType
import com.nrlm.baselinesurvey.ui.common_components.CTAButtonComponent
import com.nrlm.baselinesurvey.ui.common_components.GridTypeComponent
import com.nrlm.baselinesurvey.ui.common_components.ListTypeQuestion
import com.nrlm.baselinesurvey.ui.common_components.LoaderComponent
import com.nrlm.baselinesurvey.ui.common_components.RadioQuestionBoxComponent
import com.nrlm.baselinesurvey.ui.common_components.SearchWithFilterViewComponent
import com.nrlm.baselinesurvey.ui.common_components.common_events.SearchEvent
import com.nrlm.baselinesurvey.ui.description_component.presentation.DescriptionContentComponent
import com.nrlm.baselinesurvey.ui.description_component.presentation.ImageExpanderDialogComponent
import com.nrlm.baselinesurvey.ui.description_component.presentation.ModelBottomSheetDescriptionContentComponent
import com.nrlm.baselinesurvey.ui.question_screen.viewmodel.QuestionScreenViewModel
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.ui.theme.defaultCardElevation
import com.nrlm.baselinesurvey.ui.theme.defaultTextStyle
import com.nrlm.baselinesurvey.ui.theme.dimen_10_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_16_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_24_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_3_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_80_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_8_dp
import com.nrlm.baselinesurvey.ui.theme.inactiveLightBlue
import com.nrlm.baselinesurvey.ui.theme.inactiveTextBlue
import com.nrlm.baselinesurvey.ui.theme.progressIndicatorColor
import com.nrlm.baselinesurvey.ui.theme.roundedCornerRadiusDefault
import com.nrlm.baselinesurvey.ui.theme.smallTextStyle
import com.nrlm.baselinesurvey.ui.theme.textColorDark
import com.nrlm.baselinesurvey.ui.theme.trackColor
import com.nrlm.baselinesurvey.ui.theme.white
import com.nrlm.baselinesurvey.utils.DescriptionContentType
import com.nrlm.baselinesurvey.utils.states.DescriptionContentState
import com.nrlm.baselinesurvey.utils.states.SectionStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun QuestionScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: QuestionScreenViewModel,
    surveyeeId: Int,
    sectionId: Int,
    nextSectionHandler: (sectionId: Int) -> Unit
) {

    val sectionDetails = viewModel.filterSectionList.value
    val loaderState = viewModel.loaderState.value


    LaunchedEffect(key1 = true) {
        viewModel.onEvent(LoaderEvent.UpdateLoaderState(true))
        viewModel.init(sectionId, surveyeeId)
        delay(300)
        viewModel.onEvent(LoaderEvent.UpdateLoaderState(false))
    }

    val scaffoldState =
        rememberModalBottomSheetState(ModalBottomSheetValue.Hidden, skipHalfExpanded = false)
    val scope = rememberCoroutineScope()

    val totalQuestionCount = sectionDetails.questionList.size
    val answeredQuestionCount = remember { mutableIntStateOf(sectionDetails.questionAnswerMapping.size) }

    val curPercentage = animateFloatAsState(
        targetValue =
        if (totalQuestionCount != 0)
            (answeredQuestionCount.value.toFloat() / totalQuestionCount.toFloat()).coerceIn(
                0F,
                totalQuestionCount.toFloat()
            )
        else
            0F,
        label = "",
        animationSpec = tween()
    )

    BackHandler {
        navController.popBackStack()
    }

    Scaffold(
        containerColor = white,
        modifier = Modifier.padding(top = dimen_10_dp),
        bottomBar = {
            BottomAppBar(containerColor = white, tonalElevation = defaultCardElevation, contentPadding = PaddingValues(horizontal = dimen_16_dp)) {
                Column {
                    /*LinearProgressIndicator(
                        modifier = Modifier
                            .height(dimen_6_dp)
                            .fillMaxWidth()
                            .clip(
                                RoundedCornerShape(
                                    topStart = roundedCornerRadiusDefault,
                                    topEnd = roundedCornerRadiusDefault
                                )
                            )
                            ,
                        color = greenOnline,
                        trackColor = Color.Transparent,
                        progress = curPercentage.value
                    )*/
                    ExtendedFloatingActionButton(
                        modifier = Modifier
                            .fillMaxWidth(),
//                        shape = RoundedCornerShape(bottomStart = roundedCornerRadiusDefault, bottomEnd = roundedCornerRadiusDefault),
                        shape = RoundedCornerShape(roundedCornerRadiusDefault),
                        containerColor = if (answeredQuestionCount.value == totalQuestionCount) blueDark else inactiveLightBlue,
                        contentColor = if (answeredQuestionCount.value == totalQuestionCount) white else inactiveTextBlue,
                        onClick = {
                            if (answeredQuestionCount.value == totalQuestionCount) {
                                viewModel.onEvent(
                                    QuestionScreenEvents.SectionProgressUpdated(
                                        surveyId = sectionDetails.surveyId,
                                        sectionId = sectionDetails.sectionId,
                                        didiId = surveyeeId,
                                        SectionStatus.COMPLETED
                                    )
                                )
                                viewModel.onEvent(QuestionScreenEvents.SendAnswersToServer(surveyId = sectionDetails.surveyId, sectionId = sectionDetails.sectionId, surveyeeId))
//                                navigateBackToSurveyeeListScreen(navController)
                                nextSectionHandler(sectionId)
                            }
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.save_next_section_button_text),
                            style = defaultTextStyle,
                            color = if (answeredQuestionCount.value == totalQuestionCount) white else inactiveTextBlue
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "save section button",
                            tint = if (answeredQuestionCount.value == totalQuestionCount) white else inactiveTextBlue,
                            modifier = Modifier.absolutePadding(top = dimen_3_dp)
                        )
                    }
                }
            }
        }
    ) {

        LoaderComponent(visible = loaderState.isLoaderVisible, modifier = Modifier.padding(it))

        if (!loaderState.isLoaderVisible) {

            ModelBottomSheetDescriptionContentComponent(
                sheetContent = {
                    DescriptionContentComponent(
                        buttonClickListener = {
                            scope.launch {
                                scaffoldState.hide()
                            }
                        },
                        imageClickListener = { imageTypeDescriptionContent ->
                            handleOnMediaTypeDescriptionActions(
                                viewModel,
                                navController,
                                DescriptionContentType.IMAGE_TYPE_DESCRIPTION_CONTENT,
                                imageTypeDescriptionContent
                            )
                        },
                        videoLinkClicked = { videoTypeDescriptionContent ->
                            handleOnMediaTypeDescriptionActions(
                                viewModel,
                                navController,
                                DescriptionContentType.VIDEO_TYPE_DESCRIPTION_CONTENT,
                                videoTypeDescriptionContent
                            )

                        },
                        descriptionContentState = DescriptionContentState(textTypeDescriptionContent = sectionDetails.sectionDetails)
                    )
                },
                sheetState = scaffoldState,
                sheetElevation = 20.dp,
                sheetBackgroundColor = Color.White,
                sheetShape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
            ) {
                if (viewModel.showExpandedImage.value) {
                    ImageExpanderDialogComponent(
                        viewModel.expandedImagePath.value
                    ) {
                        viewModel.showExpandedImage.value = false
                    }
                }

                NestedLazyList(
                    navController = navController,
                    sectionDetails = sectionDetails,
                    viewModel = viewModel,
                    surveyeeId = surveyeeId,
                    sectionInfoButtonClicked = {
                        scope.launch {
                            if (!scaffoldState.isVisible) {
                                scaffoldState.show()
                            } else {
                                scaffoldState.hide()
                            }
                        }
                    },
                    answeredQuestionCountIncreased = { count ->
                        answeredQuestionCount.value = count
                    }
                )
            }

            /*ModalBottomSheetLayout(
                sheetContent = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier
                    ) {

                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = dimen_10_dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.info_icon),
                                    contentDescription = "info icon"
                                )
                            }
                            Divider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp), color = greyBorder
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = dimen_16_dp, horizontal = dimen_18_dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = sectionDetails.sectionDetails,
                                    color = textColorDark,
                                    style = smallerTextStyleNormalWeight,
                                    modifier = Modifier.padding(horizontal = dimen_10_dp)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = dimen_16_dp, horizontal = dimen_24_dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Button(
                                    onClick = {
                                        scope.launch {
                                            scaffoldState.hide()
                                        }
                                    }, shape = RoundedCornerShape(
                                        roundedCornerRadiusDefault
                                    ), colors = ButtonDefaults.buttonColors(
                                        backgroundColor = blueDark,
                                        contentColor = white
                                    )
                                ) {
                                    Text(text = "Ok", color = white, style = smallerTextStyle)
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                },
                sheetState = scaffoldState,
                sheetElevation = 20.dp,
                sheetBackgroundColor = Color.White,
                sheetShape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
            ) {

                if (viewModel.showExpandedImage.value) {
                    ImageExpanderDialogComponent(
                        viewModel.expandedImagePath.value
                    ) {
                        viewModel.showExpandedImage.value = false
                    }
                }

                NestedLazyList(
                    navController = navController,
                    sectionDetails = sectionDetails,
                    viewModel = viewModel,
                    surveyeeId = surveyeeId,
                    sectionInfoButtonClicked = {
                        scope.launch {
                            if (!scaffoldState.isVisible) {
                                scaffoldState.show()
                            } else {
                                scaffoldState.hide()
                            }
                        }
                    },
                    answeredQuestionCountIncreased = { count ->
                        answeredQuestionCount.value = count
                    }
                )
            }*/
        }
    }
}


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
    answeredQuestionCountIncreased: (count: Int) -> Unit,
) {
    val questionScreenViewModel = (viewModel as QuestionScreenViewModel)

    val innerQueState: LazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val innerFirstVisibleItemIndex by remember {
        derivedStateOf {
            innerState.firstVisibleItemIndex
        }
    }

    val answeredQuestionCount = remember {
        mutableIntStateOf(sectionDetails.questionAnswerMapping.size)
    }

    val answeredQuestionIndices = remember {
        mutableStateOf(mutableListOf<Int>())
    }

    val curPercentage = animateFloatAsState(
        targetValue = answeredQuestionCount.value.toFloat()/sectionDetails.questionList.size.toFloat(),
        label = "" ,
        animationSpec = tween()
    )


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
                SearchWithFilterViewComponent(
                    placeholderString = stringResource(R.string.search_question_placeholder),
                    showFilter = false,
                    onFilterSelected = {

                    },
                    onSearchValueChange = { queryTerm ->
                        viewModel.onEvent(
                            SearchEvent.PerformSearch(
                                queryTerm,
                                false,
                                BLANK_STRING
                            )
                        )

                    }
                )
            }

            item {
                TopAppBar(
                    title = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            /*if (sectionDetails.sectionIcon != null) {
                                Icon(
                                    painter = painterResource(id = R.drawable.sample_step_icon_2),
                                    contentDescription = "section icon",
                                    tint = textColorDark
                                )
                                Spacer(modifier = Modifier.width(dimen_4_dp))
                            }*/
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

                        Icon(
                            painterResource(id = R.drawable.info_icon),
                            null,
                            tint = textColorDark,
                            modifier = Modifier.clickable {
                                sectionInfoButtonClicked()
                            }
                        )
                    },
                    backgroundColor = white,
                    elevation = 0.dp,

                    )
            }
            item {
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = dimensionResource(id = R.dimen.dp_15))
//                        .padding(vertical = dimensionResource(id = R.dimen.dp_15))
//                ) {
//                    if (!sectionDetails.sectionName.equals("Food Security", true)) {
//                        CTAButtonComponent(tittle = "Add Income Source", Modifier.fillMaxWidth()) {
//                            // navController.navigate(AddIncome_SCREEN_ROUTE_NAME)
//                            if (sectionDetails.sectionName.equals("Financial Inclusion", true))
//                                navController.navigate(AddIncome_SCREEN_ROUTE_NAME)
//                            if (sectionDetails.sectionName.equals("Social Inclusion", true))
//                                navController.navigate(AddHouseHoldMember_SCREEN_ROUTE_NAME)
//
//                        }
//                    }
//                }
            }

            item {
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
                        text = "${answeredQuestionCount.value}/${sectionDetails.questionList.size}",
                        color = textColorDark,
                        style = smallTextStyle
                    )
                }
            }
            item { Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(dimen_8_dp)) }
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
                        items = sectionDetails.questionList ?: emptyList()
                    ) { index, question ->

                        when (question?.type) {
                            QuestionType.RadioButton.name -> {
                                val selectedOption =
                                    sectionDetails.questionAnswerMapping[question.questionId]?.first()
                                val optionList = sectionDetails.optionsItemMap[question.questionId]
                                RadioQuestionBoxComponent(
                                    questionIndex = index, question = question,
                                    maxCustomHeight = maxHeight,
                                    optionItemEntityList = optionList,
                                    selectedOptionIndex = optionList?.indexOf(selectedOption)
                                        ?: -1,
                                    onAnswerSelection = { questionIndex, optionItem ->
                                        if (!answeredQuestionIndices.value.contains(questionIndex)) {
                                            answeredQuestionIndices.value.add(questionIndex)
                                            answeredQuestionCount.value =
                                                answeredQuestionCount.value.inc()
                                                    .coerceIn(0, sectionDetails.questionList.size)
                                            answeredQuestionCountIncreased(answeredQuestionCount.value)
                                        }
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
                                                questionEntity = question,
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
                                    question = question,
                                    optionItemEntityList = optionList ?: listOf(),
                                    selectedOptionIndex = optionList?.indexOf(selectedOption)
                                        ?: -1,
                                    questionIndex = index,
                                    maxCustomHeight = maxHeight,
                                    onAnswerSelection = { questionIndex, optionItem ->
                                        if (!answeredQuestionIndices.value.contains(questionIndex)) {
                                            answeredQuestionIndices.value.add(questionIndex)
                                            answeredQuestionCount.value =
                                                answeredQuestionCount.value.inc()
                                                    .coerceIn(0, sectionDetails.questionList.size)
                                            answeredQuestionCountIncreased(answeredQuestionCount.value)
                                        }

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
                                                questionEntity = question
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
                                selectedOption?.forEach {
                                    selectedIndices.add(optionList?.indexOf(it) ?: -1)
                                }
                                GridTypeComponent(
                                    question = question,
                                    questionIndex = index,
                                    optionItemEntityList = optionList,
                                    selectedOptionIndices = selectedIndices,
                                    maxCustomHeight = maxHeight,
                                    onAnswerSelection = { questionIndex, optionItems, selectedIndeciesCount ->
                                        if (!answeredQuestionIndices.value.contains(questionIndex)) {
                                            answeredQuestionIndices.value.add(questionIndex)
                                            if (selectedIndeciesCount.size <= 1) {
                                                answeredQuestionCount.value =
                                                    answeredQuestionCount.value.inc().coerceIn(
                                                        0,
                                                        sectionDetails.questionList.size
                                                    )
                                                answeredQuestionCountIncreased(answeredQuestionCount.value)
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

                                        questionScreenViewModel.onEvent(
                                            QuestionScreenEvents.GridTypeQuestionAnswered(
                                                surveyId = sectionDetails.surveyId,
                                                sectionId = sectionDetails.sectionId,
                                                didiId = surveyeeId,
                                                questionId = question.questionId ?: 0,
                                                optionItemList = sectionDetails.optionsItemMap[question.questionId]
                                                    ?: listOf(),
                                                questionEntity = question
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
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = dimensionResource(id = R.dimen.dp_15))
                                        .padding(vertical = dimensionResource(id = R.dimen.dp_15))
                                ) {
                                    CTAButtonComponent(
                                        tittle = question.questionDisplay,
                                        Modifier.fillMaxWidth()
                                    ) {
                                        navController.navigate("$AddIncome_SCREEN_ROUTE_NAME/${sectionDetails.surveyId}/${sectionDetails.sectionId}/${question.questionId}")
                                    }
//                                    if (!sectionDetails.sectionName.equals("Food Security", true)) {
//                                        CTAButtonComponent(tittle = "Add Income Source", Modifier.fillMaxWidth()) {
//                                            // navController.navigate(AddIncome_SCREEN_ROUTE_NAME)
//                                            if (sectionDetails.sectionName.equals("Financial Inclusion", true))
//                                                navController.navigate(AddIncome_SCREEN_ROUTE_NAME)
//                                            if (sectionDetails.sectionName.equals("Social Inclusion", true))
//                                                navController.navigate(AddHouseHoldMember_SCREEN_ROUTE_NAME)
//
//                                        }
//                                    }
                                }

                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier
                            .fillMaxWidth()
                            .height(dimen_80_dp + dimen_16_dp))
                    }

                }
            }
        }

    }
}

fun handleOnMediaTypeDescriptionActions(viewModel: QuestionScreenViewModel, navController: NavController, descriptionContentType: DescriptionContentType, contentLink: String) {
    if (descriptionContentType == DescriptionContentType.IMAGE_TYPE_DESCRIPTION_CONTENT) {
        viewModel.expandedImagePath.value = contentLink
        viewModel.showExpandedImage.value = true
    }
    if (descriptionContentType == DescriptionContentType.VIDEO_TYPE_DESCRIPTION_CONTENT) {
        navController.navigate("$VIDEO_PLAYER_SCREEN_ROUTE_NAME/$contentLink")
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showSystemUi = true, showBackground = true)
@Composable
fun TestPreviewPreview() {
    Scaffold(Modifier.fillMaxSize(), bottomBar = {
        BottomAppBar(containerColor = white, tonalElevation = defaultCardElevation, contentPadding = PaddingValues(horizontal = dimen_16_dp)) {
            ExtendedFloatingActionButton(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(roundedCornerRadiusDefault),
                containerColor = inactiveLightBlue,
                contentColor = inactiveTextBlue,
                onClick = {  }
            ) {
                Text(text = "Save & Next Section")
                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "save section button")
            }
        }
    }) {
        it
    }
}