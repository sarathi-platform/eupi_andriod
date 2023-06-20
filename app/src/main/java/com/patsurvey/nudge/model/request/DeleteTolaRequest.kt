package com.patsurvey.nudge.model.request

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class DeleteTolaRequest(
    @SerializedName("id") var id: Int,
    @SerializedName("localModifiedDate") var localModifiedDate : Long
) {
    fun toJson() : JsonObject {
        val jsonObject = JsonObject()
        jsonObject.addProperty("id", id)
        jsonObject.addProperty("localModifiedDate", localModifiedDate)
        return jsonObject
    }
}
