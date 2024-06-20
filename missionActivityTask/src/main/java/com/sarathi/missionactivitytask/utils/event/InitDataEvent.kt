package com.sarathi.missionactivitytask.utils.event


sealed class InitDataEvent {
    object InitDataState : InitDataEvent()
    data class InitDisbursmentScreenState(val activityId: Int) : InitDataEvent()

}
