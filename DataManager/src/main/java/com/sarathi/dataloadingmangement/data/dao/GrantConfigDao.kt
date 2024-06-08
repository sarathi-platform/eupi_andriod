package com.sarathi.dataloadingmangement.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sarathi.dataloadingmangement.data.entities.GrantConfigEntity


@Dao
interface GrantConfigDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGrantActivityConfig(grantConfig: GrantConfigEntity)

    @Query("select * from grant_config_table where activityConfigId=:activityConfigId")
    fun getGrantConfig(activityConfigId: Int): List<GrantConfigEntity>

    @Query("select * from grant_config_table where activityConfigId=:activityConfigId and grantId=:grantId")
    fun getGrantConfigWithGrantId(activityConfigId: Int, grantId: Int): GrantConfigEntity

    @Query("select grantComponent from grant_config_table where userId=:userId and surveyId=:surveyId and activityConfigId=:activityConfigId")
    fun getGrantComponent(userId: String, surveyId: Int, activityConfigId: Int): String
}