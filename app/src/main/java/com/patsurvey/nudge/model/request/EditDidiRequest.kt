package com.patsurvey.nudge.model.request

import androidx.room.ColumnInfo
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

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
