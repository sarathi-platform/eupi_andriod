package com.nrlm.baselinesurvey.ui.question_type_screen.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.DELAY_2_MS
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.ui.common_components.ButtonPositive
import com.nrlm.baselinesurvey.ui.common_components.LoaderComponent
import com.nrlm.baselinesurvey.ui.description_component.presentation.DescriptionContentComponent
import com.nrlm.baselinesurvey.ui.description_component.presentation.ModelBottomSheetDescriptionContentComponent
import com.nrlm.baselinesurvey.ui.question_type_screen.presentation.component.NestedLazyListForFormQuestions
import com.nrlm.baselinesurvey.ui.question_type_screen.viewmodel.QuestionTypeScreenViewModel
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.ui.theme.defaultTextStyle
import com.nrlm.baselinesurvey.ui.theme.dimen_16_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_18_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_1_dp
import com.nrlm.baselinesurvey.ui.theme.lightGray2
import com.nrlm.baselinesurvey.ui.theme.textColorDark
import com.nrlm.baselinesurvey.ui.theme.white
import com.nrlm.baselinesurvey.utils.BaselineCore
import com.nrlm.baselinesurvey.utils.showCustomToast
import com.nrlm.baselinesurvey.utils.states.DescriptionContentState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FormTypeQuestionScreen(
    navController: NavHostController,
    viewModel: QuestionTypeScreenViewModel = hiltViewModel(),
    surveyID: Int = 0,
    sectionId: Int = 0,
    questionId: Int = 0,
    surveyeeId: Int,
    referenceId: String = BLANK_STRING
) {
    val context = LocalContext.current

    val scope = rememberCoroutineScope()
    val scaffoldState =
        rememberModalBottomSheetState(ModalBottomSheetValue.Hidden, skipHalfExpanded = false)
    val selectedSectionDescription = remember {
        mutableStateOf(DescriptionContentState())
    }
    LaunchedEffect(key1 = true) {
        viewModel.onEvent(LoaderEvent.UpdateLoaderState(true))
        viewModel.init(sectionId, surveyID, questionId, surveyeeId, referenceId)
        delay(DELAY_2_MS)
        viewModel.onEvent(LoaderEvent.UpdateLoaderState(false))
    }

    val focusManager = LocalFocusManager.current

    val saveButtonActiveState = remember {
        derivedStateOf {
            /*referenceId.isNotBlank() || */(viewModel.answeredOptionCount.intValue >= viewModel.totalOptionSize.intValue)
        }
    }

    BackHandler {
        BaselineCore.setReferenceId(BLANK_STRING)
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
                    Icon(
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
                    imageClickListener = {
                    },
                    videoLinkClicked = {
                    },
                    descriptionContentState = selectedSectionDescription.value
                )
            }
        },
        sheetState = scaffoldState,
        sheetElevation = 20.dp,
        sheetBackgroundColor = Color.White,
        sheetShape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
    ) {

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = viewModel.question?.value?.questionDisplay ?: BLANK_STRING,
                            color = textColorDark,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start,
                            style = defaultTextStyle
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            BaselineCore.setReferenceId(BLANK_STRING)
                            navController.popBackStack()
                        }) {
                            Icon(Icons.Filled.ArrowBack, null, tint = textColorDark)
                        }
                    },
                    backgroundColor = Color.White,
                    elevation = 10.dp
                )
            },
            bottomBar = {
                BottomAppBar(
                    containerColor = white,
                    contentPadding = PaddingValues(dimen_16_dp)
                ) {
                    ButtonPositive(
                        buttonTitle = stringResource(id = R.string.submit),
                        isActive = saveButtonActiveState.value,
                        isArrowRequired = false
                    ) {
                        if (viewModel.storeCacheForResponse.isNotEmpty() && (viewModel.answeredOptionCount.intValue >= viewModel.totalOptionSize.intValue) && !viewModel.conditionalQuestionNotMarked) {
                            BaselineCore.setReferenceId(BLANK_STRING)
                            viewModel.onEvent(
                                QuestionTypeEvent.SaveCacheFormQuestionResponseToDbEvent(
                                    surveyId = surveyID,
                                    sectionId = sectionId,
                                    questionId = questionId,
                                    subjectId = surveyeeId,
                                    formQuestionResponseList = viewModel.storeCacheForResponse
                                )
                            )
                            navController.popBackStack()
                        } else {
                            showCustomToast(
                                context = context,
                                context.getString(R.string.madnatory_question_not_marked_error)
                            )
                        }
                    }
                }
            }
        ) {
            ConstraintLayout(modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(bottom = it.calculateTopPadding())
                .pointerInput(true) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                }
            ) {
                val (mainBox) = createRefs()
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .padding(top = it.calculateTopPadding())
                        .padding(bottom = it.calculateTopPadding())
                        .constrainAs(mainBox) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        },
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!viewModel.loaderState.value.isLoaderVisible) {
                        NestedLazyListForFormQuestions(
                            viewModel = viewModel,
                            answeredQuestionCountIncreased = {
                                viewModel.updateCachedData()
//                            viewModel.answeredOptionCount.value = count
                            },
                            onSaveFormTypeOption = { questionTypeEvent ->
                                viewModel.onEvent(
                                    questionTypeEvent
                                )
                            },
                            saveCacheFormData = { formQuestionResponseEntity ->
                                viewModel.onEvent(
                                    QuestionTypeEvent.CacheFormQuestionResponseEvent(
                                        formQuestionResponseEntity
                                    )
                                )
                            },
                            sectionInfoButtonClicked = { content ->
                                scope.launch {
                                    selectedSectionDescription.value =
                                        selectedSectionDescription.value.copy(
                                            textTypeDescriptionContent = viewModel.getContentData(
                                                content,
                                                "text"
                                            )?.contentValue
                                                ?: BLANK_STRING,
                                            imageTypeDescriptionContent = viewModel.getContentData(
                                                content,
                                                "image"
                                            )?.contentValue
                                                ?: BLANK_STRING,
                                            videoTypeDescriptionContent = viewModel.getContentData(
                                                content,
                                                "video"
                                            )?.contentValue
                                                ?: BLANK_STRING,
                                        )

                                    delay(100)
                                    if (!scaffoldState.isVisible) {
                                        scaffoldState.show()
                                    } else {
                                        scaffoldState.hide()
                                    }
                                }
                            }

                        )
                    }
                    LoaderComponent(
                        visible = viewModel.loaderState.value.isLoaderVisible,
                        modifier = Modifier.padding(it)
                    )
                }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FormTypeQuestionScreenPreview() {
    MaterialTheme { FormTypeQuestionScreen(rememberNavController(), surveyeeId = 0) }

}