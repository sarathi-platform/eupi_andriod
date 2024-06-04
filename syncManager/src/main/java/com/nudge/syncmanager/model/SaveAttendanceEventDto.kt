package com.nudge.syncmanager.model


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SaveAttendanceEventDto(
    @SerializedName("dateCreated")
    @Expose
    val dateCreated: Long,
    @SerializedName("languageId")
    @Expose
    val languageId: Int, // 2
    /*@SerializedName("localTaskId")
    @Expose
    val localTaskId: String,*/ // TODO ask where to get this
    @SerializedName("payloadData")
    @Expose
    val payloadData: List<PayloadData>,
    @SerializedName("payloadType")
    @Expose
    val payloadType: String,
    @SerializedName("subjectId")
    @Expose
    val subjectId: Int,
    @SerializedName("subjectType")
    @Expose
    val subjectType: String,
    @SerializedName("tagId")
    @Expose
    val tagId: Int
)

data class PayloadData(
    @SerializedName("date")
    @Expose
    val date: String, // 23 May 2024
    @SerializedName("id")
    @Expose
    val id: String, // 2
    @SerializedName("value")
    @Expose
    val value: String // Present
)