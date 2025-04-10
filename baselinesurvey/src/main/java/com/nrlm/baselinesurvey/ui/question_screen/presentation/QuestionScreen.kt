package com.nrlm.baselinesurvey.ui.question_screen.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.NO_SECTION
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.navigation.home.VIDEO_PLAYER_SCREEN_ROUTE_NAME
import com.nrlm.baselinesurvey.navigation.home.navigateBackToSurveyeeListScreen
import com.nrlm.baselinesurvey.ui.common_components.LoaderComponent
import com.nrlm.baselinesurvey.ui.description_component.presentation.DescriptionContentComponent
import com.nrlm.baselinesurvey.ui.description_component.presentation.ImageExpanderDialogComponent
import com.nrlm.baselinesurvey.ui.description_component.presentation.ModelBottomSheetDescriptionContentComponent
import com.nrlm.baselinesurvey.ui.question_screen.presentation.questionComponent.NestedLazyList
import com.nrlm.baselinesurvey.ui.question_screen.viewmodel.QuestionScreenViewModel
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.ui.theme.defaultCardElevation
import com.nrlm.baselinesurvey.ui.theme.defaultTextStyle
import com.nrlm.baselinesurvey.ui.theme.dimen_10_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_16_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_18_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_1_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_3_dp
import com.nrlm.baselinesurvey.ui.theme.inactiveLightBlue
import com.nrlm.baselinesurvey.ui.theme.inactiveTextBlue
import com.nrlm.baselinesurvey.ui.theme.lightGray2
import com.nrlm.baselinesurvey.ui.theme.roundedCornerRadiusDefault
import com.nrlm.baselinesurvey.ui.theme.white
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.nrlm.baselinesurvey.utils.DescriptionContentType
import com.nrlm.baselinesurvey.utils.states.DescriptionContentState
import com.nrlm.baselinesurvey.utils.states.SectionStatus
import com.nrlm.baselinesurvey.utils.states.SurveyState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun QuestionScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: QuestionScreenViewModel,
    surveyId: Int,
    surveyeeId: Int,
    sectionId: Int,
    nextSectionHandler: (sectionId: Int) -> Unit
) {
    val outerState= rememberLazyListState()
    val innerState= rememberLazyListState()

    val sectionDetails = viewModel.filterSectionList.value
    val loaderState = viewModel.loaderState.value


    LaunchedEffect(key1 = true) {
        try {
            viewModel.onEvent(LoaderEvent.UpdateLoaderState(true))
            viewModel.init(surveyId = surveyId, sectionId = sectionId, surveyeeId = surveyeeId)

        } catch (ex: Exception) {
            BaselineLogger.e("QuestionScreen", "LaunchedEffect -> exception: ${ex.message}", ex)
        }
    }

    val scaffoldState =
        rememberModalBottomSheetState(ModalBottomSheetValue.Hidden, skipHalfExpanded = false)
    val scope = rememberCoroutineScope()

//    val totalQuestionCount = sectionDetails.questionList.size
//    val answeredQuestionCount = remember { mutableIntStateOf(sectionDetails.questionAnswerMapping.size) }

    /*val curPercentage = animateFloatAsState(
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
    )*/

    BackHandler {
        navController.popBackStack()
    }

    ModelBottomSheetDescriptionContentComponent(
        sheetContent = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = {
                    scope.launch {
                        scaffoldState.hide()
                    }
                }) {
                    androidx.compose.material3.Icon(
                        painter = painterResource(id = R.drawable.info_icon),
                        contentDescription = "question info button",
                        Modifier.size(dimen_18_dp),
                        tint = blueDark
                    )
                }
                Divider(
                    thickness = dimen_1_dp,
                    color = lightGray2,
                    modifier = Modifier.fillMaxWidth()
                )
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
                    descriptionContentState = DescriptionContentState(
                        textTypeDescriptionContent = viewModel.contentMapping.value["text"]?.contentValue
                            ?: BLANK_STRING,
                        imageTypeDescriptionContent = viewModel.contentMapping.value["image"]?.contentValue
                            ?: BLANK_STRING,
                        videoTypeDescriptionContent = viewModel.contentMapping.value["video"]?.contentValue
                            ?: BLANK_STRING,
                    )
                )
            }
        },
        sheetState = scaffoldState,
        sheetElevation = 20.dp,
        sheetBackgroundColor = Color.White,
        sheetShape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
    ) {
        if (!loaderState.isLoaderVisible) {
            Scaffold(
                containerColor = white,
                modifier = Modifier
                    .padding(top = dimen_10_dp)
                    .fillMaxSize(),
                bottomBar = {
                    if (viewModel.didiDetails.value?.surveyStatus != SurveyState.COMPLETED.ordinal) {
                        BottomAppBar(
                            containerColor = white,
                            tonalElevation = defaultCardElevation
                        ) {
                            Box(modifier = Modifier.padding(horizontal = dimen_16_dp)) {
                                ExtendedFloatingActionButton(
                                    modifier = Modifier
                                        .fillMaxWidth(),
//                        shape = RoundedCornerShape(bottomStart = roundedCornerRadiusDefault, bottomEnd = roundedCornerRadiusDefault),
                                    shape = RoundedCornerShape(roundedCornerRadiusDefault),
                                    containerColor = if (viewModel.isSectionCompleted.value) blueDark else inactiveLightBlue,
                                    contentColor = if (viewModel.isSectionCompleted.value) white else inactiveTextBlue,
                                    onClick = {
                                        if (viewModel.isSectionCompleted.value) {
                                            viewModel.onEvent(
                                                QuestionScreenEvents.SectionProgressUpdated(
                                                    surveyId = sectionDetails.surveyId,
                                                    sectionId = sectionDetails.sectionId,
                                                    didiId = surveyeeId,
                                                    SectionStatus.COMPLETED
                                                )
                                            )
//                                viewModel.onEvent(QuestionScreenEvents.SendAnswersToServer(surveyId = sectionDetails.surveyId, sectionId = sectionDetails.sectionId, surveyeeId))

                                            if (sectionDetails.sectionName.equals(
                                                    NO_SECTION,
                                                    true
                                                )
                                            )
                                                navigateBackToSurveyeeListScreen(navController)
                                            else
                                                nextSectionHandler(sectionId)
                                        }
                                    }
                                ) {
                                    Text(
                                        text = stringResource(R.string.save_next_section_button_text),
                                        style = defaultTextStyle,
                                        color = if (viewModel.isSectionCompleted.value) white else inactiveTextBlue
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ArrowForward,
                                        contentDescription = "save section button",
                                        tint = if (viewModel.isSectionCompleted.value) white else inactiveTextBlue,
                                        modifier = Modifier.absolutePadding(top = dimen_3_dp)
                                    )
                                }
                            }
                        }
                    }
                }
            ) {

                LoaderComponent(
                    visible = loaderState.isLoaderVisible,
                    modifier = Modifier.padding(it)
                )
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
                    outerState = outerState,
                    innerState = innerState,
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
                    answeredQuestionCountIncreased = { question, isAllMultipleTypeQuestionUnanswered ->
                        viewModel.onEvent(
                            QuestionScreenEvents.UpdateAnsweredQuestionCount(
                                question,
                                isAllMultipleTypeQuestionUnanswered
                            )
                        )
                    }
                )
            }
        }
    }
}


fun handleOnMediaTypeDescriptionActions(
    viewModel: QuestionScreenViewModel,
    navController: NavController,
    descriptionContentType: DescriptionContentType,
    contentLink: String
) {
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
    Scaffold(
        Modifier
            .fillMaxSize()
            .background(white), containerColor = white, bottomBar = {
            BottomAppBar(
                containerColor = white,
                tonalElevation = defaultCardElevation,
                contentPadding = PaddingValues(horizontal = dimen_16_dp)
            ) {
                ExtendedFloatingActionButton(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(roundedCornerRadiusDefault),
                    containerColor = inactiveLightBlue,
                    contentColor = inactiveTextBlue,
                    onClick = { }
                ) {
                    Text(text = "Save & Next Section")
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "save section button"
                    )
            }
        }
    }) {
        it
    }
}