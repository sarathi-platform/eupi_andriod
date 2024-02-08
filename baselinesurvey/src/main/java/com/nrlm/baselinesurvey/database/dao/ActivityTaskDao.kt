package com.nrlm.baselinesurvey.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nrlm.baselinesurvey.TASK_TABLE_NAME
import com.nrlm.baselinesurvey.database.entity.ActivityTaskEntity

@Dao
interface ActivityTaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertActivityTask(activities: ActivityTaskEntity)

    @Query("DELETE FROM $TASK_TABLE_NAME")
    fun deleteActivityTask()

    @Query("SELECT * FROM $TASK_TABLE_NAME")
    suspend fun getAllActivityTask(): List<ActivityTaskEntity>

    @Query("SELECT * FROM $TASK_TABLE_NAME where missionId=:missionId and activityName like :activityName")
    suspend fun getActivityTask(missionId: Int, activityName: String): List<ActivityTaskEntity>
}