package com.sarathi.missionactivitytask.utils.event


sealed class InitDataEvent {
    object InitDataState : InitDataEvent()
    data class InitDisbursmentScreenState(val missionId: Int, val activityId: Int) : InitDataEvent()
    data class InitGrantTaskScreenState(val missionId: Int, val activityId: Int) : InitDataEvent()

}
