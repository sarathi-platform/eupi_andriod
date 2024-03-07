package com.nrlm.baselinesurvey.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nrlm.baselinesurvey.ACTIVITY_TABLE_NAME
import com.nrlm.baselinesurvey.TASK_TABLE_NAME
import com.nrlm.baselinesurvey.database.entity.MissionActivityEntity
import com.nrlm.baselinesurvey.model.datamodel.ActivityForSubjectDto

const val activityForSubject =
    "$ACTIVITY_TABLE_NAME.missionId missionId, $ACTIVITY_TABLE_NAME.activityId, $ACTIVITY_TABLE_NAME.activityName, $ACTIVITY_TABLE_NAME.activityType, " +
            "$ACTIVITY_TABLE_NAME.activityTypeId, $ACTIVITY_TABLE_NAME.doer, $ACTIVITY_TABLE_NAME.subject, $ACTIVITY_TABLE_NAME.reviewer, $TASK_TABLE_NAME.taskId, $TASK_TABLE_NAME.didiId"
@Dao
interface MissionActivityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMissionActivity(activities: MissionActivityEntity)

    @Query("DELETE FROM $ACTIVITY_TABLE_NAME")
    fun deleteActivities()

//    @Query("SELECT * FROM $ACTIVITY_TABLE_NAME")
//    suspend fun getActivities(): List<MissionActivityEntity>

    @Query("SELECT * FROM $ACTIVITY_TABLE_NAME where missionId=:missionId ")
    suspend fun getActivities(missionId: Int): List<MissionActivityEntity>

    @Query("SELECT * FROM $ACTIVITY_TABLE_NAME where activityId=:activityId ")
    suspend fun getActivity(activityId: Int): MissionActivityEntity

    @Query("SELECT * FROM $ACTIVITY_TABLE_NAME where activityId=:activityId ")
    suspend fun getActivitiesFormIds(activityId: Int): MissionActivityEntity
//    @Query("SELECT * FROM $ACTIVITY_TABLE_NAME where missionId=:missionId ")
//    suspend fun getActivities(missionId: Int,activityId: Int)

    @Query("Update $ACTIVITY_TABLE_NAME set activityStatus = :status, pendingDidi=:pendingDidi where activityId = :activityId")
    fun updateActivityStatus(activityId: Int, status: Int, pendingDidi: Int)

    @Query("Update $ACTIVITY_TABLE_NAME set isAllTask = :isAllTaskDone where activityId = :activityId")
    fun updateActivityStatus(activityId: Int, isAllTaskDone: Boolean)

    @Query("Select * FROM $ACTIVITY_TABLE_NAME where missionId in(:missionId)")
    fun isActivityExist(missionId: Int): Boolean

    @Query("SELECT $activityForSubject FROM $ACTIVITY_TABLE_NAME LEFT JOIN $TASK_TABLE_NAME on $ACTIVITY_TABLE_NAME.activityId = $TASK_TABLE_NAME.activityId where $TASK_TABLE_NAME.didiId = :subjectId")
    fun getActivityFromSubjectId(subjectId: Int): ActivityForSubjectDto

}