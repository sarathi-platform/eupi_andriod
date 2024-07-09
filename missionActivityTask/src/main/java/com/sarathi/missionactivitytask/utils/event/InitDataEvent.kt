package com.sarathi.missionactivitytask.utils.event


sealed class InitDataEvent {
    object InitDataState : InitDataEvent()

    data class InitDisbursmentScreenState(
        val missionId: Int,
        val activityId: Int,
        val isFormGenerated: Boolean
    ) : InitDataEvent()

}
