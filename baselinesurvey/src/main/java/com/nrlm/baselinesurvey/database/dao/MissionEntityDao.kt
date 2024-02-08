package com.nrlm.baselinesurvey.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nrlm.baselinesurvey.MISSION_TABLE_NAME
import com.nrlm.baselinesurvey.database.entity.MissionEntity

@Dao
interface MissionEntityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMission(missions: MissionEntity)

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insertMission(mission: MissionEntity)

    @Query("DELETE FROM $MISSION_TABLE_NAME")
    fun deleteMissions()

    @Query("SELECT * FROM $MISSION_TABLE_NAME")
    suspend fun getMissions(): List<MissionEntity>

    @Query("SELECT * FROM $MISSION_TABLE_NAME where missionId=:missionId ")
    suspend fun getMission(missionId: Int): MissionEntity

}