package com.sarathi.dataloadingmangement.data.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sarathi.dataloadingmangement.ACTIVITY_TABLE_NAME
import com.sarathi.dataloadingmangement.MISSION_TABLE_NAME
import com.sarathi.dataloadingmangement.TASK_TABLE_NAME
import com.sarathi.dataloadingmangement.data.entities.MissionEntity


const val missionActivityTaskDto =
    "$MISSION_TABLE_NAME.missionId missionId, $MISSION_TABLE_NAME.status missionStatus, $MISSION_TABLE_NAME.actualStartDate missionActualStartDate, $MISSION_TABLE_NAME.actualCompletedDate missionActualCompletedDate, $ACTIVITY_TABLE_NAME.activityId activityId," +
            " $ACTIVITY_TABLE_NAME.activityName activityName, $ACTIVITY_TABLE_NAME.activityType activityType, " +
            "$ACTIVITY_TABLE_NAME.activityTypeId activityTypeId, $ACTIVITY_TABLE_NAME.doer, $ACTIVITY_TABLE_NAME.subject, $ACTIVITY_TABLE_NAME.reviewer, " +
            "$ACTIVITY_TABLE_NAME.status activityStatus, $ACTIVITY_TABLE_NAME.actualStartDate activityActualStartDate, $ACTIVITY_TABLE_NAME.actualCompleteDate activityActualCompletedDate, " +
            "$TASK_TABLE_NAME.taskId, $TASK_TABLE_NAME.didiId, $TASK_TABLE_NAME.status taskStatus" +
            "$TASK_TABLE_NAME.actualStartDate taskActualStartDate, $TASK_TABLE_NAME.actualCompletedDate taskActualCompletedDate"

@Dao
interface MissionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMission(missions: MissionEntity)

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insertMission(mission: MissionEntity)

    @Query("DELETE FROM $MISSION_TABLE_NAME where userId=:userId")
    fun deleteMissions(userId: String)

    @Query("select * from $MISSION_TABLE_NAME where userId=:userId")
    fun getMissions(userId: String): List<MissionEntity>

    @Query("Update $MISSION_TABLE_NAME set isActive=0 where userId=:userId ")
    fun softDeleteMission(userId: String)

    @Query("Update $MISSION_TABLE_NAME set isActive=1 where userId=:userId  and missionId=:missionId")
    fun updateMissionActiveStatus(missionId: Int, userId: String)

    @Query("SELECT count(*) FROM $MISSION_TABLE_NAME where  userId=:userId and missionId=:missionId ")
    suspend fun getMissionCount(userId: String, missionId: Int): Int

}