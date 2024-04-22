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

    @Query("DELETE FROM $TASK_TABLE_NAME")
    fun deleteActivityTask()

    @Query("SELECT * FROM $TASK_TABLE_NAME")
    suspend fun getAllActivityTask(): List<ActivityTaskEntity>

    @Query("SELECT * FROM $TASK_TABLE_NAME where taskId = :taskId")
    fun getTaskById(taskId: Int): ActivityTaskEntity

    @Query("SELECT * FROM $TASK_TABLE_NAME where missionId=:missionId and activityId = :activityId")
    suspend fun getActivityTask(missionId: Int, activityId: Int): List<ActivityTaskEntity>

    @Query("SELECT * FROM $TASK_TABLE_NAME where activityId=:activityId ")
    suspend fun getActivityTaskFromIds(activityId: Int): List<ActivityTaskEntity>

    @Query("Select * FROM $TASK_TABLE_NAME where missionId in(:missionId) and activityName in(:activityName)")
    fun isTaskExist(missionId: Int, activityName: String): Boolean

    @Query("Select * from $TASK_TABLE_NAME where didiId = :subjectId")
    fun getTaskFromSubjectId(subjectId: Int): ActivityTaskEntity?

    @Query("UPDATE $TASK_TABLE_NAME set activityState = :surveyStatus where didiId = :subjectId")
    fun updateTaskStatus(subjectId: Int, surveyStatus: Int)

    @Query("UPDATE $TASK_TABLE_NAME set status = :status where taskId = :taskId AND activityId = :activityId AND missionId = :missionId")
    fun updateTaskStatus(taskId: Int, activityId: Int, missionId: Int, status: String)


    @Query("SELECT * FROM $TASK_TABLE_NAME where activityId=:activityId AND missionId = :missionId and taskId = :taskId")
    fun getTask(activityId: Int, missionId: Int, taskId: Int): ActivityTaskEntity

    @Query("UPDATE $TASK_TABLE_NAME SET actualStartDate = :actualStartDate where taskId = :taskId")
    fun updateTaskStartDate(taskId: Int, actualStartDate: String)

    @Query("UPDATE $TASK_TABLE_NAME SET actualCompletedDate = :actualCompletedDate where taskId = :taskId")
    fun updateTaskCompletedDate(taskId: Int, actualCompletedDate: String)

    @Transaction
    fun markTaskInProgress(
        taskId: Int,
        activityId: Int,
        missionId: Int,
        status: String,
        actualStartDate: String
    ) {
        updateTaskStatus(taskId, activityId, missionId, status)
        updateTaskStartDate(taskId, actualStartDate)
    }

    @Transaction
    fun markTaskCompleted(
        taskId: Int,
        activityId: Int,
        missionId: Int,
        status: String,
        actualCompletedDate: String
    ) {
        updateTaskStatus(taskId, activityId, missionId, status)
        updateTaskCompletedDate(taskId, actualCompletedDate)
    }

    @Query("SELECT COUNT(*) from $TASK_TABLE_NAME where activityId = :activityId AND status NOT in (:status)")
    fun getPendingTaskCountLive(
        activityId: Int,
        status: List<String> = listOf(SurveyState.COMPLETED.name, SurveyState.NOT_AVAILABLE.name)
    ): LiveData<Int>

    @Query("SELECT COUNT(*) from $TASK_TABLE_NAME where missionId = :missionId")
    fun getTaskCountForMission(missionId: Int): Int

    @Query("SELECT COUNT(*) from $TASK_TABLE_NAME where missionId = :missionId AND status != :status")
    fun getPendingTaskCountLiveForMission(
        missionId: Int,
        status: String = SurveyState.COMPLETED.name
    ): LiveData<Int>

}