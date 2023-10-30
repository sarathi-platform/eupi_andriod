package com.nrlm.baselinesurvey.ui.auth.repository

import com.nrlm.baselinesurvey.model.request.LoginRequest
import com.nrlm.baselinesurvey.model.request.OtpRequest
import com.nrlm.baselinesurvey.model.response.ApiResponseModel
import com.nrlm.baselinesurvey.model.response.OtpVerificationModel

interface OtpVerificationRepository {

    suspend fun validateOtp(otpRequest: OtpRequest): ApiResponseModel<OtpVerificationModel>

    suspend fun resendOtp(loginRequest: LoginRequest): ApiResponseModel<String>

    fun getMobileNumber(): String

    fun saveAccessToken(token: String)

}