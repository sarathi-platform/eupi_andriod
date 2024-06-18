package com.sarathi.dataloadingmangement.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.sarathi.dataloadingmangement.TASK_TABLE_NAME
import com.sarathi.dataloadingmangement.data.entities.ActivityTaskEntity
import com.sarathi.dataloadingmangement.model.uiModel.TaskUiModel


@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertActivityTask(activities: ActivityTaskEntity)

    @Query("DELETE FROM $TASK_TABLE_NAME where userId=:userId ")
    fun deleteActivityTask(userId: String)

    @Query("SELECT * FROM $TASK_TABLE_NAME where userId=:userId and isActive=1")
    suspend fun getAllActivityTask(userId: String): List<ActivityTaskEntity>

    @Query("SELECT * FROM $TASK_TABLE_NAME where  userId=:userId and taskId = :taskId and isActive=1")
    fun getTaskById(userId: String, taskId: Int): ActivityTaskEntity

    @Query("SELECT count(*) FROM $TASK_TABLE_NAME where  userId=:userId and taskId = :taskId")
    fun getTaskByIdCount(userId: String, taskId: Int): Int

    @Query("SELECT localTaskId FROM $TASK_TABLE_NAME where  subjectId=:didiId and userId = :userId")
    fun getTaskLocalId(userId: String, didiId: Int): String?

    @Query("UPDATE $TASK_TABLE_NAME set isActive = 0 where userId=:userId and activityId=:activityId and missionId = :missionId ")
    fun softDeleteActivityTask(userId: String, activityId: Int, missionId: Int)

    @Query("SELECT taskId,subjectId, status FROM $TASK_TABLE_NAME  where userId=:userId and missionId=:missionId and activityId = :activityId and isActive=1")
    suspend fun getActiveTask(
        userId: String,
        missionId: Int,
        activityId: Int
    ): List<TaskUiModel>

    @Query("SELECT * FROM $TASK_TABLE_NAME where  userId=:userId and  activityId=:activityId and isActive=1 ")
    suspend fun getActivityTaskFromIds(userId: String, activityId: Int): List<ActivityTaskEntity>


    @Query("UPDATE $TASK_TABLE_NAME set status = :status where userId=:userId and taskId = :taskId and subjectId=:subjectId")
    fun updateTaskStatus(
        userId: String,
        taskId: Int,
        status: String,
        subjectId: Int
    )

    @Query("UPDATE $TASK_TABLE_NAME set status = :status where userId=:userId and taskId = :taskId")
    fun updateTaskStatus(
        userId: String,
        taskId: Int,
        status: String
    )

    @Query("SELECT * FROM $TASK_TABLE_NAME where userId=:userId and  activityId=:activityId AND missionId = :missionId and taskId = :taskId")
    fun getTask(userId: String, activityId: Int, missionId: Int, taskId: Int): ActivityTaskEntity

    @Query("UPDATE $TASK_TABLE_NAME SET actualStartDate = :actualStartDate where  userId=:userId and taskId = :taskId")
    fun updateTaskStartDate(userId: String, taskId: Int, actualStartDate: String)

    @Query("UPDATE $TASK_TABLE_NAME SET actualCompletedDate = :actualCompletedDate where userId=:userId and  taskId = :taskId ")
    fun updateTaskCompletedDate(
        userId: String,
        taskId: Int,
        actualCompletedDate: String,
    )

    @Transaction
    fun markTaskInProgress(
        userId: String,
        taskId: Int,
        status: String,
        actualStartDate: String
    ) {
        updateTaskStatus(userId, taskId, status)
        updateTaskStartDate(userId, taskId, actualStartDate)
    }

    @Transaction
    fun markTaskCompleted(
        userId: String,
        taskId: Int,
        subjectId: Int,
        status: String,
        actualCompletedDate: String
    ) {
        updateTaskStatus(userId, taskId, status)
        updateTaskCompletedDate(userId, taskId, actualCompletedDate)
    }


    @Query("SELECT COUNT(*) from $TASK_TABLE_NAME where  userId=:userId and missionId = :missionId and isActive=1")
    fun getTaskCountForMission(userId: String, missionId: Int): Int


    @Query("UPDATE $TASK_TABLE_NAME SET isActive = :isActive where userId=:userId and  taskId = :taskId")
    fun updateActiveTaskStatus(isActive: Int, taskId: Int, userId: String)

    @Query("SELECT COUNT(*) from $TASK_TABLE_NAME where  userId=:userId and missionId = :missionId and activityId=:activityId and isActive=1")
    fun getTaskCountForActivity(userId: String, missionId: Int, activityId: Int): Int

    @Query("SELECT status from $TASK_TABLE_NAME where  userId=:userId and taskId = :taskId and subjectId=:subjectId")
    fun getTaskStatus(
        userId: String,
        taskId: Int,
        subjectId: Int
    ): String?

    @Query("SELECT count(*) FROM $TASK_TABLE_NAME WHERE userId = :userId AND activityId=:activityId AND missionId=:missionId AND status IN (:statuses)")
    suspend fun countTasksByStatus(
        userId: String,
        activityId: Int,
        missionId: Int,
        statuses: List<String>
    ): Int

}