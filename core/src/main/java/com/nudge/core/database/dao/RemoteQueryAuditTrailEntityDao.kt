package com.nudge.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.nudge.core.BLANK_STRING
import com.nudge.core.database.entities.RemoteQueryAuditTrailEntity

@Dao
interface RemoteQueryAuditTrailEntityDao {

    @Insert
    suspend fun insertRemoteQueryAuditTrailEntity(remoteQueryAuditTrailEntity: RemoteQueryAuditTrailEntity): Long

    @Query("UPDATE remote_query_edit_trail_table SET status = :status, errorMessage = :errorMessage, modifiedDate = :modifiedDate  WHERE userId = :userId and id = :id")
    suspend fun updateRemoteQueryAuditTrailEntityStatus(
        id: Int,
        status: String,
        errorMessage: String = BLANK_STRING,
        userId: String,
        modifiedDate: Long = System.currentTimeMillis()
    )

    @Query("SELECT * FROM remote_query_edit_trail_table WHERE userId = :userId AND databaseName = :databaseName AND dbVersion = :dbVersion AND tableName = :tableName AND operationType = :operationType AND appVersion = :appVersion AND `query` = :query")
    suspend fun isQueryAlreadyExecuted(
        userId: String,
        databaseName: String,
        dbVersion: Int,
        tableName: String,
        operationType: String,
        appVersion: String,
        query: String
    ): RemoteQueryAuditTrailEntity?

}