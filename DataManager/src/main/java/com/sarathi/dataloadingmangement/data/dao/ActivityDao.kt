package com.sarathi.dataloadingmangement.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.sarathi.dataloadingmangement.data.entities.Activity
import com.sarathi.dataloadingmangement.util.ACTIVITY_TABLE_NAME
import com.sarathi.dataloadingmangement.util.TASK_TABLE_NAME

const val activityForSubject =
    "$ACTIVITY_TABLE_NAME.missionId missionId, $ACTIVITY_TABLE_NAME.activityId, $ACTIVITY_TABLE_NAME.activityName, $ACTIVITY_TABLE_NAME.activityType, " +
            "$ACTIVITY_TABLE_NAME.activityTypeId, $ACTIVITY_TABLE_NAME.doer, $ACTIVITY_TABLE_NAME.subject, $ACTIVITY_TABLE_NAME.reviewer, $TASK_TABLE_NAME.taskId, $TASK_TABLE_NAME.didiId, " +
            "$TASK_TABLE_NAME.actualStartDate, $TASK_TABLE_NAME.actualCompletedDate"

@Dao
interface ActivityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMissionActivity(activities: Activity)

    @Query("DELETE FROM $ACTIVITY_TABLE_NAME where userId=:userId")
    fun deleteActivities(userId: String)


    @Query("SELECT * FROM $ACTIVITY_TABLE_NAME where userId=:userId and missionId=:missionId and isActive=1")
    suspend fun getActivities(userId: String, missionId: Int): List<Activity>

    @Query("SELECT * FROM $ACTIVITY_TABLE_NAME where userId=:userId  and isActive=1")
    suspend fun getAllActivities(userId: String): List<Activity>

    @Query("SELECT * FROM $ACTIVITY_TABLE_NAME where  userId=:userId and activityId=:activityId and isActive=1")
    suspend fun getActivity(userId: String, activityId: Int): Activity

    @Query("SELECT count(*) FROM $ACTIVITY_TABLE_NAME where  userId=:userId and activityId=:activityId ")
    suspend fun getActivityCount(userId: String, activityId: Int): Int

    @Query("SELECT count(*) FROM $ACTIVITY_TABLE_NAME where  userId=:userId and missionId=:missionId and isActive=1")
    suspend fun getAllActivityCount(userId: String, missionId: Int): Int

    @Query("SELECT * FROM $ACTIVITY_TABLE_NAME where  userId=:userId and activityId=:activityId and isActive=1 ")
    suspend fun getActivitiesFormIds(userId: String, activityId: Int): Activity


    @Query("Update $ACTIVITY_TABLE_NAME set activityStatus = :status, pendingDidi=:pendingDidi where  userId=:userId and activityId = :activityId")
    fun updateActivityStatus(userId: String, activityId: Int, status: Int, pendingDidi: Int)

    @Query("Update $ACTIVITY_TABLE_NAME set isAllTask = :isAllTaskDone where userId=:userId and activityId = :activityId")
    fun updateActivityAllTaskStatus(userId: String, activityId: Int, isAllTaskDone: Boolean)

    @Query("Select * FROM $ACTIVITY_TABLE_NAME where userId=:userId and missionId in(:missionId) and isActive=1")
    fun isActivityExist(userId: String, missionId: Int): Boolean


    @Query("SELECT * from $ACTIVITY_TABLE_NAME where userId=:userId and  missionId = :missionId and activityId = :activityId and isActive=1")
    fun getActivity(userId: String, missionId: Int, activityId: Int): Activity

    @Query("UPDATE $ACTIVITY_TABLE_NAME SET status = :status WHERE  userId=:userId and activityId = :activityId AND missionId = :missionId")
    fun updateActivityStatus(userId: String, missionId: Int, activityId: Int, status: String)

    @Query("UPDATE $ACTIVITY_TABLE_NAME SET actualCompleteDate = :completedDate WHERE  userId=:userId and activityId = :activityId AND missionId = :missionId")
    fun updateCompletedDate(userId: String, missionId: Int, activityId: Int, completedDate: String)

    @Query("UPDATE $ACTIVITY_TABLE_NAME SET actualStartDate = :actualStartDate WHERE  userId=:userId and activityId = :activityId AND missionId = :missionId")
    fun updateStartDate(userId: String, missionId: Int, activityId: Int, actualStartDate: String)

    @Transaction
    fun markActivityComplete(
        userId: String,
        missionId: Int,
        activityId: Int,
        status: String,
        completedDate: String
    ) {
        updateActivityStatus(
            userId = userId,
            missionId = missionId,
            activityId = activityId,
            status = status
        )
        updateCompletedDate(
            userId = userId,
            missionId = missionId,
            activityId = activityId,
            completedDate = completedDate
        )
    }

    @Transaction
    fun markActivityStart(
        userId: String,
        missionId: Int,
        activityId: Int,
        status: String,
        actualStartDate: String
    ) {
        updateActivityStatus(
            userId = userId,
            missionId = missionId,
            activityId = activityId,
            status = status
        )
        updateStartDate(
            userId = userId,
            missionId = missionId,
            activityId = activityId,
            actualStartDate = actualStartDate
        )
    }

    @Query("SELECT * from $ACTIVITY_TABLE_NAME where  userId=:userId and missionId = :missionId and activityId = :activityId and isActive=1")
    fun isActivityCompleted(userId: String, missionId: Int, activityId: Int): Activity

    @Query("SELECT COUNT(*) from $ACTIVITY_TABLE_NAME where userId = :userId AND missionId = :missionId AND status != :status and isActive=1")
    fun getPendingTaskCountLiveForMission(
        userId: String,
        missionId: Int,
        status: String = ""
    ): LiveData<Int>

    @Query("SELECT COUNT(*) from $ACTIVITY_TABLE_NAME where userId = :userId AND missionId = :missionId and isActive=1")
    fun getTotalActivityCountForMission(userId: String, missionId: Int): Int

    @Query("UPDATE $ACTIVITY_TABLE_NAME SET isActive = 0 where  userId=:userId and missionId = :missionId")
    fun softDeleteActivity(missionId: Int, userId: String)

    @Query("UPDATE $ACTIVITY_TABLE_NAME SET isActive = :isActive where  userId=:userId and missionId = :missionId and activityId= :activityId")
    fun updateActivityActiveStatus(missionId: Int, userId: String, isActive: Int, activityId: Int)

    @Query("SELECT COUNT(*) from $ACTIVITY_TABLE_NAME where userId=:userId and missionId = :missionId AND status NOT in (:status) and isActive=1")
    fun getPendingActivity(
        userId: String,
        missionId: Int,
        status: List<String> = listOf("", "")
    ): Int


}