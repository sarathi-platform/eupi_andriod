package com.sarathi.dataloadingmangement.data.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.sarathi.dataloadingmangement.ACTIVITY_TABLE_NAME
import com.sarathi.dataloadingmangement.TASK_TABLE_NAME
import com.sarathi.dataloadingmangement.data.entities.ActivityEntity
import com.sarathi.dataloadingmangement.model.uiModel.ActivityUiModel
import com.sarathi.dataloadingmangement.util.constants.SurveyStatusEnum


const val activityForSubject =
    "$ACTIVITY_TABLE_NAME.missionId missionId, $ACTIVITY_TABLE_NAME.activityId, $ACTIVITY_TABLE_NAME.activityName, $ACTIVITY_TABLE_NAME.activityType, " +
            "$ACTIVITY_TABLE_NAME.activityTypeId, $ACTIVITY_TABLE_NAME.doer, $ACTIVITY_TABLE_NAME.subject, $ACTIVITY_TABLE_NAME.reviewer, $TASK_TABLE_NAME.taskId, $TASK_TABLE_NAME.didiId, " +
            "$TASK_TABLE_NAME.actualStartDate, $TASK_TABLE_NAME.actualCompletedDate"

@Dao
interface ActivityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMissionActivity(activities: ActivityEntity)

    @Query("DELETE FROM $ACTIVITY_TABLE_NAME where userId=:userId")
    fun deleteActivities(userId: String)

    @Query("UPDATE $ACTIVITY_TABLE_NAME SET isActive = 0 where  userId=:userId and missionId = :missionId")
    fun softDeleteActivity(missionId: Int, userId: String)

    @Query("UPDATE $ACTIVITY_TABLE_NAME SET isActive = :isActive where  userId=:userId and missionId = :missionId and activityId= :activityId")
    fun updateActivityActiveStatus(missionId: Int, userId: String, isActive: Int, activityId: Int)

    @Query("SELECT count(*) FROM $ACTIVITY_TABLE_NAME where  userId=:userId and activityId=:activityId ")
    suspend fun getActivityCount(userId: String, activityId: Int): Int

    @Query(
        "select activity_table.missionId,activity_table.activityId,  activity_language_attribute_table.description,  activity_table.status , \n" +
                "count(task_table.taskId) as taskCount,\n" +
                " SUM(CASE WHEN task_table.status  in(:surveyStatus)  THEN 1 ELSE 0 END) AS pendingTaskCount\n" +
                " from activity_table\n" +
                "inner join activity_language_attribute_table on activity_table.activityId = activity_language_attribute_table.activityId  \n" +
                "left join task_table on activity_table.activityId = task_table.activityId \n" +
                " where activity_language_attribute_table.languageCode =:languageCode and activity_table.isActive=1 and task_table.isActive=1 " +
                "and activity_table.userId =:userId and activity_table.missionId=:missionId group by task_table.activityId "
    )
    suspend fun getActivities(
        userId: String,
        languageCode: String,
        missionId: Int,
        surveyStatus: List<String> = listOf(
            SurveyStatusEnum.COMPLETED.name,
            SurveyStatusEnum.NOT_AVAILABLE.name
        )
    ): List<ActivityUiModel>

    @Query("SELECT COUNT(*) from $ACTIVITY_TABLE_NAME where userId=:userId and missionId = :missionId AND status NOT in (:status) and isActive=1")
    fun getPendingActivity(
        userId: String,
        missionId: Int,
        status: List<String> = listOf("", "")
    ): Int


    @Query("UPDATE $ACTIVITY_TABLE_NAME set status = :status where userId=:userId and missionId = :missionId and activityId=:activityId and isActive=1")
    fun updateActivityStatus(
        userId: String,
        activityId: Int,
        status: String,
        missionId: Int
    )

    @Query("UPDATE $ACTIVITY_TABLE_NAME set actualStartDate = :actualStartDate where userId=:userId and missionId = :missionId and activityId=:activityId and isActive=1")
    fun updateActivityActualStartDate(
        userId: String,
        activityId: Int,
        actualStartDate: String,
        missionId: Int
    )

    @Query("UPDATE $ACTIVITY_TABLE_NAME set actualEndDate = :actualEndDate where userId=:userId and missionId = :missionId and activityId=:activityId and isActive=1")
    fun updateActivityActualCompletedDate(
        userId: String,
        activityId: Int,
        actualEndDate: String,
        missionId: Int
    )


    @Transaction
    fun markActivityCompleted(
        userId: String,
        activityId: Int,
        actualEndDate: String,
        missionId: Int
    ) {
        updateActivityStatus(
            userId = userId,
            activityId = activityId,
            missionId = missionId,
            status = SurveyStatusEnum.COMPLETED.name
        )

        updateActivityActualCompletedDate(
            userId = userId,
            activityId = activityId,
            missionId = missionId,
            actualEndDate = actualEndDate
        )
    }

    @Transaction
    fun markActivityInProgress(
        userId: String,
        activityId: Int,
        actualStartDate: String,
        missionId: Int
    ) {
        updateActivityStatus(
            userId = userId,
            activityId = activityId,
            missionId = missionId,
            status = SurveyStatusEnum.INPROGRESS.name
        )
        updateActivityActualStartDate(
            userId = userId,
            activityId = activityId,
            missionId = missionId,
            actualStartDate = actualStartDate
        )
    }


    @Query("SELECT count(*) FROM $ACTIVITY_TABLE_NAME WHERE userId = :userId AND missionId=:missionId AND activityId=:activityId AND status IN (:statuses)")
    suspend fun countActivityByStatus(
        userId: String,
        missionId: Int,
        activityId: Int,
        statuses: List<String>
    ): Int


    @Query("SELECT count(*) FROM $ACTIVITY_TABLE_NAME WHERE userId = :userId AND missionId=:missionId AND status IN (:statuses)")
    suspend fun countActivityByStatus(
        userId: String,
        missionId: Int,
        statuses: List<String>
    ): Int


    @Query("Select * from activity_table where userId=:userId and activityId=:activityId and missionId=:missionId")
    suspend fun getActivity(userId: String, missionId: Int, activityId: Int): ActivityEntity?

}
