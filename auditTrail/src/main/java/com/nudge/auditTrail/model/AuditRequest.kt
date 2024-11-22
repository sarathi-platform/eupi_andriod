package com.nudge.auditTrail.model

import com.google.gson.annotations.SerializedName
import com.nudge.auditTrail.entities.AuditTrailEntity

data class AuditRequest(
    @SerializedName("mobileNumber") val mobileNumber: String,
    @SerializedName("deviceType") val deviceType: String,
    @SerializedName("deviceId") val deviceId: String,
    @SerializedName("actionStatusType") val actionStatusType: String,
    @SerializedName("message") val message: String,
    @SerializedName("dataChangeDetails") val dataChangeDetails: List<DataChangeDetail>,
    @SerializedName("created_date") val createdDate: String,
    @SerializedName("modified_date") val modifiedDate: String,
)
data class DataChangeDetail(
    @SerializedName("entityId") val entityId: Int,
    @SerializedName("entityName") val entityName: String,
    @SerializedName("fieldName") val fieldName: String,
    @SerializedName("oldValue")val oldValue: String,
    @SerializedName("newValue") val newValue: String
)
fun AuditTrailEntity.toEventRequest() =
    AuditRequest(
        this.id,
        this.createdDate.toString(),
        this.modifiedDate.toString(),
        this.mobileNumber,
        this.auditData
        )


