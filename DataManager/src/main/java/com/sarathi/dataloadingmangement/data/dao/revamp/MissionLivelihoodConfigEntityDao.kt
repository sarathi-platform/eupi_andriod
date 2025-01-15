package com.sarathi.dataloadingmangement.data.dao.revamp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sarathi.dataloadingmangement.MISSION_LIVELIHOOD_CONFIG_TABLE_NAME
import com.sarathi.dataloadingmangement.data.entities.revamp.MissionLivelihoodConfigEntity

@Dao
interface MissionLivelihoodConfigEntityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLivelihoodConfigs(livelihoodConfigEntities: List<MissionLivelihoodConfigEntity>)

    @Query("SELECT * FROM $MISSION_LIVELIHOOD_CONFIG_TABLE_NAME WHERE missionId = :missionId AND userId = :uniqueUserIdentifier AND languageCode = :language")
    fun getLivelihoodConfigForMission(
        missionId: Int,
        uniqueUserIdentifier: String,
        language: String
    ): MissionLivelihoodConfigEntity?

    @Query("Delete from $MISSION_LIVELIHOOD_CONFIG_TABLE_NAME where  missionId=:missionId and userId=:uniqueUserIdentifier")
    fun deleteLivelihoodConfig(
        missionId: Int,
        uniqueUserIdentifier: String
    )


    @Query("Delete from $MISSION_LIVELIHOOD_CONFIG_TABLE_NAME where userId=:uniqueUserIdentifier")
    fun deleteLivelihoodConfigForUser(
        uniqueUserIdentifier: String
    )

}