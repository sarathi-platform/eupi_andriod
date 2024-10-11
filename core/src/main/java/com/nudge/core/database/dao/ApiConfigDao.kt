package com.nudge.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nudge.core.APP_CONFIG_TABLE
import com.nudge.core.database.entities.AppConfigEntity

@Dao
interface ApiConfigDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(appConfig: List<AppConfigEntity>)

    @Query("SELECT * from $APP_CONFIG_TABLE where `key`=:key and userId=:userId  and status=1")
    fun getConfig(key: String, userId: String): AppConfigEntity?


    @Query("DELETE FROM $APP_CONFIG_TABLE where userId=:userId")
    fun deleteAppConfig(userId: String)
}
