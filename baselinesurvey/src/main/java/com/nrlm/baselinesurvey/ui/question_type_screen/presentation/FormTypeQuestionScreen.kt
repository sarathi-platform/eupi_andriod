package com.nrlm.baselinesurvey.ui.question_type_screen.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
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
import com.nrlm.baselinesurvey.ui.question_type_screen.domain.entity.FormTypeOption
import com.nrlm.baselinesurvey.ui.question_type_screen.presentation.component.NestedLazyListForFormQuestions
import com.nrlm.baselinesurvey.ui.question_type_screen.viewmodel.QuestionTypeScreenViewModel
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.ui.theme.largeTextStyle
import com.nrlm.baselinesurvey.ui.theme.textColorDark
import kotlinx.coroutines.delay

@SuppressLint("UnrememberedMutableState")
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
        viewModel.init(sectionId, surveyID, questionId, referenceId)
        delay(DELAY_2_MS)
        viewModel.onEvent(LoaderEvent.UpdateLoaderState(false))
    }
    val totalOptionSize = mutableStateOf(viewModel.optionList.value.size)
    val answeredOptionCount = remember { mutableIntStateOf(0) }
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = questionName,
                        color = textColorDark,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start,
                        style = largeTextStyle
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensionResource(id = R.dimen.dp_15))
                    .padding(vertical = dimensionResource(id = R.dimen.dp_15))
            ) {
                ButtonPositive(
                    buttonTitle = questionName,
                    isActive = answeredOptionCount.value == totalOptionSize.value,
                    isArrowRequired = false
                ) {
                    viewModel.onEvent(
                        QuestionTypeEvent.StoreCacheFormQuestionResponseEvent(
                            viewModel._storeCacheForResponse.distinct()
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
            .padding(bottom = it.calculateTopPadding() + 70.dp)
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
                    .padding(top = it.calculateTopPadding() + 20.dp)
                    .padding(bottom = it.calculateTopPadding() + 100.dp)
                    .constrainAs(mainBox) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                    },
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!viewModel.loaderState.value.isLoaderVisible) {
                    var fromTypeOption = FormTypeOption.getOptionItem(
                        surveyID,
                        surveyeeId,
                        sectionId,
                        questionId,
                        viewModel.optionList.value
                    )
                    totalOptionSize.value = fromTypeOption.options.size
                    NestedLazyListForFormQuestions(
                        formTypeOption = fromTypeOption,
                        viewModel = viewModel,
                        answeredQuestionCountIncreased = { count ->
                            answeredOptionCount.value = count
                        },
                        onSaveFormTypeOption = { questionTypeEvent ->
                            viewModel.onEvent(
                                questionTypeEvent
                            )
                        },
                        saveCacheFormData = { formQuestionResponseEntity ->
                            val form = viewModel._storeCacheForResponse
                                .find { it.optionId == formQuestionResponseEntity.optionId }
                            if (form == null) {
                                answeredOptionCount.value.and(formQuestionResponseEntity.optionId)
                                answeredOptionCount.value = answeredOptionCount.value.inc()
                                viewModel._storeCacheForResponse.add(formQuestionResponseEntity)
                            } else {
                                form.selectedValue = formQuestionResponseEntity.selectedValue
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

@Preview(showBackground = true)
@Composable
private fun FormTypeQuestionScreenPreview() {
    MaterialTheme { FormTypeQuestionScreen(rememberNavController(), surveyeeId = 0) }

}