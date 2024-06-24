package com.sarathi.dataloadingmangement.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sarathi.dataloadingmangement.data.entities.FormUiConfigEntity


@Dao
interface FormUiConfigDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFormUiConfig(uiConfigEntity: FormUiConfigEntity)

    @Query("select * from form_ui_config_table where userId=:uniqueUserIdentifier and activityId=:activityId and missionId=:missionId and componentType=:formType")
    fun getActivityFormUiConfig(
        uniqueUserIdentifier: String,
        activityId: Int,
        missionId: Int,
        formType: String = "form"
    ): List<FormUiConfigEntity>

    @Query("Delete from  form_ui_config_table where  userId=:uniqueUserIdentifier and activityId=:activityId and missionId=:missionId")
    fun deleteActivityFormUiConfig(
        uniqueUserIdentifier: String,
        activityId: Int,
        missionId: Int,
    )

    @Query("Delete from  form_ui_config_table where  userId=:uniqueUserIdentifier ")
    fun deleteActivityFormUiConfigForUser(
        uniqueUserIdentifier: String,
    )

}