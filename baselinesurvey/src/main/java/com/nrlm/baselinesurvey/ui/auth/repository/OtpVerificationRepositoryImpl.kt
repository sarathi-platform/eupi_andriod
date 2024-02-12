package com.nrlm.baselinesurvey.ui.auth.repository

import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.PREF_MOBILE_NUMBER
import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.model.request.LoginRequest
import com.nrlm.baselinesurvey.model.request.OtpRequest
import com.nrlm.baselinesurvey.model.response.ApiResponseModel
import com.nrlm.baselinesurvey.model.response.OtpVerificationModel
import com.nrlm.baselinesurvey.network.interfaces.ApiService
import javax.inject.Inject

class OtpVerificationRepositoryImpl @Inject constructor(
    private val prefRepo: PrefRepo,
    private val apiService: ApiService
): OtpVerificationRepository {

    override suspend fun validateOtp(otpRequest: OtpRequest): ApiResponseModel<OtpVerificationModel> {
        return apiService.validateOtp(otpRequest)
    }

    override suspend fun resendOtp(loginRequest: LoginRequest): ApiResponseModel<String> {
        return apiService.generateOtp(loginRequest)
    }

    override fun getMobileNumber(): String {
        return prefRepo.getPref(PREF_MOBILE_NUMBER, BLANK_STRING) ?: BLANK_STRING
    }

    override fun saveAccessToken(token: String) {
        prefRepo.saveAccessToken(token)
    }

}