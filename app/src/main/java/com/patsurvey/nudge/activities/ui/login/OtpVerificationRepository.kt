package com.patsurvey.nudge.activities.ui.login

import com.patsurvey.nudge.RetryHelper
import com.patsurvey.nudge.base.BaseRepository
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.model.request.LoginRequest
import com.patsurvey.nudge.model.request.OtpRequest
import com.patsurvey.nudge.model.response.ApiResponseModel
import com.patsurvey.nudge.model.response.OtpVerificationModel
import javax.inject.Inject

class OtpVerificationRepository @Inject constructor(
    val prefRepo: PrefRepo,
    val villageListDao: VillageListDao
)  :BaseRepository() {

    suspend fun validateOtp(otpNumber: String): ApiResponseModel<OtpVerificationModel>{

        val otpRequest =
            OtpRequest(mobileNumber = getMobileNumber() ?: "", otp = otpNumber ) //Text this code
        return apiInterface.validateOtp(otpRequest);
    }

    fun saveAccessToken(token: String){
        prefRepo.saveAccessToken(token)
    }

    suspend fun generateOtp(
    ): ApiResponseModel<String>{
        val loginRequest =
            LoginRequest(mobileNumber = getMobileNumber() ?: "")
        return apiInterface.generateOtp(loginRequest);
    }

    fun getMobileNumber(): String?{
       return prefRepo.getMobileNumber();
    }

}