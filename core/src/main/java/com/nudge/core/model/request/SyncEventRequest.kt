package com.nudge.core.model.request

import com.google.gson.annotations.SerializedName
import com.nudge.core.database.entities.Events


data class EventRequest(
    @SerializedName("client_id") val id: String,
    @SerializedName("event_name") val eventName: String,
    @SerializedName("topic_name") val eventTopic: String,
    @SerializedName("created_by") val createdBy: String,
    @SerializedName("mobile_no") val mobileNo: String,
    @SerializedName("payload") val payload: String?,
    @SerializedName("metadata") val metadata: String?,
    @SerializedName("created_date") val createdDate: String?,
    @SerializedName("modified_date") val modifiedDate: String?,
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




