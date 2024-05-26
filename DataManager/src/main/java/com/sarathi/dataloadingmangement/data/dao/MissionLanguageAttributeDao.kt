package com.sarathi.dataloadingmangement.data.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.sarathi.dataloadingmangement.data.entities.MissionLanguageEntity


@Dao
interface MissionLanguageAttributeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMissionLanguageAttribute(missionLanguageEntity: MissionLanguageEntity)


}