package com.sarathi.dataloadingmangement.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sarathi.dataloadingmangement.network.response.MissionResponseModel
import com.sarathi.dataloadingmangement.util.BLANK_STRING
import com.sarathi.dataloadingmangement.util.MISSION_TABLE_NAME

@Entity(tableName = MISSION_TABLE_NAME)
data class Mission(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: Int = 0,
    var userId: String? = BLANK_STRING,
    var missionId: Int,
    var missionName: String,
    var startDate: String,
    var endDate: String,
    var status: String,
    var activityTaskSize: Int,
    var missionStatus: Int,
    var pendingActivity: Int,
    var activityComplete: Int,
    var language: String?,
    var actualStartDate: String = BLANK_STRING,
    var actualCompletedDate: String = BLANK_STRING,
    var isActive: Int = 1
) {
    companion object {
        fun getMissionEntity(
            userId: String,
            activityTaskSize: Int,
            mission: MissionResponseModel
        ): Mission {
            return Mission(
                id = 0,
                userId = userId,
                missionId = mission.missionId,
                missionName = mission.missionName,
                startDate = mission.startDate,
                endDate = mission.endDate,
                status = "",
                activityTaskSize = activityTaskSize,
                missionStatus = 0,
                pendingActivity = activityTaskSize,
                activityComplete = 0,
                language = mission.language
            )
        }

    }
}