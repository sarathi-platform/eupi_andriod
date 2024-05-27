package com.sarathi.dataloadingmangement.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sarathi.dataloadingmangement.ACTIVITY_CONFIG_TABLE_NAME
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.model.mat.response.ActivityConfig


@Entity(tableName = ACTIVITY_CONFIG_TABLE_NAME)
data class ActivityConfigEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: Int = 0,
    var userId: String? = BLANK_STRING,
    var activityType: String,
    var activityTypeId: Int,
    var surveyId: Int,
    var doer: String,
    var reviewer: String,
    var subject: String,
    var activityId: Int,
    var missionId: Int

) {
    companion object {
        fun getActivityConfigEntity(
            activityId: Int,
            missionId: Int,
            activityConfig: ActivityConfig,
            uniqueUserIdentifier: String,
        ): ActivityConfigEntity {
            return ActivityConfigEntity(
                id = 0,
                userId = uniqueUserIdentifier,
                activityType = activityConfig.activityType,
                activityTypeId = activityConfig.activityTypeId,
                surveyId = activityConfig.surveyId,
                doer = activityConfig.doer,
                reviewer = activityConfig.reviewer,
                subject = activityConfig.subject,
                activityId = activityId,
                missionId = missionId
            )

        }


    }
}
