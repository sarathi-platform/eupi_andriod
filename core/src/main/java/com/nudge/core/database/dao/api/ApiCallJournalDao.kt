package com.nudge.core.database.dao.api

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.nudge.core.ApiCallJournalTable
import com.nudge.core.database.entities.api.ApiCallJournalEntity

@Dao
interface ApiCallJournalDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(apiCallJournalEntity: ApiCallJournalEntity)

    @Query("select * from $ApiCallJournalTable where userId=:userId and apiName=:apiName and status=:status ")
    fun getApiCallJournalEntity(userId: String, apiName: String, status: String): Int

    @Query("UPDATE $ApiCallJournalTable SET status = :status, retryCount = :retryCount,modifiedDate=:modifiedDate WHERE userId = :userId and  apiName = :apiName and status!=status")
    fun updateApiCallJournal(
        userId: String,
        apiName: String,
        status: String,
        retryCount: Int,
        modifiedDate: Long
    ): Int

    @Transaction
    fun insertOrUpdate(apiCallJournalEntity: ApiCallJournalEntity) {
        val existJournalEntity = getApiCallJournalEntity(
            userId = apiCallJournalEntity.userId,
            apiName = apiCallJournalEntity.apiName,
            status = apiCallJournalEntity.status
        )
        if (existJournalEntity == 0) {
            insert(apiCallJournalEntity)
        } else {
            updateApiCallJournal(
                userId = apiCallJournalEntity.userId,
                apiName = apiCallJournalEntity.apiName,
                status = apiCallJournalEntity.status,
                modifiedDate = apiCallJournalEntity.modifiedDate,
                retryCount = apiCallJournalEntity.retryCount ?: 0
            )
        }
    }

    @Query("DELETE FROM $ApiCallJournalTable where userId=:userId")
    fun deleteApiCallJournalTable(userId: String): Int
}