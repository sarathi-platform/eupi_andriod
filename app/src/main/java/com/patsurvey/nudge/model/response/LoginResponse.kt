package com.patsurvey.nudge.model.responseModel

import com.google.gson.annotations.SerializedName
import com.patsurvey.nudge.base.BaseResponseModel

data class LoginResponse(
    @SerializedName("email")
    val email: String?,
) : BaseResponseModel()