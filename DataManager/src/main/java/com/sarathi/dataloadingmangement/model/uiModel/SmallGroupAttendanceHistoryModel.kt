package com.sarathi.dataloadingmangement.model.uiModel

data class SmallGroupAttendanceHistoryModel(
    val id: Int,
    val subjectId: Int,
    var subjectType: String,
    var attribute: String,
    var date: String,
    var key: String,
    var value: String,
    val valueType: String,
)

data class SmallGroupAttendanceHistoryModelForEvent(
    val id: Int,
    val subjectId: Int,
    var subjectType: String,
    val smallGroupId: Int,
    var attribute: String,
    var date: String,
    var key: String,
    var value: String,
    val valueType: String,
)
