package com.nudge.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nudge.core.RequestStatusTable
import com.nudge.core.database.entities.RequestStatusEntity

@Dao
interface RequestStatusDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(requestStatusEntity: RequestStatusEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(requestStatusList: List<RequestStatusEntity>)

    @Query("SELECT * from $RequestStatusTable")
    fun getAllRequestEvent(): List<RequestStatusEntity>
}