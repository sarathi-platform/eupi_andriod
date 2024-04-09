package com.nrlm.baselinesurvey.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.nrlm.baselinesurvey.TASK_TABLE_NAME
import com.nrlm.baselinesurvey.database.entity.ActivityTaskEntity
import com.nrlm.baselinesurvey.utils.states.SurveyState

@Dao
interface ActivityTaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertActivityTask(activities: ActivityTaskEntity)

    @Query("DELETE FROM $TASK_TABLE_NAME where userId=:userId ")
    fun deleteActivityTask(userId: Int)

    @Query("SELECT * FROM $TASK_TABLE_NAME where userId=:userId")
    suspend fun getAllActivityTask(userId: Int): List<ActivityTaskEntity>

    @Query("SELECT * FROM $TASK_TABLE_NAME where  userId=:userId and taskId = :taskId")
    fun getTaskById(userId: Int, taskId: Int): ActivityTaskEntity

    @Query("SELECT * FROM $TASK_TABLE_NAME where userId=:userId and  missionId=:missionId and activityName like :activityName")
    suspend fun getActivityTask(
        userId: Int,
        missionId: Int,
        activityName: String
    ): List<ActivityTaskEntity>

    @Query("SELECT * FROM $TASK_TABLE_NAME where  userId=:userId and  activityId=:activityId ")
    suspend fun getActivityTaskFromIds(userId: Int, activityId: Int): List<ActivityTaskEntity>

    @Query("Select * FROM $TASK_TABLE_NAME where  userId=:userId and missionId in(:missionId) and activityName in(:activityName)")
    fun isTaskExist(userId: Int, missionId: Int, activityName: String): Boolean

    @Query("Select * from $TASK_TABLE_NAME where userId=:userId and  didiId = :subjectId")
    fun getTaskFromSubjectId(userId: Int, subjectId: Int): ActivityTaskEntity?

    @Query("UPDATE $TASK_TABLE_NAME set activityState = :surveyStatus where userId=:userId and didiId = :subjectId")
    fun updateTaskStatus(userId: Int, subjectId: Int, surveyStatus: Int)

    @Query("UPDATE $TASK_TABLE_NAME set status = :status where userId=:userId and taskId = :taskId AND activityId = :activityId AND missionId = :missionId")
    fun updateTaskStatus(userId: Int, taskId: Int, activityId: Int, missionId: Int, status: String)


    @Query("SELECT * FROM $TASK_TABLE_NAME where userId=:userId and  activityId=:activityId AND missionId = :missionId and taskId = :taskId")
    fun getTask(userId: Int, activityId: Int, missionId: Int, taskId: Int): ActivityTaskEntity

    @Query("UPDATE $TASK_TABLE_NAME SET actualStartDate = :actualStartDate where  userId=:userId and taskId = :taskId")
    fun updateTaskStartDate(userId: Int, taskId: Int, actualStartDate: String)

    @Query("UPDATE $TASK_TABLE_NAME SET actualCompletedDate = :actualCompletedDate where userId=:userId and  taskId = :taskId")
    fun updateTaskCompletedDate(userId: Int, taskId: Int, actualCompletedDate: String)

    @Transaction
    fun markTaskInProgress(
        userId: Int,
        taskId: Int,
        activityId: Int,
        missionId: Int,
        status: String,
        actualStartDate: String
    ) {
        updateTaskStatus(userId, taskId, activityId, missionId, status)
        updateTaskStartDate(userId, taskId, actualStartDate)
    }

    @Transaction
    fun markTaskCompleted(
        userId: Int,
        taskId: Int,
        activityId: Int,
        missionId: Int,
        status: String,
        actualCompletedDate: String
    ) {
        updateTaskStatus(userId, taskId, activityId, missionId, status)
        updateTaskCompletedDate(userId, taskId, actualCompletedDate)
    }

    @Query("SELECT COUNT(*) from $TASK_TABLE_NAME where  userId=:userId and activityId = :activityId AND status != :status")
    fun getPendingTaskCountLive(
        userId: Int,
        activityId: Int,
        status: String = SurveyState.COMPLETED.name
    ): LiveData<Int>

    @Query("SELECT COUNT(*) from $TASK_TABLE_NAME where  userId=:userId and missionId = :missionId")
    fun getTaskCountForMission(userId: Int, missionId: Int): Int

    @Query("SELECT COUNT(*) from $TASK_TABLE_NAME where  userId=:userId and missionId = :missionId AND status != :status")
    fun getPendingTaskCountLiveForMission(
        userId: Int,
        missionId: Int,
        status: String = SurveyState.COMPLETED.name
    ): LiveData<Int>

}