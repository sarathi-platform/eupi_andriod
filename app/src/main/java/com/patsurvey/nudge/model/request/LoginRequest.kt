package com.patsurvey.nudge.model.request

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("phoneNumber") var mobileNumber: String
)

data class OtpRequest(
    @SerializedName("phoneNumber") var mobileNumber: String,
    @SerializedName("otp") var otp: String,
)
