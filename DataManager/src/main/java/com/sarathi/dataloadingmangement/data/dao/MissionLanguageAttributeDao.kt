package com.sarathi.dataloadingmangement.data.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sarathi.dataloadingmangement.MISSION_LANGUAGE_TABLE_NAME
import com.sarathi.dataloadingmangement.data.entities.MissionLanguageEntity
import com.sarathi.dataloadingmangement.model.uiModel.MissionInfoUIModel


@Dao
interface MissionLanguageAttributeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMissionLanguageAttribute(missionLanguageEntity: MissionLanguageEntity)

    @Query("Delete from mission_language_table where userId=:userId")
    fun deleteMissionLanguageAttributeForUser(userId: String)

    @Query(
        "select description as title ,livelihood_config_table.livelihoodOrder as livelihoodOrder,livelihood_config_table.livelihoodType as livelihoodType " +
                "from $MISSION_LANGUAGE_TABLE_NAME" +
                " left join livelihood_config_table on mission_language_table.missionId=livelihood_config_table.missionId" +
                "  and livelihood_config_table.userId=:userId " +
                "and livelihood_config_table.languageCode=:languageCode " +
                "where mission_language_table.missionId =:missionId and mission_language_table.userId=:userId and mission_language_table.languageCode=:languageCode"
    )
    fun fetchMissionInfo(missionId: Int, userId: String, languageCode: String): MissionInfoUIModel?

}