package com.sarathi.smallgroupmodule.ui.smallGroupAttendanceHistory.presentation.event

sealed class SmallGroupAttendanceHistoryEvent {

    data class LoadSmallGroupDetailsForSmallGroupIdEvent(val smallGroupId: Int) :
        SmallGroupAttendanceHistoryEvent()

}