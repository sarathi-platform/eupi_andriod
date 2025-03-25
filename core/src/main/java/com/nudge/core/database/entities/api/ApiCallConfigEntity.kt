package com.nudge.core.database.entities.api

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nudge.core.ApiCallConfigTable

@Entity(tableName = ApiCallConfigTable)
data class ApiCallConfigEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo("id") val id: Int,
    @ColumnInfo("userId") val userId: String,
    @ColumnInfo("apiUrls") val apiUrls: String,
    @ColumnInfo("apiName") val apiName: String,
    @ColumnInfo("screenName") val screenName: String,
    @ColumnInfo("triggerPoint") val triggerPoint: String,
    @ColumnInfo("apiType") val apiType: String,
    @ColumnInfo("moduleName") val moduleName: String,
    @ColumnInfo("order") val order: Int,
    @ColumnInfo("isAsyncCall") val isAsyncCall: Boolean
)
