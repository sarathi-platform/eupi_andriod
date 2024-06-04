package com.sarathi.smallgroupmodule.ui.smallGroupAttendanceHistory.presentation.event

sealed class SmallGroupAttendanceEvent {

    data class LoadSmallGroupDetailsForSmallGroupIdEvent(val smallGroupId: Int) :
        SmallGroupAttendanceEvent()

    data class MarkAttendanceForAll(val checked: Boolean) : SmallGroupAttendanceEvent()

    data class MarkAttendanceForSubject(val checked: Boolean, val subjectId: Int) :
        SmallGroupAttendanceEvent()

}