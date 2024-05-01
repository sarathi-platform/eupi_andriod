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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.model.FormResponseObjectDto
import com.nrlm.baselinesurvey.ui.common_components.AlertDialogComponent
import com.nrlm.baselinesurvey.ui.common_components.FormResponseCard
import com.nrlm.baselinesurvey.ui.form_response_summary_screen.viewmodel.FormResponseSummaryScreenViewModel
import com.nrlm.baselinesurvey.ui.question_type_screen.presentation.QuestionTypeEvent
import com.nrlm.baselinesurvey.ui.theme.dimen_10_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_16_dp
import com.nrlm.baselinesurvey.ui.theme.h6Bold
import com.nrlm.baselinesurvey.ui.theme.textColorDark
import com.nrlm.baselinesurvey.ui.theme.white
import com.nrlm.baselinesurvey.utils.BaselineCore
import com.nudge.navigationmanager.graphs.navigateToFormTypeQuestionScreen

val DEFAULT_OPEN_DIALOG_VALUE = Pair<Boolean, FormResponseObjectDto?>(false, null)

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

    val openAlertDialog = remember { mutableStateOf(DEFAULT_OPEN_DIALOG_VALUE) }


    BackHandler {
        navController.popBackStack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = white,
                contentColor = textColorDark,
                title = {
                    Text(
                        text = "Summary",
                        style = h6Bold,
                        color = textColorDark
                    ) //TODO Remove Hard coded strings
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

        if (openAlertDialog.value.first) {
            AlertDialogComponent(
                onDismissRequest = { openAlertDialog.value = DEFAULT_OPEN_DIALOG_VALUE },
                onConfirmation = {
                    formResponseSummaryScreenViewModel.updateFormResponseObjectDtoList(
                        openAlertDialog.value.second?.referenceId!!
                    )
                    formResponseSummaryScreenViewModel.onEvent(
                        QuestionTypeEvent.DeleteFormQuestionResponseEvent(
                            surveyId = surveyId,
                            sectionId = sectionId,
                            questionId = questionId,
                            surveyeeId = surveyeeId,
                            questionDesc = formResponseSummaryScreenViewModel.questionEntity?.questionDisplay
                                ?: BLANK_STRING,
                            referenceId = openAlertDialog.value.second?.referenceId!!
                        )
                    )
                    openAlertDialog.value = DEFAULT_OPEN_DIALOG_VALUE
                    if (formResponseSummaryScreenViewModel.formResponseObjectDtoList.value.isEmpty()) {
                        navController.popBackStack()
                    }
                },
                dialogTitle = stringResource(R.string.alert_dialog_title_text),
                dialogText = stringResource(R.string.alart_dialog_entry_deleteion_message_text),
                confirmButtonText = stringResource(R.string.delete_text),
                dismissButtonText = stringResource(R.string.cancel_text)
            )
        }

        LazyColumn(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = dimen_16_dp),
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
                    isPictureRequired = formResponseObjectDto.questionTag.equals(
                        stringResource(R.string.household_information_comparision),
                        true
                    ),
                    viewModel = formResponseSummaryScreenViewModel,
                    onDelete = {
                        openAlertDialog.value = Pair(true, formResponseObjectDto)
                    },
                    onUpdate = {
                        formResponseSummaryScreenViewModel.questionEntity?.let { it1 ->
                            BaselineCore.setReferenceId(formResponseObjectDto.referenceId)
                            BaselineCore.setIsEditAllowedForNoneMarkedQuestionFlag(false)

                            navController.navigateToFormTypeQuestionScreen(
                                questionId = it1.questionId?:0,
                                questionDisplay = it1.questionDisplay?: BLANK_STRING,
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