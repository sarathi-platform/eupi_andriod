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
    fun deleteActivityTask(userId: String)

    @Query("SELECT * FROM $TASK_TABLE_NAME where userId=:userId")
    suspend fun getAllActivityTask(userId: String): List<ActivityTaskEntity>

    @Query("SELECT * FROM $TASK_TABLE_NAME where  userId=:userId and taskId = :taskId")
    fun getTaskById(userId: String, taskId: Int): ActivityTaskEntity

    @Query("SELECT count(*) FROM $TASK_TABLE_NAME where  userId=:userId and taskId = :taskId")
    fun getTaskByIdCount(userId: String, taskId: Int): Int


    @Query("SELECT * FROM $TASK_TABLE_NAME where userId=:userId and missionId=:missionId and activityId = :activityId")
    suspend fun getActivityTask(
        userId: String,
        missionId: Int,
        activityId: Int
    ): List<ActivityTaskEntity>

    @Query("SELECT * FROM $TASK_TABLE_NAME where  userId=:userId and  activityId=:activityId ")
    suspend fun getActivityTaskFromIds(userId: String, activityId: Int): List<ActivityTaskEntity>

    @Query("Select * FROM $TASK_TABLE_NAME where  userId=:userId and missionId in(:missionId) and activityName in(:activityName)")
    fun isTaskExist(userId: String, missionId: Int, activityName: String): Boolean

    @Query("Select * from $TASK_TABLE_NAME where userId=:userId and  didiId = :subjectId")
    fun getTaskFromSubjectId(userId: String, subjectId: Int): ActivityTaskEntity?

    @Query("UPDATE $TASK_TABLE_NAME set activityState = :surveyStatus where userId=:userId and didiId = :subjectId")
    fun updateTaskStatus(userId: String, subjectId: Int, surveyStatus: Int)

    @Query("UPDATE $TASK_TABLE_NAME set status = :status where userId=:userId and taskId = :taskId AND activityId = :activityId AND missionId = :missionId")
    fun updateTaskStatus(
        userId: String,
        taskId: Int,
        activityId: Int,
        missionId: Int,
        status: String
    )

    @Query("SELECT * FROM $TASK_TABLE_NAME where userId=:userId and  activityId=:activityId AND missionId = :missionId and taskId = :taskId")
    fun getTask(userId: String, activityId: Int, missionId: Int, taskId: Int): ActivityTaskEntity

    @Query("UPDATE $TASK_TABLE_NAME SET actualStartDate = :actualStartDate where  userId=:userId and taskId = :taskId")
    fun updateTaskStartDate(userId: String, taskId: Int, actualStartDate: String)

    @Query("UPDATE $TASK_TABLE_NAME SET actualCompletedDate = :actualCompletedDate where userId=:userId and  taskId = :taskId")
    fun updateTaskCompletedDate(userId: String, taskId: Int, actualCompletedDate: String)

    @Transaction
    fun markTaskInProgress(
        userId: String,
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
        userId: String,
        taskId: Int,
        activityId: Int,
        missionId: Int,
        status: String,
        actualCompletedDate: String
    ) {
        updateTaskStatus(userId, taskId, activityId, missionId, status)
        updateTaskCompletedDate(userId, taskId, actualCompletedDate)
    }

    @Query("SELECT COUNT(*) from $TASK_TABLE_NAME where userId=:userId and activityId = :activityId AND status NOT in (:status)")
    fun getPendingTaskCountLive(
        userId: String,
        activityId: Int,
        status: List<String> = listOf(SurveyState.COMPLETED.name, SurveyState.NOT_AVAILABLE.name)
    ): LiveData<Int>

    @Query("SELECT COUNT(*) from $TASK_TABLE_NAME where  userId=:userId and missionId = :missionId")
    fun getTaskCountForMission(userId: String, missionId: Int): Int

    @Query("SELECT COUNT(*) from $TASK_TABLE_NAME where  userId=:userId and missionId = :missionId AND status != :status")
    fun getPendingTaskCountLiveForMission(
        userId: String,
        missionId: Int,
        status: String = SurveyState.COMPLETED.name
    ): LiveData<Int>

}