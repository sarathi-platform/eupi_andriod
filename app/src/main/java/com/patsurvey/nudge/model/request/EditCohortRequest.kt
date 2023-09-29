package com.patsurvey.nudge.model.request

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import com.patsurvey.nudge.database.TolaEntity

data class EditCohortRequest(
    @SerializedName("id") var id: Int,
    @SerializedName("latitude") var latitude: Double,
    @SerializedName("longitude") var longitude: Double,
    @SerializedName("name") var name: String,
    @SerializedName("type") var type : String,
    @SerializedName("villageId") var villageId : Int,
    @SerializedName("localModifiedDate") var localModifiedDate : Long,
) {

    companion object {
        fun getRequestObjectForTola(tola: TolaEntity): EditCohortRequest {
            return EditCohortRequest(
                latitude = tola.latitude,
                longitude = tola.longitude,
                name = tola.name,
                type = tola.type,
                villageId = tola.villageId,
                id = tola.serverId,
                localModifiedDate = tola.localModifiedDate ?:0
            )
        }
    }

    fun toJson() : JsonObject {
        val jsonObject = JsonObject()
        jsonObject.addProperty("latitude", latitude)
        jsonObject.addProperty("longitude", longitude)
        jsonObject.addProperty("name", name)
        jsonObject.addProperty("type", type)
        jsonObject.addProperty("villageId", villageId)
        jsonObject.addProperty("id", id)
        jsonObject.addProperty("localModifiedDate", localModifiedDate)
        return jsonObject
    }
}
