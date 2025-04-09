package com.sarathi.dataloadingmangement.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.MISSION_LANGUAGE_TABLE_NAME


@Entity(tableName = MISSION_LANGUAGE_TABLE_NAME)
data class MissionLanguageEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: Int = 0,
    var userId: String? = BLANK_STRING,
    var languageCode: String,
    var description: String,
    var missionId: Int
) {
    companion object {

        fun getMissionLanguageEntity(
            missionId: Int,
            languageCode: String,
            description: String,
            uniqueUserIdentifier: String,
        ): MissionLanguageEntity {

            return MissionLanguageEntity(
                id = 0,
                userId = uniqueUserIdentifier,
                languageCode = languageCode,
                description = description,
                missionId
            )
        }

    }

}
