package com.nrlm.baselinesurvey.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nrlm.baselinesurvey.ACTIVITY_TABLE_NAME
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.model.datamodel.MissionActivityModel
import com.nrlm.baselinesurvey.utils.states.SurveyState
import com.nrlm.baselinesurvey.utils.states.toStringList

@Entity(tableName = ACTIVITY_TABLE_NAME)
data class MissionActivityEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: Int = 0,
    var userId: String? = BLANK_STRING,
    var missionId: Int,
    var activityId: Int,
    var activityName: String,
    var activityType: String,
    var activityTypeId: Int,
    var doer: String,
    var endDate: String,
    var reviewer: String,
    var startDate: String,
    var actualStartDate: String = BLANK_STRING,
    val actualCompleteDate: String = BLANK_STRING,
    var subject: String,
    var status: String?,
    var activityTaskSize: Int,
    var activityStatus: Int,
    var pendingDidi: Int,
    val isAllTask: Boolean,
    var language: String?,
    var isActive: Int
    ) {
    companion object {
        fun getMissionActivityEntity(
            userId: String,
            missionId: Int,
            activityTaskSize: Int,
            activity: MissionActivityModel
        ): MissionActivityEntity {
            return MissionActivityEntity(
                userId = userId,
                missionId = missionId,
                activityId = activity.activityId,
                activityTypeId = activity.activityTypeId,
                activityName = activity.activityName,
                activityType = activity.activityType,
                doer = activity.doer,
                startDate = activity.startDate,
                endDate = activity.endDate,
                reviewer = activity.reviewer,
                subject = activity.subject,
                status = getStatusForActivity(activity.status ?: SurveyState.NOT_STARTED.name),
                activityTaskSize = activityTaskSize,
                activityStatus = SurveyState.INPROGRESS.ordinal,
                pendingDidi = activityTaskSize,
                isAllTask = false,
                language = activity.language,
                isActive = 1
            )
        }

        fun getStatusForActivity(status: String): String {
            return SurveyState.values().toStringList().find { it.equals(status, true) }
                ?: SurveyState.NOT_STARTED.name
        }

    }
}