package com.nrlm.baselinesurvey.ui.form_response_summary_screen.presentation


import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.nrlm.baselinesurvey.navigation.home.navigateToFormTypeQuestionScreen
import com.nrlm.baselinesurvey.ui.common_components.FormResponseCard
import com.nrlm.baselinesurvey.ui.form_response_summary_screen.viewmodel.FormResponseSummaryScreenViewModel
import com.nrlm.baselinesurvey.ui.question_type_screen.presentation.QuestionTypeEvent
import com.nrlm.baselinesurvey.ui.theme.dimen_10_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_16_dp
import com.nrlm.baselinesurvey.ui.theme.h6Bold
import com.nrlm.baselinesurvey.ui.theme.textColorDark
import com.nrlm.baselinesurvey.ui.theme.white
import com.nrlm.baselinesurvey.utils.BaselineCore

@Composable
fun FormQuestionSummaryScreen(
    modifier: Modifier = Modifier,
    formResponseSummaryScreenViewModel: FormResponseSummaryScreenViewModel,
    navController: NavController,
    surveyId: Int,
    sectionId: Int,
    questionId: Int,
    surveyeeId: Int,
) {

    LaunchedEffect(key1 = true) {
        formResponseSummaryScreenViewModel.init(surveyId, sectionId, questionId, surveyeeId)
    }

    BackHandler {
        navController.popBackStack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = white,
                contentColor = textColorDark,
                title = {
                    Text(text = "Summary", style = h6Bold, color = textColorDark)
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            null,
                            tint = textColorDark,
                            modifier = Modifier
                        )
                    }
                }
            )
        }
    ) {
        it
        LazyColumn(
            modifier = Modifier.padding(horizontal = dimen_16_dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val optionItemEntityListWithConditions =
                formResponseSummaryScreenViewModel.getOptionItemListWithConditionals()
            item {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimen_16_dp)
                )
            }
            itemsIndexed(formResponseSummaryScreenViewModel.formResponseObjectDtoList.value) { index, formResponseObjectDto ->
                FormResponseCard(
                    formResponseObjectDto = formResponseObjectDto,
                    optionItemListWithConditionals = optionItemEntityListWithConditions,
                    viewModel = formResponseSummaryScreenViewModel,
                    onDelete = {
                        formResponseSummaryScreenViewModel.updateFormResponseObjectDtoList(
                            formResponseObjectDto.referenceId
                        )
                        formResponseSummaryScreenViewModel.onEvent(
                            QuestionTypeEvent.DeleteFormQuestionResponseEvent(
                                surveyId = surveyId,
                                sectionId = sectionId,
                                questionId = questionId,
                                surveyeeId = surveyeeId,
                                referenceId = formResponseObjectDto.referenceId
                            )
                        )
                    },
                    onUpdate = {
                        formResponseSummaryScreenViewModel.questionEntity?.let { it1 ->
                            BaselineCore.setReferenceId(formResponseObjectDto.referenceId)
                            navigateToFormTypeQuestionScreen(
                                navController = navController,
                                question = it1,
                                surveyId = surveyId,
                                sectionId = sectionId,
                                surveyeeId = surveyeeId
                            )
                        }
                    }
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimen_10_dp)
                )
            }
            item {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimen_16_dp)
                )
            }
        }
    }

}