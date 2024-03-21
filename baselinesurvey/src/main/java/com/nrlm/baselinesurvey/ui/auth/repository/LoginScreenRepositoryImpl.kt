package com.nrlm.baselinesurvey.ui.auth.repository

import com.nrlm.baselinesurvey.PREF_MOBILE_NUMBER
import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.model.request.LoginRequest
import com.nrlm.baselinesurvey.model.response.ApiResponseModel
import com.nrlm.baselinesurvey.network.interfaces.BaseLineApiService
import javax.inject.Inject

class LoginScreenRepositoryImpl @Inject constructor(
    private val prefRepo: PrefRepo,
    private val baseLineApiService: BaseLineApiService
): LoginScreenRepository {


    override suspend fun generateOtp(mobileNumber: String): ApiResponseModel<String> {
        val loginRequest = LoginRequest(mobileNumber)
        return baseLineApiService.generateOtp(loginRequest)
    }

    override fun saveMobileNumber(mobileNumber: String) {
        prefRepo.savePref(PREF_MOBILE_NUMBER, mobileNumber)
    }


}