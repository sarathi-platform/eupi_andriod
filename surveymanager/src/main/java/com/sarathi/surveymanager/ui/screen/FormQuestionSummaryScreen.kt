package com.sarathi.surveymanager.ui.screen

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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nudge.core.ui.commonUi.AlertDialogComponent
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.h6Bold
import com.nudge.core.ui.theme.textColorDark
import com.nudge.core.ui.theme.white
import com.nudge.core.value
import com.sarathi.dataloadingmangement.model.uiModel.SurveyConfigCardSlots
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.surveymanager.R
import com.sarathi.surveymanager.ui.component.FormResponseCard
import com.nudge.core.R as CoreRes

val DEFAULT_OPEN_DIALOG_VALUE = Pair<Boolean, Pair<String?, Int>?>(false, null)

@Composable
fun FormQuestionSummaryScreen(
    modifier: Modifier = Modifier,
    formResponseSummaryScreenViewModel: FormResponseSummaryViewModel = hiltViewModel(),
    navController: NavController,
    taskId: Int,
    surveyId: Int,
    sectionId: Int,
    formId: Int,
    activityConfigId: Int,
    onFormTypeQuestionClicked: (sectionId: Int, surveyId: Int, formId: Int, referenceId: String, taskId: Int, activityId: Int, activityConfigId: Int, missionId: Int, subjectType: String) -> Unit
) {

    LaunchedEffect(key1 = true) {
        formResponseSummaryScreenViewModel.init(
            taskId = taskId,
            surveyId = surveyId,
            sectionId = sectionId,
            formId = formId,
            activityConfigId = activityConfigId
        )
        formResponseSummaryScreenViewModel.onEvent(InitDataEvent.InitDataState)
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
                        text = stringResource(CoreRes.string.summary_text),
                        style = h6Bold,
                        color = textColorDark
                    )
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

                    formResponseSummaryScreenViewModel.deleteAnswer(openAlertDialog.value.second)

                    openAlertDialog.value = DEFAULT_OPEN_DIALOG_VALUE

                    if (formResponseSummaryScreenViewModel.referenceIdsList.isEmpty()) {
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

            item {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimen_16_dp)
                )
            }

            itemsIndexed(formResponseSummaryScreenViewModel.referenceIdsList) { index: Int, key: Pair<String, Int> ->
                FormResponseCard(
                    referenceId = key,
                    surveyAnswerFormSummaryUiModelList = formResponseSummaryScreenViewModel.sortedEntries.value.find { it.key == key }?.value.value(),
                    isEditAllowed = !formResponseSummaryScreenViewModel.isActivityCompleted,
                    surveyConfig = formResponseSummaryScreenViewModel.surveyConfig,
                    isPictureRequired = formResponseSummaryScreenViewModel.surveyConfig.containsKey(
                        SurveyConfigCardSlots.FORM_SUMMARY_CARD_IMAGE.name
                    ),
                    onDelete = { mReferenceId ->
                        openAlertDialog.value = Pair(true, mReferenceId)
                    },
                    onUpdate = { mReferenceId ->
                        val activityConfig = formResponseSummaryScreenViewModel.activityConfig
                        onFormTypeQuestionClicked(
                            formResponseSummaryScreenViewModel.sectionId,
                            formResponseSummaryScreenViewModel.surveyId,
                            formResponseSummaryScreenViewModel.formId,
                            mReferenceId.first,
                            formResponseSummaryScreenViewModel.taskId,
                            activityConfig?.activityId.value(),
                            formResponseSummaryScreenViewModel.activityConfigId,
                            activityConfig?.missionId.value(),
                            activityConfig?.subject.value()
                        )
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