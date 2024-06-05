package com.sarathi.dataloadingmangement.data.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sarathi.dataloadingmangement.ACTIVITY_CONFIG_TABLE_NAME
import com.sarathi.dataloadingmangement.data.entities.ActivityConfigEntity


@Dao
interface ActivityConfigDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertActivityConfig(activityConfigEntity: ActivityConfigEntity)

    @Query("Select  DISTINCT surveyId from $ACTIVITY_CONFIG_TABLE_NAME")
    fun getSurveyIds(): List<Int>
}