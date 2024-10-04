package com.patsurvey.nudge.model.dataModel


import com.google.gson.annotations.SerializedName
import com.nudge.core.BLANK_STRING
import com.patsurvey.nudge.database.DidiEntity

data class RankingEditEvent(
    @SerializedName("villageId")
    val villageId: Int,
    @SerializedName("type")
    val type: String,
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("name") var name: String = BLANK_STRING,
    @SerializedName("guardianName") var guardianName: String = BLANK_STRING,
    @SerializedName("address") var address: String = BLANK_STRING,
    @SerializedName("cohortId") var cohortId: Int = -1,
    @SerializedName("cohortName") var cohortName: String = BLANK_STRING,
    @SerializedName("deviceId") var deviceId: String,
    @SerializedName("cohortDeviceId") var cohortDeviceId: String? = BLANK_STRING,
) {

    companion object {

        fun getRankingEditEvent(
            villageId: Int,
            stepType: String,
            didiEntity: DidiEntity,
            tolaDeviceId: String
        ): RankingEditEvent {

            return RankingEditEvent(
                villageId = villageId,
                type = stepType,
                status = false,
                name = didiEntity.name,
                guardianName = didiEntity.guardianName,
                address = didiEntity.address,
                cohortId = 0, // TODO Add serverId from tola table.
                cohortName = didiEntity.cohortName,
                deviceId = didiEntity.localUniqueId,
                cohortDeviceId = tolaDeviceId
            )

        }

    }

}