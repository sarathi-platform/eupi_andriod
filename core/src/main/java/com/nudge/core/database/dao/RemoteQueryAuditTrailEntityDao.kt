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

}