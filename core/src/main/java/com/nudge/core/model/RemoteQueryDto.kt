package com.nudge.core.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RemoteQueryDto(
    @SerializedName("appVersion")
    @Expose
    val appVersion: String,
    @SerializedName("databaseName")
    @Expose
    val databaseName: String,
    @SerializedName("dbVersion")
    @Expose
    val dbVersion: Int,
    @SerializedName("executionOrder")
    @Expose
    val executionOrder: Int,
    @SerializedName("operationType")
    @Expose
    val operationType: String,
    @SerializedName("query")
    @Expose
    val query: String,
    @SerializedName("queryStatus")
    @Expose
    val queryStatus: String,
    @SerializedName("tableName")
    @Expose
    val tableName: String
)
