package com.sarathi.missionactivitytask.ui.grantTask.viewmodel

import androidx.compose.runtime.mutableStateOf
import com.nudge.core.CoreDispatchers
import com.sarathi.contentmodule.ui.content_screen.domain.usecase.FetchContentUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FetchAllDataUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetActivityUiConfigUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetActivityUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetTaskUseCase
import com.sarathi.dataloadingmangement.domain.use_case.MATStatusEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SaveSurveyAnswerUseCase
import com.sarathi.dataloadingmangement.domain.use_case.UpdateMissionActivityTaskStatusUseCase
import com.sarathi.dataloadingmangement.model.uiModel.TaskUiModel
import com.sarathi.missionactivitytask.ui.grantTask.domain.usecases.GetActivityConfigUseCase
import com.sarathi.missionactivitytask.utils.event.InitDataEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SurveyTaskScreenViewModel @Inject constructor(
    getTaskUseCase: GetTaskUseCase,
    surveyAnswerUseCase: SaveSurveyAnswerUseCase,
    getActivityUiConfigUseCase: GetActivityUiConfigUseCase,
    getActivityConfigUseCase: GetActivityConfigUseCase,
    fetchContentUseCase: FetchContentUseCase,
    taskStatusUseCase: UpdateMissionActivityTaskStatusUseCase,
    eventWriterUseCase: MATStatusEventWriterUseCase,
    getActivityUseCase: GetActivityUseCase,
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

    var taskUiList = mutableStateOf<List<TaskUiModel>>(emptyList())

    override fun <T> onEvent(event: T) {
        super.onEvent(event)
        when (event) {
            is InitDataEvent.InitSurveyTaskScreenState -> {
                initSurveyTaskScreen(event.missionId, event.activityId)
            }
        }
    }

    private fun initSurveyTaskScreen(missionId: Int, activityId: Int) {

        CoreDispatchers.launchViewModelScope(CoreDispatchers.ioDispatcher + exceptionHandler) {
            taskUiList.value =
                getTaskUseCase.getActiveTasks(missionId, activityId)
        }

    }

}