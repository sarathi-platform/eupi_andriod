package com.nudge.core.database.dao.api

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nudge.core.ApiCallConfigTable
import com.nudge.core.database.entities.api.ApiCallConfigEntity

@Dao
interface ApiCallConfigDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(apiCallConfigEntity: ApiCallConfigEntity)

    @Query("DELETE FROM $ApiCallConfigTable where userId=:userId")
    fun deleteApiCallConfigTable(userId: String)

    @Query("Select * FROM $ApiCallConfigTable where userId=:userId and screenName=:screenName  and triggerPoint=:triggerPoint order by apiOrder ")
    fun getApiCallConfigForScreen(
        screenName: String,
        userId: String,
        triggerPoint: String
    ): List<ApiCallConfigEntity>

}