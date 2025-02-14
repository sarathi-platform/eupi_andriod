package com.sarathi.dataloadingmangement.data.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sarathi.dataloadingmangement.ACTIVITY_LANGUAGE_ATTRIBUTE_TABLE_NAME
import com.sarathi.dataloadingmangement.data.entities.ActivityLanguageAttributesEntity
import com.sarathi.dataloadingmangement.model.uiModel.ActivityInfoUIModel


@Dao
interface ActivityLanguageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertActivityLanguage(activityLanguageAttributesEntity: ActivityLanguageAttributesEntity)

    @Query("delete from activity_language_attribute_table where userId=:userId")
    fun deleteActivityLanguageAttributeForUser(userId: String)

    @Query("delete from activity_language_attribute_table where userId=:userId and missionId=:missionId and activityId=:activityId")
    fun deleteActivityLanguageAttributeForActivity(userId: String, missionId: Int, activityId: Int)

    @Query(
        "select mission_language_table.description as missionName , activity_language_attribute_table.description as activityName ,mission_livelihood_config_table.livelihoodOrder as livelihoodOrder, \n" +
                "mission_livelihood_config_table.description as livelihoodType, mission_livelihood_config_table.programLivelihoodReferenceId \n" +
                "from $ACTIVITY_LANGUAGE_ATTRIBUTE_TABLE_NAME" +
                " left join mission_livelihood_config_table on activity_language_attribute_table.missionId=mission_livelihood_config_table.missionId and mission_livelihood_config_table.userId=:userId and mission_livelihood_config_table.languageCode=:languageCode " +
                " left join mission_language_table on activity_language_attribute_table.missionId=mission_language_table.missionId and mission_language_table.userId=:userId and mission_language_table.languageCode=:languageCode" +
                " where activity_language_attribute_table.missionId =:missionId  and  activity_language_attribute_table.activityId =:activityId and activity_language_attribute_table.userId=:userId and activity_language_attribute_table.languageCode=:languageCode"
    )
    fun fetchActivityInfo(
        missionId: Int,
        activityId: Int,
        userId: String,
        languageCode: String
    ): ActivityInfoUIModel?


}