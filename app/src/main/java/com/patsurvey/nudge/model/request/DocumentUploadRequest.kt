package com.patsurvey.nudge.model.request

import com.google.gson.annotations.SerializedName

data class DocumentUploadRequest(
    @SerializedName("villageId")
    val villageId: String,
    @SerializedName("userType")
    val userType: String,
    @SerializedName("filePath")
    val filePath: String,
    @SerializedName("formName")
    val formName: String
)


