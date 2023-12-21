package com.nrlm.baselinesurvey.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class OtpVerificationModel(
    @SerializedName("token")
    @Expose
    val token:String
)
