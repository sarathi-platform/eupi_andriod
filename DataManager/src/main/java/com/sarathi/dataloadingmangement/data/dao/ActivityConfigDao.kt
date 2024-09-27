package com.sarathi.dataloadingmangement.data.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sarathi.dataloadingmangement.ACTIVITY_CONFIG_TABLE_NAME
import com.sarathi.dataloadingmangement.data.entities.ActivityConfigEntity
import com.sarathi.dataloadingmangement.model.uiModel.ActivityConfigUiModel


@Dao
interface ActivityConfigDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertActivityConfig(activityConfigEntity: ActivityConfigEntity): Long

    @Query("select activity_config_table.surveyId ,activity_config_table.id  as  activityConfigId ,activity_config_table.subject,activity_config_table.taskCompletion, section_table.sectionId from activity_config_table left join section_table on activity_config_table.surveyId=section_table.surveyId  where activityId =:activityId and  activity_config_table. userId =:userId and  section_table.userId =:userId ")
    fun getActivityConfigWithSection(
        activityId: Int,
        userId: String,
    ): ActivityConfigUiModel

    @Query("Select  DISTINCT surveyId from $ACTIVITY_CONFIG_TABLE_NAME")
    fun getSurveyIds(): List<Int>

    @Query("Select  * from $ACTIVITY_CONFIG_TABLE_NAME where userId=:userId")
    fun getActivityConfigUiModel(userId: String): List<ActivityConfigEntity>?

    @Query("Select icon from $ACTIVITY_CONFIG_TABLE_NAME where userId=:userId and icon <> ''")
    fun getAllActivityIconsKey(userId: String): List<String>?

    @Query("Delete from activity_config_table where activityId=:activityId and userId=:userId")
    fun deleteActivityConfig(activityId: Int, userId: String)

    @Query("Select  subject from $ACTIVITY_CONFIG_TABLE_NAME where userId=:userId and activityId=:activityId and missionId=:missionId")
    fun getSubjectTypeForActivity(missionId: Int, activityId: Int, userId: String): String

    @Query("Delete from activity_config_table where userId=:userId")
    fun deleteActivityConfigForUser(userId: String)

    @Query("Select activityId from $ACTIVITY_CONFIG_TABLE_NAME where activityType=:activityType and userId = :userId")
    fun getActivityIdForLivelihood(activityType: String, userId: String): Int

    @Query("Select count(*) from $ACTIVITY_CONFIG_TABLE_NAME where activityType=:activityType and userId = :userId")
    suspend fun isActivityAvailable(activityType: String, userId: String): Int
}