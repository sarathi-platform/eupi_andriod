package com.nrlm.baselinesurvey.model.response

import com.google.gson.annotations.SerializedName

data class LoginResponseModel(
    @SerializedName("lastSynUserId") val lastSynUserId: Int? = 0,
    @SerializedName("currentUserId") val currentUserId: Int? = 0,
    @SerializedName("messageCode") val messageCode: String? = null
)