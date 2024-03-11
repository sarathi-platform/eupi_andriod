package com.nrlm.baselinesurvey.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nrlm.baselinesurvey.MISSION_TABLE_NAME
import com.nrlm.baselinesurvey.model.response.MissionResponseModel
import com.nrlm.baselinesurvey.utils.states.SurveyState

@Entity(tableName = MISSION_TABLE_NAME)
data class MissionEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: Int = 1,
    var missionId: Int,
    var missionName: String,
    var startDate: String,
    var endDate: String,
    var status: String,
    var activityTaskSize: Int,
    var missionStatus: Int,
    var pendingActivity: Int,
    var activityComplete: Int,
    var language: String
) {
    companion object {
        fun getMissionEntity(activityTaskSize: Int, mission: MissionResponseModel): MissionEntity {
            return MissionEntity(
                missionId = mission.missionId,
                missionName = mission.missionName,
                startDate = mission.startDate,
                endDate = mission.endDate,
                status = "",
                activityTaskSize = activityTaskSize,
                missionStatus = 0,
                pendingActivity = activityTaskSize,
                activityComplete = SurveyState.NOT_STARTED.ordinal,
                language = mission.language
            )
        }
    }
}