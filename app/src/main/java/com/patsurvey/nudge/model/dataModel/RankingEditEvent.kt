package com.patsurvey.nudge.model.dataModel


import com.google.gson.annotations.SerializedName
import com.patsurvey.nudge.database.DidiEntity

data class RankingEditEvent(
    @SerializedName("villageId")
    val villageId: Int,
    @SerializedName("type")
    val type: String,
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("name") var name: String = "",
    @SerializedName("guardianName") var guardianName: String = "",
    @SerializedName("address") var address: String = "",
    @SerializedName("cohortId") var cohortId: Int = -1,
    @SerializedName("cohortName") var cohortName: String = "",
    @SerializedName("deviceId") var deviceId: String,
    @SerializedName("cohortDeviceId") var cohortDeviceId: String? = "",
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
                cohortId = didiEntity.cohortId,
                cohortName = didiEntity.cohortName,
                deviceId = didiEntity.localUniqueId,
                cohortDeviceId = tolaDeviceId
            )

        }

    }

}