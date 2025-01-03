package com.sarathi.dataloadingmangement.data.entities.revamp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.LIVELIHOOD_CONFIG_TABLE_NAME

@Entity(tableName = LIVELIHOOD_CONFIG_TABLE_NAME)
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
    var languageCode: String
) {
    companion object {

        fun getLivelihoodConfigEntity(
            missionId: Int,
            missionType: String,
            livelihoodType: String,
            livelihoodOrder: Int,
            languageId: String,
            userId: String,
        ): LivelihoodConfigEntity {
            return LivelihoodConfigEntity(
                id = 0,
                missionId = missionId,
                missionType = missionType,
                livelihoodType = livelihoodType,
                livelihoodOrder = livelihoodOrder,
                languageCode = languageId ?: "en",
                userId = userId
            )
        }
    }

}
