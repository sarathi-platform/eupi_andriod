package com.nrlm.baselinesurvey.model.request

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("phoneNumber") var mobileNumber: String
)

