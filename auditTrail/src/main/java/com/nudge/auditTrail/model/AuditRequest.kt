package com.nudge.auditTrail.model

import com.google.gson.annotations.SerializedName

data class AuditRequest(
    @SerializedName("mobileNumber") var mobileNumber: String,
    @SerializedName("deviceType") var deviceType: String?,
    @SerializedName("deviceId") var deviceId: String?,
    @SerializedName("actionStatusType") var actionStatusType: String?,
    @SerializedName("message") var message: String?,
    @SerializedName("dataChangeDetails") var dataChangeDetails: List<DataChangeDetail>?,
    @SerializedName("created_date") val createdDate: String?,
    @SerializedName("modified_date") val modifiedDate: String?,
)
data class DataChangeDetail(
    @SerializedName("entityId") var entityId: Int,
    @SerializedName("entityName") var entityName: String,
    @SerializedName("fieldName") var fieldName: String,
    @SerializedName("oldValue")var oldValue: String,
    @SerializedName("newValue") var newValue: String
)
fun AuditRequest.toEventRequest() =
    AuditRequest(
        this.mobileNumber,
        this.deviceType,
        this.actionStatusType,
        this.deviceId,
        this.message,
        this.dataChangeDetails,
        this.createdDate.toString(),
        this.modifiedDate.toString()
    )
