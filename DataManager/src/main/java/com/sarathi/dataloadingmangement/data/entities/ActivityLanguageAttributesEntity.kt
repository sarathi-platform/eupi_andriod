package com.sarathi.dataloadingmangement.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sarathi.dataloadingmangement.model.mat.response.ActivityTitle
import com.sarathi.dataloadingmangement.util.ACTIVITY_LANGUAGE_ATTRIBUTE_TABLE_NAME
import com.sarathi.dataloadingmangement.util.BLANK_STRING

@Entity(tableName = ACTIVITY_LANGUAGE_ATTRIBUTE_TABLE_NAME)
data class ActivityLanguageAttributesEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: Int = 0,
    var userId: String? = BLANK_STRING,
    var languageCode: String,
    var description: String,
    var activityId: Int,
    var missionId: Int
) {
    companion object {

        fun getActivityLanguageAttributesEntity(
            missionId: Int,
            userId: String?,
            activityId: Int,
            activityTitle: ActivityTitle
        ): ActivityLanguageAttributesEntity {

            return ActivityLanguageAttributesEntity(
                id = 0,
                userId = userId,
                languageCode = activityTitle.language,
                activityId = activityId,
                missionId = missionId,
                description = activityTitle.name
            )
        }
    }
}
