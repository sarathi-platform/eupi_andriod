package com.nudge.core.database.dao.api

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nudge.core.ApiCallJournalTable
import com.nudge.core.database.entities.api.ApiCallJournalEntity

@Dao
interface ApiCallJournalDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(apiCallJournalEntity: ApiCallJournalEntity)

    @Query("DELETE FROM $ApiCallJournalTable where userId=:userId")
    suspend fun deleteApiCallJournalTable(userId: String)

    @Query("Select Count(*) from api_call_journal_table where userId=:uniqueUserIdentifier and apiUrl=:apiUrl and requestBody=:requestPayload")
    suspend fun isApiCallAlreadyExist(
        apiUrl: String,
        requestPayload: String,
        uniqueUserIdentifier: String
    ): Int

    @Query("Select * from api_call_journal_table where screenName=:screenName and moduleName=:moduleName  and  apiUrl=:apiUrl")
    fun getApiCallStatus(
        screenName: String,
        moduleName: String,
        apiUrl: String
    ): ApiCallJournalEntity?

    @Query("update api_call_journal_table set status=:status, screenName=:screenName , triggerPoint=:triggerPoint, errorMsg=:errorMsg, moduleName=:moduleName  where apiUrl=:apiUrl and requestBody=:requestBody and userId=:userId ")
    fun updateApiCallStatus(
        screenName: String,
        moduleName: String,
        triggerPoint: String,
        userId: String,
        apiUrl: String,
        requestBody: String,
        status: String,
        errorMsg: String
    )
}