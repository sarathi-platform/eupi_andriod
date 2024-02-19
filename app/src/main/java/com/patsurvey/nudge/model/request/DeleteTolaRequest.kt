package com.patsurvey.nudge.model.request

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import com.patsurvey.nudge.database.TolaEntity

data class DeleteTolaRequest(
    @SerializedName("id") var id: Int,
    @SerializedName("localModifiedDate") var localModifiedDate : Long,
    @SerializedName("cohortName") var cohortName: String,
    @SerializedName("villageId") var villageId: Int,
    @SerializedName("deviceId") var deviceId: String,

    ) {

    companion object {
        fun getRequestObjectForDeleteTola(tola: TolaEntity): DeleteTolaRequest {
            return DeleteTolaRequest(
                id = tola.serverId,
                localModifiedDate = System.currentTimeMillis(),
                cohortName = tola.name,
                villageId = tola.villageId,
                deviceId = tola.localUniqueId ?: ""
            )
        }
    }

    fun toJson() : JsonObject {
        val jsonObject = JsonObject()
        jsonObject.addProperty("id", id)
        jsonObject.addProperty("localModifiedDate", localModifiedDate)
        return jsonObject
    }
}
