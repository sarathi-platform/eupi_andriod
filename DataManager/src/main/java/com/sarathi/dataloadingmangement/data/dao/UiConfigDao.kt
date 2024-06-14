package com.sarathi.dataloadingmangement.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sarathi.dataloadingmangement.data.entities.UiConfigEntity


@Dao
interface UiConfigDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUiConfig(uiConfigEntity: UiConfigEntity)

    @Query("select * from ui_config_table where missionId=:missionId and activityId=:activityId and userId=:uniqueUserIdentifier")
    fun getActivityUiConfig(
        missionId: Int,
        activityId: Int,
        uniqueUserIdentifier: String
    ): List<UiConfigEntity>

    @Query("Delete from  ui_config_table where  missionId=:missionId and activityId=:activityId and userId=:uniqueUserIdentifier")
    fun deleteActivityUiConfig(
        missionId: Int,
        activityId: Int,
        uniqueUserIdentifier: String
    )

}