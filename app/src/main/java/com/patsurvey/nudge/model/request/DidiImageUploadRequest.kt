package com.patsurvey.nudge.model.request

import com.google.gson.annotations.SerializedName
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.utils.BLANK_STRING

data class DidiImageUploadRequest(
    val didiId: String, val location: String,
    val userType: String, val filePath: String,
    @SerializedName("address") var address: String,
    @SerializedName("guardianName") var guardianName: String,
    @SerializedName("name") var name: String,
    @SerializedName("relationship") var relationship: String,
    @SerializedName("castName") var castName: String,
    @SerializedName("castId") var castId: Int,
    @SerializedName("cohortId") var cohortId: Int,
    @SerializedName("deviceId") var deviceId: String,
    @SerializedName("villageId") var villageId: Int,
    @SerializedName("cohortName") var cohortName: String,
    @SerializedName("cohortDeviceId") var cohortDeviceId: String,
) {
    companion object {
        fun getRequestObjectForDidiUploadImage(
            didi: DidiEntity,
            filePath: String,
            location: String,
            userType: String,
            tolaServerId: Int? = 0,
            cohortdeviceId: String? = ""
        ): DidiImageUploadRequest {
            return DidiImageUploadRequest(
                didiId = didi.serverId.toString(),
                filePath = filePath,
                userType = userType,
                location = location,
                address = didi.address,
                guardianName = didi.guardianName,
                name = didi.name,
                relationship = didi.relationship,
                castName = didi.castName ?: BLANK_STRING,
                castId = didi.castId,
                cohortId = tolaServerId ?: 0,
                deviceId = didi.localUniqueId,
                villageId = didi.villageId,
                cohortName = didi.cohortName,
                cohortDeviceId = cohortdeviceId ?: ""
            )
        }
    }
}
