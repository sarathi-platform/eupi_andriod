package com.nudge.core.model.request

import com.google.gson.annotations.SerializedName
import com.nudge.core.database.entities.Events


data class EventRequest(
    val id: String,
    @SerializedName("eventName") val eventName: String,
    @SerializedName("eventTopic") val eventTopic: String,
    @SerializedName("created_by") val createdBy: String,
    @SerializedName("mobileNo") val mobileNo: String,
    @SerializedName("payload") val payload: String?,
    @SerializedName("metadata") val metadata: String?,
    @SerializedName("createdDate") val createdDate: String?,
    @SerializedName("modifiedDate") val modifiedDate: String?,
) {


}

fun Events.toEventRequest() =
    EventRequest(
        this.id,
        this.name,
        this.type,
        this.createdBy,
        this.mobile_number,
        this.request_payload,
        this.metadata,
        this.created_date.toString(),
        this.modified_date.toString()
    )



