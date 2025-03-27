package com.nudge.core.database.entities.api

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nudge.core.ApiCallJournalTable

@Entity(tableName = ApiCallJournalTable)
data class ApiCallJournalEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo("id") val id: Int,
    @ColumnInfo("userId") val userId: String,
    @ColumnInfo("apiName") val apiName: String,
    @ColumnInfo("apiUrl") val apiUrl: String,
    @ColumnInfo("status") val status: String,
    @ColumnInfo("moduleName") val moduleName: String,
    @ColumnInfo("screenName") val screenName: String,
    @ColumnInfo("requestBody") val requestBody: String?,
    @ColumnInfo("triggerPoint") val triggerPoint: String,
    @ColumnInfo("errorMsg") val errorMsg: String?,
    @ColumnInfo("retryCount") val retryCount: Int?,
    @ColumnInfo("createdDate") val createdDate: Long,
    @ColumnInfo("modifiedDate") val modifiedDate: Long
) {
    companion object {
        fun getApiCallJournalEntity(
            userId: String,
            apiName: String,
            apiUrl: String,
            status: String,
            moduleName: String,
            screenName: String,
            requestBody: String?,
            triggerPoint: String,
            errorMsg: String?,
            retryCount: Int?,
            createdDate: Long,
            modifiedDate: Long
        ): ApiCallJournalEntity {
            return ApiCallJournalEntity(
                id = 0,
                userId = userId,
                apiName = apiName,
                apiUrl = apiUrl,
                status = status,
                moduleName = moduleName,
                screenName = screenName,
                requestBody = requestBody,
                triggerPoint = triggerPoint,
                errorMsg = errorMsg,
                retryCount = retryCount,
                createdDate = createdDate,
                modifiedDate = modifiedDate
            )
        }
    }
}
