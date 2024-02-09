package com.nudge.core.model.request

import com.google.gson.annotations.SerializedName
import com.nudge.core.database.entities.Events
import com.nudge.core.enums.getTopicFromName


data class EventRequest(val id:String,
                            @SerializedName("event_name")val eventName:String,
                            @SerializedName("event_topic")val eventTopic:String,
                            @SerializedName("created_by")val createdBy:String,
                            @SerializedName("mobile_no")val mobileNo:String,
                            @SerializedName("payload")val payload:String?,
                            @SerializedName("metadata")val metadata:String?,
                            @SerializedName("created_date")val createdDate:String?,
                            @SerializedName("modified_date")val modifiedDate:String?, ){

    object EventRequestMapper {
        fun fromEvent(event: Events) =
           EventRequest(event.id,event.name,event.type,event.createdBy, event.mobile_number,event.request_payload,event.metadata,event.created_date.toString(),event.modified_date.toString())
    }
}


