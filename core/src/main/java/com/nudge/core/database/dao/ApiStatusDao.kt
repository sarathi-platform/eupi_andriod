package com.nudge.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nudge.core.ApiStatusTable
import com.nudge.core.database.entities.ApiStatusEntity

@Dao
interface ApiStatusDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(apiStatusEntity: ApiStatusEntity)

    @Query("SELECT * from $ApiStatusTable where api_end_point =:apiEndpoint ")
    fun getAPIStatus(apiEndpoint: String): ApiStatusEntity

    @Query("update $ApiStatusTable set status =:status where api_end_point =:apiEndpoint ")
    fun updateApiStatus(apiEndpoint: String, status: Int)
}
