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
    @SerializedName("cohortId") var cohortId : Int
) {

    companion object {
        fun getRequestObjectForDidi(didi: DidiEntity): AddDidiRequest {
            return AddDidiRequest(
                address=didi.address,
                guardianName=didi.guardianName,
                name=didi.name,
                relationship=didi.relationship,
                castName=didi.castName?: BLANK_STRING,
                castId=didi.castId,
                cohortId=didi.cohortId
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
        return jsonObject
    }

}
