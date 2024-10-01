package com.nudge.core.datamodel

import com.google.gson.annotations.SerializedName

data class SyncImageUploadPayload(
    @SerializedName("client_id") val clientId: String,
    @SerializedName("event_name") val eventName: String,
    @SerializedName("topic_name") val eventTopic: String,
    @SerializedName("created_by") val createdBy: String,
    @SerializedName("mobile_no") val mobileNo: String,
    @SerializedName("payload") val payload: String,
    @SerializedName("file_name") val fileName: String,
    @SerializedName("file_path") val filePath: String,
    @SerializedName("derive_type") val driveType: String,
    @SerializedName("file_event_client_id") val fileEventClientId: String,
    @SerializedName("metadata") val metadata: String
)
