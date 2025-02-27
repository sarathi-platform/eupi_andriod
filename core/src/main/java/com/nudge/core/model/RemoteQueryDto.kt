package com.nudge.core.model

data class RemoteQueryDto(
    val databaseName: String,
    val dbVersion: Int,
    val tableName: String,
    val query: String,
    val operationType: String,
    val appVersion: String,
    val executionOrder: Int,
    val status: String
)
