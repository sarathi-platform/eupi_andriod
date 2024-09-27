package com.sarathi.dataloadingmangement.network.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AttendanceHistoryRequest(
    @SerializedName("sgId")
    @Expose
    val sgId: Int,

    @SerializedName("userId")
    @Expose
    val userId: Int
) {

    companion object {

        fun getRequest(smallGroupId: Int, userId: Int): AttendanceHistoryRequest {

            return AttendanceHistoryRequest(sgId = smallGroupId, userId = userId)

        }

    }

}
