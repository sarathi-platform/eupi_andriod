package com.patsurvey.nudge.model.request

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import com.patsurvey.nudge.database.TolaEntity

data class DeleteTolaRequest(
    @SerializedName("id") var id: Int,
    @SerializedName("localModifiedDate") var localModifiedDate : Long
) {

    companion object {
        fun getRequestObjectForDeleteTola(tola: TolaEntity): DeleteTolaRequest {
            return DeleteTolaRequest(
                id = tola.serverId,
                localModifiedDate = System.currentTimeMillis()
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
