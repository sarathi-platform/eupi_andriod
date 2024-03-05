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

    @Query("SELECT * FROM $TASK_TABLE_NAME where activityId=:activityId ")
    suspend fun getActivityTaskFromIds(activityId: Int): List<ActivityTaskEntity>

    @Query("Select * FROM $TASK_TABLE_NAME where missionId in(:missionId) and activityName in(:activityName)")
    fun isTaskExist(missionId: Int, activityName: String): Boolean

    @Query("Select * from $TASK_TABLE_NAME where didiId = :subjectId")
    fun getTaskFromSubjectId(subjectId: Int): ActivityTaskEntity

    @Query("UPDATE $TASK_TABLE_NAME set activityState = :surveyStatus where didiId = :subjectId")
    fun updateTaskStatus(subjectId: Int, surveyStatus: Int)
}