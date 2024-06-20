package com.sarathi.dataloadingmangement.data.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.sarathi.dataloadingmangement.ACTIVITY_TABLE_NAME
import com.sarathi.dataloadingmangement.MISSION_TABLE_NAME
import com.sarathi.dataloadingmangement.TASK_TABLE_NAME
import com.sarathi.dataloadingmangement.data.entities.MissionEntity
import com.sarathi.dataloadingmangement.model.uiModel.MissionUiModel
import com.sarathi.dataloadingmangement.util.constants.SurveyStatusEnum


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

    @Query(
        "select mission_table.missionId, mission_language_table.description,  mission_table.status as missionStatus , \n" +
                "count(activity_table.activityId) as activityCount,\n" +
                " SUM(CASE WHEN activity_table.status = :status THEN 1 ELSE 0 END) AS pendingActivityCount\n" +
                " from mission_table\n" +
                "\n" +
                "inner join mission_language_table on mission_table.missionId = mission_language_table.missionId  \n" +
                "left join activity_table on mission_table.missionId = activity_table.missionId\n" +
                " where mission_language_table.languageCode =:languageCode and mission_table.isActive=1 and activity_table.isActive=1 and mission_table.userId=:userId " +
                "group by mission_table.missionId"
    )
    fun getMissions(
        userId: String,
        languageCode: String,
        status: String = SurveyStatusEnum.COMPLETED.name
    ): List<MissionUiModel>

    @Query("Update $MISSION_TABLE_NAME set isActive=0 where userId=:userId ")
    fun softDeleteMission(userId: String)

    @Query("Update $MISSION_TABLE_NAME set isActive=1 where userId=:userId  and missionId=:missionId")
    fun updateMissionActiveStatus(missionId: Int, userId: String)

    @Query("SELECT count(*) FROM $MISSION_TABLE_NAME where  userId=:userId and missionId=:missionId ")
    suspend fun getMissionCount(userId: String, missionId: Int): Int

    @Query("UPDATE $MISSION_TABLE_NAME set status = :status where userId=:userId and missionId = :missionId  and isActive=1")
    fun updateMissionStatus(
        userId: String,
        status: String,
        missionId: Int
    )

    @Query("Select * from mission_table where missionId=:missionId and userId=:userId")
    suspend fun getMission(
        userId: String,
        missionId: Int
    ): MissionEntity

    @Query("UPDATE mission_table set actualStartDate = :actualStartDate where userId=:userId and missionId = :missionId  and isActive=1")
    fun updateMissionActualStartDate(
        userId: String,
        actualStartDate: String,
        missionId: Int
    )

    @Query("UPDATE mission_table set activityComplete = :actualEndDate where userId=:userId and missionId = :missionId and isActive=1")
    fun updateMissionActualCompletedDate(
        userId: String,
        actualEndDate: String,
        missionId: Int
    )


    @Transaction
    fun markMissionCompleted(
        userId: String,
        actualEndDate: String,
        missionId: Int
    ) {
        updateMissionStatus(
            userId = userId,
            missionId = missionId,
            status = SurveyStatusEnum.COMPLETED.name
        )

        updateMissionActualCompletedDate(
            userId = userId,
            missionId = missionId,
            actualEndDate = actualEndDate
        )
    }

    @Transaction
    fun markMissionInProgress(
        userId: String,
        actualStartDate: String,
        missionId: Int
    ) {
        updateMissionStatus(
            userId = userId,
            missionId = missionId,
            status = SurveyStatusEnum.INPROGRESS.name
        )
        updateMissionActualStartDate(
            userId = userId,
            missionId = missionId,
            actualStartDate = actualStartDate
        )
    }

}