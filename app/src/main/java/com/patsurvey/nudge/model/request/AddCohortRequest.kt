package com.patsurvey.nudge.model.request

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.patsurvey.nudge.database.TolaEntity

data class AddCohortRequest(
    @SerializedName("latitude") var latitude: Double,
    @SerializedName("longitude") var longitude: Double,
    @SerializedName("name") var name: String,
    @SerializedName("type") var type : String,
    @SerializedName("villageId") var villageId : Int,
    @SerializedName("localCreatedDate") var localCreatedDate : Long,
    @SerializedName("localModifiedDate") var localModifiedDate : Long,
    @SerializedName("deviceId") var deviceId : String = "",
    @SerializedName("id") var id : Int = 0,
) {

    companion object {
        fun getRequestObjectForTola(tola: TolaEntity): AddCohortRequest {
            return AddCohortRequest(
                latitude = tola.latitude,
                longitude = tola.longitude,
                name = tola.name,
                type = tola.type,
                villageId = tola.villageId,
                localCreatedDate = tola.localCreatedDate ?: 0,
                localModifiedDate = tola.localModifiedDate ?: 0,
                deviceId = tola.localUniqueId?: "",
                id = tola.serverId
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
        jsonObject.addProperty("localCreatedDate", localCreatedDate)
        jsonObject.addProperty("localModifiedDate", localModifiedDate)
        jsonObject.addProperty("deviceId", deviceId)
        return jsonObject
    }

}

fun String.getAddCohortRequestPayloadFromString(): AddCohortRequest? {
    val type = object : TypeToken<AddCohortRequest?>() {}.type
    return Gson().fromJson(this, type)
}

fun String.getAddDidiRequestPayloadFromString(): AddDidiRequest? {
    val type = object : TypeToken<AddDidiRequest?>() {}.type
    return Gson().fromJson(this, type)
}