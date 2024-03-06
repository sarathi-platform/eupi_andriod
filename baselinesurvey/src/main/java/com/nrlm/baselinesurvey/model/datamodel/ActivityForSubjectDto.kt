package com.nrlm.baselinesurvey.model.datamodel

data class ActivityForSubjectDto(
    var missionId: Int,
    var activityId: Int,
    var activityName: String,
    var activityType: String,
    var doer: String,
    var subject: String,
    var reviewer: String,
    var taskId: Int,
    var didiId: Int
)
