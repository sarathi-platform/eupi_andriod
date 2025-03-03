package com.sarathi.dataloadingmangement.data.entities.revamp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nudge.core.DEFAULT_LANGUAGE_CODE
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.MISSION_LIVELIHOOD_CONFIG_TABLE_NAME
import com.sarathi.dataloadingmangement.data.converters.TagConverter
import com.sarathi.dataloadingmangement.model.mat.response.MissionLivelihoodMission

@Entity(tableName = MISSION_LIVELIHOOD_CONFIG_TABLE_NAME)
data class MissionLivelihoodConfigEntity(
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
    var languageCode: String,
    @TypeConverters(TagConverter::class)
    val programLivelihoodReferenceId: List<Int>?
) {
    companion object {

        fun getLivelihoodConfigEntityList(
            missionId: Int,
            missionType: String,
            livelihoodType: String,
            livelihoodOrder: Int,
            languages: List<MissionLivelihoodMission>?,
            userId: String,
            programLivelihoodReferenceId: List<Int>?
        ): List<MissionLivelihoodConfigEntity> {
            val missionLivelihoodConfigEntityList = ArrayList<MissionLivelihoodConfigEntity>()
            languages?.forEach { language ->
                missionLivelihoodConfigEntityList.add(
                    MissionLivelihoodConfigEntity(
                        userId = userId,
                        missionId = missionId,
                        missionType = missionType,
                        livelihoodType = livelihoodType,
                        livelihoodOrder = livelihoodOrder,
                        languageCode = language.languageCode ?: DEFAULT_LANGUAGE_CODE,
                        description = language.livelihood ?: livelihoodType,
                        programLivelihoodReferenceId = programLivelihoodReferenceId
                    )
                )
            }

            return missionLivelihoodConfigEntityList
        }
    }

}
