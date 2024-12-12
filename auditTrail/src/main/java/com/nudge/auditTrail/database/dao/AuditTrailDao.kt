package com.nudge.auditTrail.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nudge.auditTrail.entities.AuditTrailEntity
import com.nudge.core.AUDIT_TRAIL_TABLE
@Dao
interface AuditTrailDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(event: AuditTrailEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(events: List<AuditTrailEntity>)

    @Query("SELECT * from $AUDIT_TRAIL_TABLE")
    fun getAllEvent(): List<AuditTrailEntity>



    @Query("DELETE FROM $AUDIT_TRAIL_TABLE where id=:userId")
    fun deleteContent(userId: String)
    @Query("Select * from AUDIT_TRAIL_TABLE where id=:userId ")
    fun getAllContentKey(userId: String): List<AuditTrailEntity>

}
