package com.patsurvey.nudge.model.request

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.utils.BLANK_STRING

data class AddDidiRequest(
    @SerializedName("address") var address: String,
    @SerializedName("guardianName") var guardianName: String,
    @SerializedName("name") var name: String,
    @SerializedName("relationship") var relationship : String,
    @SerializedName("castName") var castName : String,
    @SerializedName("castId") var castId : Int,
    @SerializedName("cohortId") var cohortId : Int,
    @SerializedName("localCreatedDate") var localCreatedDate : Long,
    @SerializedName("localModifiedDate") var localModifiedDate : Long,
    @SerializedName("deviceId") var deviceId : String,
    @SerializedName("villageId") var villageId: Int,
    @SerializedName("cohortName") var cohortName: String,
    @SerializedName("cohortDeviceId") var cohortDeviceId: String,

) {

    companion object {
        fun getRequestObjectForDidi(
            didi: DidiEntity,
            tolaServerId: Int? = 0,
            cohortdeviceId: String? = ""
        ): AddDidiRequest {
            return AddDidiRequest(
                address=didi.address,
                guardianName=didi.guardianName,
                name=didi.name,
                relationship=didi.relationship,
                castName=didi.castName?: BLANK_STRING,
                castId=didi.castId,
                cohortId = tolaServerId ?: 0,
                localModifiedDate = didi.localModifiedDate?:0,
                localCreatedDate = didi.localCreatedDate ?:0,
                deviceId = didi.localUniqueId,
                villageId = didi.villageId,
                cohortName = didi.cohortName,
                cohortDeviceId = cohortdeviceId ?: ""
            )
        }
    }

    fun toJson() : JsonObject {
        val jsonObject = JsonObject()
        jsonObject.addProperty("address", address)
        jsonObject.addProperty("guardianName", guardianName)
        jsonObject.addProperty("name", name)
        jsonObject.addProperty("relationship", relationship)
        jsonObject.addProperty("castName", castName)
        jsonObject.addProperty("castId", castId)
        jsonObject.addProperty("cohortId", cohortId)
        jsonObject.addProperty("localCreatedDate", localCreatedDate)
        jsonObject.addProperty("localModifiedDate", localModifiedDate)
        jsonObject.addProperty("deviceId", deviceId)
        jsonObject.addProperty("villageId", villageId)
        jsonObject.addProperty("cohortName", cohortName)
        return jsonObject
    }

}
