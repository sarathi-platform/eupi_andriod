package com.sarathi.dataloadingmangement.network.response


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AttendanceHistoryResponse(
    @SerializedName("date")
    @Expose
    val date: String, // 2024-05-26T18:30:00.000+00:00
    @SerializedName("didiAttendanceDetailList")
    @Expose
    val didiAttendanceDetailList: List<DidiAttendanceDetail>,
    @SerializedName("present")
    @Expose
    val present: Int, // 1
    @SerializedName("total")
    @Expose
    val total: Int // 1
)

data class DidiAttendanceDetail(
    @SerializedName("attendanceStatus")
    @Expose
    val attendanceStatus: String, // 1
    @SerializedName("id")
    @Expose
    val id: Int // 5974
)