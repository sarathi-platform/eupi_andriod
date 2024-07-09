package com.sarathi.missionactivitytask.ui.grantTask.viewmodel

import androidx.compose.runtime.MutableState
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
import com.sarathi.dataloadingmangement.domain.use_case.GrantConfigUseCase
import com.sarathi.dataloadingmangement.domain.use_case.MATStatusEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SaveSurveyAnswerUseCase
import com.sarathi.dataloadingmangement.domain.use_case.UpdateTaskStatusUseCase
import com.sarathi.dataloadingmangement.model.uiModel.GrantTaskCardSlots
import com.sarathi.dataloadingmangement.util.constants.SurveyStatusEnum
import com.sarathi.missionactivitytask.ui.grantTask.domain.usecases.GetActivityConfigUseCase
import com.sarathi.missionactivitytask.utils.event.InitDataEvent
import com.sarathi.missionactivitytask.utils.event.LoaderEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class GrantTaskScreenViewModel @Inject constructor(
    private val getTaskUseCase: GetTaskUseCase,
    private val surveyAnswerUseCase: SaveSurveyAnswerUseCase,
    private val getActivityUiConfigUseCase: GetActivityUiConfigUseCase,
    private val getActivityConfigUseCase: GetActivityConfigUseCase,
    private val grantConfigUseCase: GrantConfigUseCase,
    private val fetchContentUseCase: FetchContentUseCase,
    private val taskStatusUseCase: UpdateTaskStatusUseCase,
    private val eventWriterUseCase: MATStatusEventWriterUseCase,
    private val getActivityUseCase: GetActivityUseCase,
    private val formUseCase: FormUseCase,
    private val formUiConfigUseCase: GetFormUiConfigUseCase,
    private val fetchAllDataUseCase: FetchAllDataUseCase,
) : TaskScreenViewModel(
    getTaskUseCase,
    surveyAnswerUseCase,
    getActivityUiConfigUseCase,
    getActivityConfigUseCase,
    grantConfigUseCase,
    fetchContentUseCase,
    taskStatusUseCase,
    eventWriterUseCase,
    getActivityUseCase,
    formUseCase,
    formUiConfigUseCase,
    fetchAllDataUseCase
) {
    private suspend fun <T> updateValueInMainThread(mutableState: MutableState<T>, newValue: T) {
        withContext(Dispatchers.Main) {
            mutableState.value = newValue
        }
    }

    override fun <T> onEvent(event: T) {
        when (event) {
            is InitDataEvent.InitDataState -> {
                initTaskScreen()
            }

            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(
                    isLoaderVisible = event.showLoader
                )
            }

        }
    }


    private suspend fun checkButtonValidation() {
        var isButtonEnablee = getTaskUseCase.isAllActivityCompleted(
            missionId = missionId,
            activityId = activityId
        ) && !isActivityCompleted.value
        updateValueInMainThread(isButtonEnable, isButtonEnablee)
    }


    private suspend fun isGenerateFormButtonEnable() {
        isGenerateFormButtonVisible.value =
            formUiConfigUseCase.getFormUiConfig(missionId = missionId, activityId = activityId)
                .isNotEmpty()
        if (isGenerateFormButtonVisible.value) {
            isGenerateFormButtonEnable.value =
                formUseCase.getNonGeneratedFormSummaryData(activityId)
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