package com.sarathi.dataloadingmangement.data.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sarathi.dataloadingmangement.data.entities.MissionLanguageEntity


@Dao
interface MissionLanguageAttributeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMissionLanguageAttribute(missionLanguageEntity: MissionLanguageEntity)

    @Query("Delete from mission_language_table where userId=:userId")
    fun deleteMissionLanguageAttributeForUser(userId: String)

}