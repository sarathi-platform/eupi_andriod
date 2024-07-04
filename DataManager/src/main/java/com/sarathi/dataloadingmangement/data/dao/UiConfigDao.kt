package com.sarathi.dataloadingmangement.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sarathi.dataloadingmangement.data.entities.ActivityConfigEntity
import com.sarathi.dataloadingmangement.data.entities.UiConfigEntity
import com.sarathi.dataloadingmangement.model.uiModel.UiConfigModel


@Dao
interface UiConfigDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUiConfig(uiConfigEntity: UiConfigEntity)

    @Query("select  ui_config_table.type, ui_config_table.value,ui_config_table.`key`,ui_config_table.label,content_table.contentValue as icon,ui_config_table.componentType from ui_config_table left join  content_table on ui_config_table.icon = content_table.contentKey where ui_config_table.missionId=:missionId and ui_config_table.activityId=:activityId and ui_config_table.userId=:uniqueUserIdentifier and ui_config_table.language=:languageCode")
    fun getActivityUiConfig(
        missionId: Int,
        activityId: Int,
        uniqueUserIdentifier: String,
        languageCode: String
    ): List<UiConfigModel>

    @Query("Select * from activity_config_table where activityId=:activityId")
    fun getActivityConfig(activityId: Int): ActivityConfigEntity?

    @Query("Delete from  ui_config_table where  missionId=:missionId and activityId=:activityId and userId=:uniqueUserIdentifier")
    fun deleteActivityUiConfig(
        missionId: Int,
        activityId: Int,
        uniqueUserIdentifier: String
    )

    @Query("select icon from ui_config_table where icon <> ''")
    fun getAllIconsKey(): List<String>


    @Query("Delete from  ui_config_table where  userId=:uniqueUserIdentifier")
    fun deleteActivityUiConfigForUser(
        uniqueUserIdentifier: String
    )
}