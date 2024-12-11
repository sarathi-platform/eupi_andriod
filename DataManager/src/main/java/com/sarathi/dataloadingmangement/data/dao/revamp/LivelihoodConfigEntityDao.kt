package com.sarathi.dataloadingmangement.data.dao.revamp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sarathi.dataloadingmangement.LIVELIHOOD_CONFIG_TABLE_NAME
import com.sarathi.dataloadingmangement.data.entities.revamp.LivelihoodConfigEntity

@Dao
interface LivelihoodConfigEntityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLivelihoodConfig(livelihoodConfigEntity: LivelihoodConfigEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLivelihoodConfigs(livelihoodConfigEntities: List<LivelihoodConfigEntity>)

    @Query("SELECT * FROM $LIVELIHOOD_CONFIG_TABLE_NAME WHERE missionId = :missionId AND userId = :uniqueUserIdentifier AND languageId = :language")
    fun getLivelihoodConfigForMission(
        missionId: Int,
        uniqueUserIdentifier: String,
        language: String
    ): List<LivelihoodConfigEntity>

    @Query("Delete from $LIVELIHOOD_CONFIG_TABLE_NAME where  missionId=:missionId and userId=:uniqueUserIdentifier")
    fun deleteLivelihoodConfig(
        missionId: Int,
        uniqueUserIdentifier: String
    )


    @Query("Delete from $LIVELIHOOD_CONFIG_TABLE_NAME where userId=:uniqueUserIdentifier")
    fun deleteLivelihoodConfigForUser(
        uniqueUserIdentifier: String
    )

}