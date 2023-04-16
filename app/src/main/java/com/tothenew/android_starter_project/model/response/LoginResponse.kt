package com.tothenew.android_starter_project.model.responseModel

import com.google.gson.annotations.SerializedName
import com.tothenew.android_starter_project.base.BaseResponseModel

data class LoginResponse(
    @SerializedName("email")
    val email: String?,
) : BaseResponseModel()