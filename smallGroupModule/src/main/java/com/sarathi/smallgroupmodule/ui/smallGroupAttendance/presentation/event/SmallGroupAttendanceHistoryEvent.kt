package com.sarathi.smallgroupmodule.ui.smallGroupAttendance.presentation.event

sealed class SmallGroupAttendanceHistoryEvent {

    data class LoadSmallGroupDetailsForSmallGroupIdEvent(val smallGroupId: Int) :
        SmallGroupAttendanceHistoryEvent()

}