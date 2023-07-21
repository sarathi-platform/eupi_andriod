package com.patsurvey.nudge.model.request

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import com.patsurvey.nudge.utils.BLANK_STRING


data class EditDidiWealthRankingRequest(
    @SerializedName("id") var id: Int,
    @SerializedName("type") var type: String,
    @SerializedName("result") var result: String,
    @SerializedName("score") var score: Double?=0.0,
    @SerializedName("comment") var comment: String?= BLANK_STRING,
    @SerializedName("localModifiedDate") var localModifiedDate: Long?=0,
    @SerializedName("rankingEdit") var rankingEdit: Boolean = false
) {
    fun toJson() : JsonObject {
        val jsonObject = JsonObject()
        jsonObject.addProperty("id", id)
        jsonObject.addProperty("type", type)
        jsonObject.addProperty("result", result)
        return jsonObject
    }
}
