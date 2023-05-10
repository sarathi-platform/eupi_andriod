package com.patsurvey.nudge.model.response

import com.google.gson.annotations.SerializedName
import com.patsurvey.nudge.database.TolaEntity
import com.patsurvey.nudge.model.request.AddCohortRequest

data class GetCohortResponseModel(
    @SerializedName("latitude") var latitude: Double,
    @SerializedName("longitude") var longitude: Double,
    @SerializedName("name") var name: String,
    @SerializedName("type") var type : String,
    @SerializedName("villageId") var villageId : Int,
    @SerializedName("id") var id: Int,
    @SerializedName("status") var status: Int
) {
    companion object {
        fun convertToTolaEntity(tola: GetCohortResponseModel): TolaEntity {
            return TolaEntity(id = tola.id, name = tola.name, villageId = tola.villageId, status = tola.status, type = tola.type, latitude = tola.latitude, longitude = tola.longitude)
        }
    }
}
