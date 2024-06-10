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
    fun insertActivityConfig(activityConfigEntity: ActivityConfigEntity)

    @Query("select activity_config_table.surveyId , activity_config_table.subject, section_table.sectionId from activity_config_table left join section_table  where activityId =:activityId  and  activity_config_table. userId =:userId and  section_table.userId =:userId ")
    fun getActivityConfigWithSection(
        activityId: Int,
        userId: String,
    ): ActivityConfigUiModel

    @Query("Select  DISTINCT surveyId from $ACTIVITY_CONFIG_TABLE_NAME")
    fun getSurveyIds(): List<Int>
}