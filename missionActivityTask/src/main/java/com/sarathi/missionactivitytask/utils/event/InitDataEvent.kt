package com.sarathi.missionactivitytask.utils.event

import com.sarathi.dataloadingmangement.model.uiModel.TaskUiModel


sealed class InitDataEvent {
    object InitDataState : InitDataEvent()
    data class InitDisbursmentScreenState(val missionId: Int, val activityId: Int,val isFormGenerated: Boolean) : InitDataEvent()
    data class InitGrantTaskScreenState(val missionId: Int, val activityId: Int) : InitDataEvent()
    data class InitTaskScreenState(val taskList: List<TaskUiModel>?) : InitDataEvent()

}
