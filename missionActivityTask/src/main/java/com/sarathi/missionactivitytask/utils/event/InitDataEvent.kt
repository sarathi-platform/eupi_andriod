package com.sarathi.missionactivitytask.utils.event

import com.sarathi.dataloadingmangement.model.uiModel.TaskUiModel


sealed class InitDataEvent {
    object InitDataState : InitDataEvent()
    data class InitDisbursmentFormSummaryScreenState(
        val missionId: Int,
        val activityId: Int,
        val isFormSettingScreen: Boolean
    ) : InitDataEvent()

    data class InitGrantTaskScreenState(val missionId: Int, val activityId: Int) : InitDataEvent()
    data class InitTaskScreenState(val taskList: List<TaskUiModel>?) : InitDataEvent()

    data class InitSurveyTaskScreenState(val missionId: Int, val activityId: Int) : InitDataEvent()

}
