package com.nrlm.baselinesurvey.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nrlm.baselinesurvey.ACTIVITY_TABLE_NAME
import com.nrlm.baselinesurvey.database.entity.MissionActivityEntity

@Dao
interface MissionActivityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMissionActivity(activities: MissionActivityEntity)

    @Query("DELETE FROM $ACTIVITY_TABLE_NAME")
    fun deleteActivities()

//    @Query("SELECT * FROM $ACTIVITY_TABLE_NAME")
//    suspend fun getActivities(): List<MissionActivityEntity>

    @Query("SELECT * FROM $ACTIVITY_TABLE_NAME where missionId=:missionId ")
    suspend fun getActivities(missionId: Int): List<MissionActivityEntity>
//    @Query("SELECT * FROM $ACTIVITY_TABLE_NAME where missionId=:missionId ")
//    suspend fun getActivities(missionId: Int,activityId: Int)

    @Query("Update $ACTIVITY_TABLE_NAME set activityStatus = :status, pendingDidi=:pendingDidi where missionId = :missionId and activityName = :activityName")
    fun updateActivityStatus(missionId: Int, activityName: String, status: Int, pendingDidi: Int)

    @Query("Select * FROM $ACTIVITY_TABLE_NAME where missionId in(:missionId)")
    fun isActivityExist(missionId: Int): Boolean

}