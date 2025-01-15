package com.sarathi.dataloadingmangement.data.entities.revamp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nudge.core.DEFAULT_LANGUAGE_CODE
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.MISSION_LIVELIHOOD_CONFIG_TABLE_NAME
import com.sarathi.dataloadingmangement.model.mat.response.MissionLivelihoodMission

@Entity(tableName = MISSION_LIVELIHOOD_CONFIG_TABLE_NAME)
data class LivelihoodConfigEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: Int = 0,
    var userId: String? = BLANK_STRING,
    var missionId: Int,
    var missionType: String,
    var livelihoodType: String,
    var livelihoodOrder: Int,
    var description: String?,
    var languageCode: String
) {
    companion object {

        fun getLivelihoodConfigEntityList(
            missionId: Int,
            missionType: String,
            livelihoodType: String,
            livelihoodOrder: Int,
            languages: List<MissionLivelihoodMission>?,
            userId: String,
        ): List<LivelihoodConfigEntity> {
            val livelihoodConfigEntityList = ArrayList<LivelihoodConfigEntity>()
            languages?.forEach { language ->
                livelihoodConfigEntityList.add(
                    LivelihoodConfigEntity(
                        userId = userId,
                        missionId = missionId,
                        missionType = missionType,
                        livelihoodType = livelihoodType,
                        livelihoodOrder = livelihoodOrder,
                        languageCode = language.languageCode ?: DEFAULT_LANGUAGE_CODE,
                        description = language.livelihood ?: livelihoodType
                    )
                )
            }

            return livelihoodConfigEntityList
        }
    }

}
