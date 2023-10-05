package com.patsurvey.nudge.activities.ui.login

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

    suspend fun validateOtp(otpRequest: OtpRequest): ApiResponseModel<OtpVerificationModel>{
        return apiInterface.validateOtp(otpRequest);
    }

    fun saveAccessToken(token: String){
        prefRepo.saveAccessToken(token)
    }

    suspend fun generateOtp( loginRequest: LoginRequest
    ): ApiResponseModel<String>{
        return apiInterface.generateOtp(loginRequest);
    }

    fun getMobileNumber(): String?{
       return prefRepo.getMobileNumber();
    }

}