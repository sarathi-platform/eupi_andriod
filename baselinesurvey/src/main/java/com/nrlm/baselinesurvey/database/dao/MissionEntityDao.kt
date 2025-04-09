package com.nrlm.baselinesurvey.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.nrlm.baselinesurvey.ACTIVITY_TABLE_NAME
import com.nrlm.baselinesurvey.MISSION_TABLE_NAME
import com.nrlm.baselinesurvey.TASK_TABLE_NAME
import com.nrlm.baselinesurvey.database.entity.MissionEntity

const val missionActivityTaskDto =
    "$MISSION_TABLE_NAME.missionId missionId, $MISSION_TABLE_NAME.status missionStatus, $MISSION_TABLE_NAME.actualStartDate missionActualStartDate, $MISSION_TABLE_NAME.actualCompletedDate missionActualCompletedDate, $ACTIVITY_TABLE_NAME.activityId activityId," +
            " $ACTIVITY_TABLE_NAME.activityName activityName, $ACTIVITY_TABLE_NAME.activityType activityType, " +
            "$ACTIVITY_TABLE_NAME.activityTypeId activityTypeId, $ACTIVITY_TABLE_NAME.doer, $ACTIVITY_TABLE_NAME.subject, $ACTIVITY_TABLE_NAME.reviewer, " +
            "$ACTIVITY_TABLE_NAME.status activityStatus, $ACTIVITY_TABLE_NAME.actualStartDate activityActualStartDate, $ACTIVITY_TABLE_NAME.actualCompleteDate activityActualCompletedDate, " +
            "$TASK_TABLE_NAME.taskId, $TASK_TABLE_NAME.didiId, $TASK_TABLE_NAME.status taskStatus" +
            "$TASK_TABLE_NAME.actualStartDate taskActualStartDate, $TASK_TABLE_NAME.actualCompletedDate taskActualCompletedDate"

@Dao
interface MissionEntityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMission(missions: MissionEntity)

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insertMission(mission: MissionEntity)

    @Query("DELETE FROM $MISSION_TABLE_NAME where userId=:userId")
    fun deleteMissions(userId: String)

    @Query("SELECT * FROM $MISSION_TABLE_NAME where userId=:userId and isActive=1")
    suspend fun getMissions(userId: String): List<MissionEntity>

    @Query("SELECT * FROM $MISSION_TABLE_NAME where  userId=:userId and missionId=:missionId ")
    suspend fun getMission(userId: String, missionId: Int): MissionEntity?

    @Query("SELECT count(*) FROM $MISSION_TABLE_NAME where  userId=:userId and missionId=:missionId ")
    suspend fun getMissionCount(userId: String, missionId: Int): Int
    @Query("Update $MISSION_TABLE_NAME set pendingActivity=:pendingActivity, activityComplete=:activityComplete where  userId=:userId and missionId = :missionId")
    fun updateMissionStatus(
        userId: String,
        missionId: Int,
        activityComplete: Int,
        pendingActivity: Int
    )

//    @Query("SELECT $missionActivityTaskDto from $MISSION_TABLE_NAME JOIN $ACTIVITY_TABLE_NAME on $ACTIVITY_TABLE_NAME.missionId = $MISSION_TABLE_NAME.missionId JOIN $TASK_TABLE_NAME on $TASK_TABLE_NAME.missionId = $MISSION_TABLE_NAME.missionId where $TASK_TABLE_NAME.taskId = :taskId AND $TASK_TABLE_NAME.activityId = :activityId AND $TASK_TABLE_NAME.missionId = :missionId")
//    fun getMissionActivityTaskDto(missionId: Int, activityId: Int, taskId: Int): MissionActivityDao

    @Query("UPDATE $MISSION_TABLE_NAME SET status = :status where  userId=:userId and missionId = :missionId")
    fun updateMissionStatus(userId: String, missionId: Int, status: String)

    @Query("UPDATE $MISSION_TABLE_NAME SET actualStartDate = :actualStartDate where  userId=:userId and missionId = :missionId")
    fun updateActualStartDate(userId: String, missionId: Int, actualStartDate: String)

    @Query("UPDATE $MISSION_TABLE_NAME SET actualCompletedDate = :actualCompletedDate where  userId=:userId and missionId = :missionId")
    fun updateActualCompletedDate(userId: String, missionId: Int, actualCompletedDate: String)

    @Transaction
    fun markMissionCompleted(
        userId: String,
        missionId: Int,
        status: String,
        actualCompletedDate: String
    ) {
        updateMissionStatus(userId, missionId, status)
        updateActualCompletedDate(userId, missionId, actualCompletedDate)
    }

    @Transaction
    fun markMissionInProgress(
        userId: String,
        missionId: Int,
        status: String,
        actualStartDate: String
    ) {
        updateMissionStatus(userId, missionId, status)
        updateActualStartDate(userId, missionId, actualStartDate)
    }

    @Query("Update $MISSION_TABLE_NAME set isActive=0 where userId=:userId ")
    fun softDeleteMission(userId: String)

    @Query("Update $MISSION_TABLE_NAME set isActive=1 where userId=:userId  and missionId=:missionId")
    fun updateMissionActiveStatus(missionId: Int, userId: String)

}