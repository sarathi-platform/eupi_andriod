package com.nrlm.baselinesurvey.model.datamodel

data class MissionActivityTaskDto(
    var missionId: Int,
    var missionStatus: String,
    var missionActualStartDate: String,
    var missionActualCompletedDate: String,
    var activityId: Int,
    var activityName: String,
    var activityType: String,
    var doer: String,
    var subject: String,
    var reviewer: String,
    var activityStatus: String,
    var activityActualStartDate: String,
    var activityActualCompletedDate: String,
    var taskId: Int,
    var didiId: Int,
    var taskStatus: String,
    var taskActualStartDate: String,
    var taskActualCompletedDate: String
)
