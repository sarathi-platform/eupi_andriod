package com.nrlm.baselinesurvey.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.nrlm.baselinesurvey.ACTIVITY_TABLE_NAME
import com.nrlm.baselinesurvey.TASK_TABLE_NAME
import com.nrlm.baselinesurvey.database.entity.MissionActivityEntity
import com.nrlm.baselinesurvey.model.datamodel.ActivityForSubjectDto
import com.nrlm.baselinesurvey.utils.states.SurveyState

const val activityForSubject =
    "$ACTIVITY_TABLE_NAME.missionId missionId, $ACTIVITY_TABLE_NAME.activityId, $ACTIVITY_TABLE_NAME.activityName, $ACTIVITY_TABLE_NAME.activityType, " +
            "$ACTIVITY_TABLE_NAME.activityTypeId, $ACTIVITY_TABLE_NAME.doer, $ACTIVITY_TABLE_NAME.subject, $ACTIVITY_TABLE_NAME.reviewer, $TASK_TABLE_NAME.taskId, $TASK_TABLE_NAME.didiId, " +
            "$TASK_TABLE_NAME.actualStartDate, $TASK_TABLE_NAME.actualCompletedDate"
@Dao
interface MissionActivityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMissionActivity(activities: MissionActivityEntity)

    @Query("DELETE FROM $ACTIVITY_TABLE_NAME where userId=:userId")
    fun deleteActivities(userId: String)

//    @Query("SELECT * FROM $ACTIVITY_TABLE_NAME")
//    suspend fun getActivities(): List<MissionActivityEntity>

    @Query("SELECT * FROM $ACTIVITY_TABLE_NAME where userId=:userId and missionId=:missionId ")
    suspend fun getActivities(userId: String, missionId: Int): List<MissionActivityEntity>

    @Query("SELECT * FROM $ACTIVITY_TABLE_NAME where userId=:userId  ")
    suspend fun getAllActivities(userId: String): List<MissionActivityEntity>

    @Query("SELECT * FROM $ACTIVITY_TABLE_NAME where  userId=:userId and activityId=:activityId ")
    suspend fun getActivity(userId: String, activityId: Int): MissionActivityEntity

    @Query("SELECT count(*) FROM $ACTIVITY_TABLE_NAME where  userId=:userId and activityId=:activityId ")
    suspend fun getActivityCount(userId: String, activityId: Int): Int

    @Query("SELECT * FROM $ACTIVITY_TABLE_NAME where  userId=:userId and activityId=:activityId ")
    suspend fun getActivitiesFormIds(userId: String, activityId: Int): MissionActivityEntity
//    @Query("SELECT * FROM $ACTIVITY_TABLE_NAME where missionId=:missionId ")
//    suspend fun getActivities(missionId: Int,activityId: Int)

    @Query("Update $ACTIVITY_TABLE_NAME set activityStatus = :status, pendingDidi=:pendingDidi where  userId=:userId and activityId = :activityId")
    fun updateActivityStatus(userId: String, activityId: Int, status: Int, pendingDidi: Int)

    @Query("Update $ACTIVITY_TABLE_NAME set isAllTask = :isAllTaskDone where userId=:userId and activityId = :activityId")
    fun updateActivityAllTaskStatus(userId: String, activityId: Int, isAllTaskDone: Boolean)

    @Query("Select * FROM $ACTIVITY_TABLE_NAME where userId=:userId and missionId in(:missionId)")
    fun isActivityExist(userId: String, missionId: Int): Boolean

    @Query("SELECT $activityForSubject FROM $ACTIVITY_TABLE_NAME LEFT JOIN $TASK_TABLE_NAME on $ACTIVITY_TABLE_NAME.activityId = $TASK_TABLE_NAME.activityId where $TASK_TABLE_NAME.userId=:userId and $TASK_TABLE_NAME.didiId = :subjectId")
    fun getActivityFromSubjectId(userId: String, subjectId: Int): ActivityForSubjectDto

    @Query("SELECT * from $ACTIVITY_TABLE_NAME where userId=:userId and  missionId = :missionId and activityId = :activityId")
    fun getActivity(userId: String, missionId: Int, activityId: Int): MissionActivityEntity

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

    @Query("SELECT * from $ACTIVITY_TABLE_NAME where  userId=:userId and missionId = :missionId and activityId = :activityId")
    fun isActivityCompleted(userId: String, missionId: Int, activityId: Int): MissionActivityEntity

    @Query("SELECT COUNT(*) from $ACTIVITY_TABLE_NAME where missionId = :missionId AND status != :status")
    fun getPendingTaskCountLiveForMission(
        missionId: Int,
        status: String = SurveyState.COMPLETED.name
    ): LiveData<Int>

    @Query("SELECT COUNT(*) from $ACTIVITY_TABLE_NAME where missionId = :missionId")
    fun getTotalActivityCountForMission(missionId: Int): Int

}