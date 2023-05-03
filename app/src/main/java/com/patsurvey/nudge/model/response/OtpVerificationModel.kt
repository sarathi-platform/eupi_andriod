package com.patsurvey.nudge.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.patsurvey.nudge.database.LanguageEntity

data class OtpVerificationModel(
    @SerializedName("token")
    @Expose
    val token:String
)
