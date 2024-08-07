package com.nudge.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nudge.core.ApiStatusTable
import com.nudge.core.database.entities.ApiStatusEntity

@Dao
interface ApiStatusDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(apiStatusEntity: ApiStatusEntity)

    @Query("SELECT * from $ApiStatusTable where api_end_point =:apiEndpoint ")
    fun getAPIStatus(apiEndpoint: String): ApiStatusEntity?

    @Query("SELECT count(*) from $ApiStatusTable where status = 2")
    fun getFailedAPICount(): Int

    @Query("update $ApiStatusTable set status =:status, error_message =:errorMessage, error_code=:errorCode where api_end_point =:apiEndpoint ")
    fun updateApiStatus(
        apiEndpoint: String, status: Int, errorMessage: String,
        errorCode: Int
    )

    @Query("DELETE FROM $ApiStatusTable")
    fun deleteApiStatus()
}
