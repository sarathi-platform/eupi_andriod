package com.patsurvey.nudge.model.request

import com.google.gson.annotations.SerializedName
import com.patsurvey.nudge.database.DidiEntity

data class DeleteDidiRequest(
    @SerializedName("id")
    var id: Int,

    @SerializedName("name")
    var name: String,

    @SerializedName("address")
    var address: String,

    @SerializedName("guardianName")
    var guardianName: String,

    @SerializedName("cohortName")
    var cohortName: String,
    @SerializedName("cohortId")
    var cohortId: Int,
    @SerializedName("deviceId") var deviceId: String,
    @SerializedName("villageId") var villageId: Int,
) {
    companion object {
        fun getDeleteDidiDetailsRequest(didiEntity: DidiEntity): DeleteDidiRequest {
            return DeleteDidiRequest(
                id = didiEntity.serverId,
                name = didiEntity.name,
                address = didiEntity.address,
                guardianName = didiEntity.guardianName,
                cohortName = didiEntity.cohortName,
                cohortId = didiEntity.cohortId,
                deviceId = didiEntity.localUniqueId,
                villageId = didiEntity.villageId

            )
        }
    }
}
