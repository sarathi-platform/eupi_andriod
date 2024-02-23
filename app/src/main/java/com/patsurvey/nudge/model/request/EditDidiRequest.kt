package com.patsurvey.nudge.model.request

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import com.patsurvey.nudge.database.DidiEntity

data class EditDidiRequest(
    @SerializedName("id") var id: Int,
    @SerializedName("name")
    var name: String,

    @SerializedName("address")
    var address: String,

    @SerializedName("guardianName")
    var guardianName: String,
    @SerializedName("castId")
    var castId: Int,
    @SerializedName("cohortId")
    var cohortId: Int,
    @SerializedName("villageId") var villageId : Int,
    @SerializedName("cohortName") var cohortName : String,
) {

    companion object {
        fun getUpdateDidiDetailsRequest(didiEntity: DidiEntity): EditDidiRequest {
            return EditDidiRequest(
                id = didiEntity.serverId,
                name = didiEntity.name,
                address = didiEntity.address,
                guardianName = didiEntity.guardianName,
                castId = didiEntity.castId,
                cohortId = didiEntity.cohortId,
                villageId = didiEntity.villageId,
                cohortName = didiEntity.cohortName
            )
        }
    }

    fun toJson() : JsonObject {
        val jsonObject = JsonObject()
        jsonObject.addProperty("id", id)
        jsonObject.addProperty("name", name)
        jsonObject.addProperty("address", address)
        jsonObject.addProperty("guardianName", guardianName)
        jsonObject.addProperty("castId", castId)
        jsonObject.addProperty("cohortId", cohortId)
        jsonObject.addProperty("villageId", villageId)
        jsonObject.addProperty("cohortName", cohortName)
        return jsonObject
    }
}
