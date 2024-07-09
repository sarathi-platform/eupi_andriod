package com.sarathi.missionactivitytask.ui.grantTask.viewmodel

import androidx.compose.runtime.mutableStateOf
import com.nudge.core.BLANK_STRING
import com.sarathi.contentmodule.ui.content_screen.domain.usecase.FetchContentUseCase
import com.sarathi.dataloadingmangement.DELEGATE_COMM
import com.sarathi.dataloadingmangement.SANCTIONED_AMOUNT_EQUAL_DISBURSED_FORM_E_GENERATED
import com.sarathi.dataloadingmangement.domain.use_case.FetchAllDataUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FormUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetActivityUiConfigUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetActivityUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetFormUiConfigUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetTaskUseCase
import com.sarathi.dataloadingmangement.domain.use_case.MATStatusEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SaveSurveyAnswerUseCase
import com.sarathi.dataloadingmangement.domain.use_case.UpdateMissionActivityTaskStatusUseCase
import com.sarathi.dataloadingmangement.model.uiModel.GrantTaskCardSlots
import com.sarathi.dataloadingmangement.util.constants.SurveyStatusEnum
import com.sarathi.missionactivitytask.ui.grantTask.domain.usecases.GetActivityConfigUseCase
import com.sarathi.missionactivitytask.utils.event.InitDataEvent
import com.sarathi.missionactivitytask.utils.event.LoaderEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class GrantTaskScreenViewModel @Inject constructor(
    getTaskUseCase: GetTaskUseCase,
    surveyAnswerUseCase: SaveSurveyAnswerUseCase,
    getActivityUiConfigUseCase: GetActivityUiConfigUseCase,
    getActivityConfigUseCase: GetActivityConfigUseCase,
    fetchContentUseCase: FetchContentUseCase,
    taskStatusUseCase: UpdateMissionActivityTaskStatusUseCase,
    eventWriterUseCase: MATStatusEventWriterUseCase,
    getActivityUseCase: GetActivityUseCase,
    private val formUseCase: FormUseCase,
    private val formUiConfigUseCase: GetFormUiConfigUseCase,
    fetchAllDataUseCase: FetchAllDataUseCase,
) : TaskScreenViewModel(
    getTaskUseCase,
    surveyAnswerUseCase,
    getActivityUiConfigUseCase,
    getActivityConfigUseCase,
    fetchContentUseCase,
    taskStatusUseCase,
    eventWriterUseCase,
    getActivityUseCase,
    fetchAllDataUseCase
) {
    var isGenerateFormButtonEnable = mutableStateOf(false)
    var isGenerateFormButtonVisible = mutableStateOf(false)

    override fun <T> onEvent(event: T) {
        when (event) {
            is InitDataEvent.InitGrantTaskScreenState -> {
                initGrantTaskScreen(event.missionId, event.activityId)
            }

            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(
                    isLoaderVisible = event.showLoader
                )
            }

        }
    }

    private fun initGrantTaskScreen(missionId: Int, activityId: Int) {

        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            isGenerateFormButtonEnable(missionId, activityId)

        }
    }


    private suspend fun isGenerateFormButtonEnable(missionId: Int, activityId: Int) {
        isGenerateFormButtonVisible.value =
            formUiConfigUseCase.getFormUiConfig(missionId = missionId, activityId = activityId)
                .isNotEmpty()
        if (isGenerateFormButtonVisible.value) {
            isGenerateFormButtonEnable.value =
                formUseCase.getNonGeneratedFormSummaryData(this.activityId)
                    .isNotEmpty() && !isActivityCompleted.value

        }
    }


    fun getTaskListOfDisburesementAmountEqualSanctionedAmount(): String {
        val taskListSanctionedEqualDisbursed = ArrayList<String>()
        if (activityConfigUiModel?.taskCompletion == SANCTIONED_AMOUNT_EQUAL_DISBURSED_FORM_E_GENERATED) {
            taskList.value.entries.forEach { task ->

                if (task.value[GrantTaskCardSlots.GRANT_TASK_SUBTITLE_5.name]?.value?.toInt() == task.value[GrantTaskCardSlots.GRANT_TASK_SUBTITLE_4.name]?.value?.toInt() && (task.value[GrantTaskCardSlots.GRANT_TASK_STATUS.name]?.value != SurveyStatusEnum.COMPLETED.name || task.value[GrantTaskCardSlots.GRANT_TASK_STATUS.name]?.value != SurveyStatusEnum.NOT_AVAILABLE.name)) {
                    taskListSanctionedEqualDisbursed.add(task.key.toString())
                }
            }
        } else {
            return BLANK_STRING
        }
        return taskListSanctionedEqualDisbursed.joinToString(DELEGATE_COMM)
    }


}