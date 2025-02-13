package com.patsurvey.nudge.model.dataModel

data class RemoteQueryDto(
    val databaseName: String,
    val dbVersion: Int,
    val tableName: String,
    val query: String,
    val operationType: String,
    val appVersion: String,
)
