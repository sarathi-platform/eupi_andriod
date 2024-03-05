package com.nrlm.baselinesurvey.ui.auth.repository

import com.nrlm.baselinesurvey.model.response.ApiResponseModel

interface LoginScreenRepository {

    suspend fun generateOtp(mobileNumber: String): ApiResponseModel<String>

    fun saveMobileNumber(mobileNumber: String)

}