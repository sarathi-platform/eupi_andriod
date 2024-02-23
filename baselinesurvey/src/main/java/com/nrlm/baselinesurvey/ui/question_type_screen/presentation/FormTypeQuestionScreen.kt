package com.nrlm.baselinesurvey.ui.question_type_screen.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.DELAY_2_MS
import com.nrlm.baselinesurvey.ui.common_components.ButtonPositive
import com.nrlm.baselinesurvey.ui.common_components.LoaderComponent
import com.nrlm.baselinesurvey.ui.question_type_screen.presentation.component.NestedLazyListForFormQuestions
import com.nrlm.baselinesurvey.ui.question_type_screen.viewmodel.QuestionTypeScreenViewModel
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.ui.theme.defaultTextStyle
import com.nrlm.baselinesurvey.ui.theme.dimen_16_dp
import com.nrlm.baselinesurvey.ui.theme.textColorDark
import com.nrlm.baselinesurvey.ui.theme.white
import kotlinx.coroutines.delay

@Composable
fun FormTypeQuestionScreen(
    navController: NavHostController,
    viewModel: QuestionTypeScreenViewModel = hiltViewModel(),
    questionName: String = "",
    surveyID: Int = 0,
    sectionId: Int = 0,
    questionId: Int = 0,
    surveyeeId: Int,
    referenceId: String = BLANK_STRING
) {


    LaunchedEffect(key1 = true) {
        viewModel.onEvent(LoaderEvent.UpdateLoaderState(true))
        viewModel.init(sectionId, surveyID, questionId, surveyeeId, referenceId)
        delay(DELAY_2_MS)
        viewModel.onEvent(LoaderEvent.UpdateLoaderState(false))
    }

    val focusManager = LocalFocusManager.current

    val saveButtonActiveState = remember {
        derivedStateOf {
            referenceId.isNotBlank() || (viewModel.answeredOptionCount.intValue >= viewModel.totalOptionSize.value)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = questionName,
                        color = textColorDark,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start,
                        style = defaultTextStyle
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
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
                    buttonTitle = "Submit",
                    isActive = saveButtonActiveState.value,
                    isArrowRequired = false
                ) {
                    viewModel.onEvent(
                        QuestionTypeEvent.SaveCacheFormQuestionResponseToDbEvent(
                            viewModel.storeCacheForResponse
                        )
                    )
                    navController.popBackStack()
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
                        answeredQuestionCountIncreased = { count ->
                            viewModel.answeredOptionCount.value = count
                        },
                        onSaveFormTypeOption = { questionTypeEvent ->
                            viewModel.onEvent(
                                questionTypeEvent
                            )
                        },
                        saveCacheFormData = { formQuestionResponseEntity ->
                            viewModel.onEvent(QuestionTypeEvent.CacheFormQuestionResponseEvent(formQuestionResponseEntity))
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

@Preview(showBackground = true)
@Composable
private fun FormTypeQuestionScreenPreview() {
    MaterialTheme { FormTypeQuestionScreen(rememberNavController(), surveyeeId = 0) }

}