package com.nrlm.baselinesurvey.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.nrlm.baselinesurvey.ACTIVITY_TABLE_NAME
import com.nrlm.baselinesurvey.TASK_TABLE_NAME
import com.nrlm.baselinesurvey.database.entity.MissionActivityEntity
import com.nrlm.baselinesurvey.model.datamodel.ActivityForSubjectDto

const val activityForSubject =
    "$ACTIVITY_TABLE_NAME.missionId missionId, $ACTIVITY_TABLE_NAME.activityId, $ACTIVITY_TABLE_NAME.activityName, $ACTIVITY_TABLE_NAME.activityType, " +
            "$ACTIVITY_TABLE_NAME.activityTypeId, $ACTIVITY_TABLE_NAME.doer, $ACTIVITY_TABLE_NAME.subject, $ACTIVITY_TABLE_NAME.reviewer, $TASK_TABLE_NAME.taskId, $TASK_TABLE_NAME.didiId, " +
            "$TASK_TABLE_NAME.actualStartDate, $TASK_TABLE_NAME.actualCompletedDate"
@Dao
interface MissionActivityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMissionActivity(activities: MissionActivityEntity)

    @Query("DELETE FROM $ACTIVITY_TABLE_NAME where userId=:userId")
    fun deleteActivities(userId: Int)

//    @Query("SELECT * FROM $ACTIVITY_TABLE_NAME")
//    suspend fun getActivities(): List<MissionActivityEntity>

    @Query("SELECT * FROM $ACTIVITY_TABLE_NAME where userId=:userId and missionId=:missionId ")
    suspend fun getActivities(userId: Int, missionId: Int): List<MissionActivityEntity>

    @Query("SELECT * FROM $ACTIVITY_TABLE_NAME where  userId=:userId and activityId=:activityId ")
    suspend fun getActivity(userId: Int, activityId: Int): MissionActivityEntity

    @Query("SELECT * FROM $ACTIVITY_TABLE_NAME where  userId=:userId and activityId=:activityId ")
    suspend fun getActivitiesFormIds(userId: Int, activityId: Int): MissionActivityEntity
//    @Query("SELECT * FROM $ACTIVITY_TABLE_NAME where missionId=:missionId ")
//    suspend fun getActivities(missionId: Int,activityId: Int)

    @Query("Update $ACTIVITY_TABLE_NAME set activityStatus = :status, pendingDidi=:pendingDidi where  userId=:userId and activityId = :activityId")
    fun updateActivityStatus(userId: Int, activityId: Int, status: Int, pendingDidi: Int)

    @Query("Update $ACTIVITY_TABLE_NAME set isAllTask = :isAllTaskDone where userId=:userId and activityId = :activityId")
    fun updateActivityAllTaskStatus(userId: Int, activityId: Int, isAllTaskDone: Boolean)

    @Query("Select * FROM $ACTIVITY_TABLE_NAME where userId=:userId and missionId in(:missionId)")
    fun isActivityExist(userId: Int, missionId: Int): Boolean

    @Query("SELECT $activityForSubject FROM $ACTIVITY_TABLE_NAME LEFT JOIN $TASK_TABLE_NAME on $ACTIVITY_TABLE_NAME.activityId = $TASK_TABLE_NAME.activityId where $TASK_TABLE_NAME.userId=:userId and $TASK_TABLE_NAME.didiId = :subjectId")
    fun getActivityFromSubjectId(userId: Int, subjectId: Int): ActivityForSubjectDto

    @Query("SELECT * from $ACTIVITY_TABLE_NAME where userId=:userId and  missionId = :missionId and activityId = :activityId")
    fun getActivity(userId: Int, missionId: Int, activityId: Int): MissionActivityEntity

    @Query("UPDATE $ACTIVITY_TABLE_NAME SET status = :status WHERE  userId=:userId and activityId = :activityId AND missionId = :missionId")
    fun updateActivityStatus(userId: Int, missionId: Int, activityId: Int, status: String)

    @Query("UPDATE $ACTIVITY_TABLE_NAME SET actualCompleteDate = :completedDate WHERE  userId=:userId and activityId = :activityId AND missionId = :missionId")
    fun updateCompletedDate(userId: Int, missionId: Int, activityId: Int, completedDate: String)

    @Query("UPDATE $ACTIVITY_TABLE_NAME SET actualStartDate = :actualStartDate WHERE  userId=:userId and activityId = :activityId AND missionId = :missionId")
    fun updateStartDate(userId: Int, missionId: Int, activityId: Int, actualStartDate: String)

    @Transaction
    fun markActivityComplete(
        userId: Int,
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
        userId: Int,
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

    @Query("SELECT * from $ACTIVITY_TABLE_NAME where  userId=:userId and missionId = :missionId and activityId = :activityId")
    fun isActivityCompleted(userId: Int, missionId: Int, activityId: Int): MissionActivityEntity

}