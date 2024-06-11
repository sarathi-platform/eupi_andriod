package com.sarathi.smallgroupmodule.ui.smallGroupAttendanceHistory.presentation.event

sealed class SmallGroupAttendanceEvent {

    data class LoadSmallGroupDetailsForSmallGroupIdEvent(val smallGroupId: Int) :
        SmallGroupAttendanceEvent()

    data class MarkAttendanceForAllEvent(val checked: Boolean) : SmallGroupAttendanceEvent()

    data class MarkAttendanceForSubjectEvent(val checked: Boolean, val subjectId: Int) :
        SmallGroupAttendanceEvent()

    data class SubmitAttendanceForDateEvent(val result: (Boolean) -> Unit) :
        SmallGroupAttendanceEvent()

    object SaveAttendanceForDateToDbEvent : SmallGroupAttendanceEvent()

    object LoadSmallGroupAttendanceHistoryOnDateRangeUpdateEvent : SmallGroupAttendanceEvent()

    object UpdateAttendanceForDateEvent : SmallGroupAttendanceEvent()

    data class LoadSmallGroupAttendanceForGroupForDateEvent(
        val smallGroupId: Int,
        val selectedDate: Long
    )

}