package com.patsurvey.nudge.activities.ui.login

import com.google.gson.Gson
import com.nudge.core.preference.CoreSharedPrefs
import com.patsurvey.nudge.base.BaseRepository
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.model.request.LoginRequest
import com.patsurvey.nudge.model.request.OtpRequest
import com.patsurvey.nudge.model.response.ApiResponseModel
import com.patsurvey.nudge.model.response.OtpVerificationModel
import com.patsurvey.nudge.utils.BPC_USER_TYPE
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.PREF_KEY_TYPE_NAME
import javax.inject.Inject

class OtpVerificationRepository @Inject constructor(
    val prefRepo: PrefRepo,
    val villageListDao: VillageListDao,
    val coreSharedPrefs: CoreSharedPrefs
)  :BaseRepository() {

    suspend fun validateOtp(otpNumber: String): ApiResponseModel<OtpVerificationModel>{

        val otpRequest =
            OtpRequest(mobileNumber = getMobileNumber() ?: "", otp = otpNumber ) //Text this code
        NudgeLogger.d("OtpVerificationRepository","validateOtp => ${Gson().toJson(otpRequest)}")
        return apiInterface.validateOtp(otpRequest);
    }

    fun saveAccessToken(token: String){
        prefRepo.saveAccessToken(token)
    }

    fun setIsUserBPC(typeName: String) {
        if (typeName.equals(BPC_USER_TYPE, true)) {
            prefRepo.setIsUserBPC(true)
        } else {
            prefRepo.setIsUserBPC(false)
        }
    }

    suspend fun generateOtp(
    ): ApiResponseModel<String>{
        val loginRequest =
            LoginRequest(mobileNumber = getMobileNumber() ?: "")
        NudgeLogger.d("OtpVerificationRepository ","generateOtp=> ${Gson().toJson(loginRequest)}")
        return apiInterface.generateOtp(loginRequest);
    }

    fun getMobileNumber(): String?{
       return prefRepo.getMobileNumber();
    }
    fun saveLoggedInUserType(userType: String) {
        coreSharedPrefs.setUserType(userType)
        prefRepo.savePref(PREF_KEY_TYPE_NAME, userType)
    }
}