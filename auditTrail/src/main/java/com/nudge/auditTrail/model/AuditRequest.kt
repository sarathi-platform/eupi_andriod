package com.nudge.auditTrail.model

import com.google.gson.annotations.SerializedName
import com.nudge.auditTrail.entities.AuditTrailEntity
import com.nudge.core.BLANK_STRING
import java.util.UUID

data class AuditRequest(
    @SerializedName("mobileNumber") val mobileNumber: String,
    @SerializedName("deviceType") val deviceType: String,
    @SerializedName("deviceId") val deviceId: String,
    @SerializedName("actionStatusType") val actionStatusType: String,
    @SerializedName("actionType") val actionType: String,
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
        mobileNumber = this.mobileNumber,
        createdDate = this.createdDate.toString(),
        modifiedDate = this.modifiedDate.toString(),
        deviceType = "Android",
        deviceId = UUID.randomUUID().toString(),
        actionStatusType = this.actionStatus ?: BLANK_STRING,
        actionType = this.actionType ?: BLANK_STRING,
        message = "Success",
        dataChangeDetails = listOf(),




        )


