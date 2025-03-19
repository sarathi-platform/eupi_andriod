package com.patsurvey.nudge.activities.ui.login

import android.util.Base64
import com.google.gson.Gson
import com.nudge.core.LAST_SYNC_TIME
import com.nudge.core.enums.AppConfigKeysEnum
import com.nudge.core.model.response.LastSyncResponseModel
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.preference.CoreSharedPrefs.Companion.PREF_KEY_USER_NAME
import com.nudge.core.utils.AESHelper
import com.patsurvey.nudge.base.BaseRepository
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.model.request.LoginRequest
import com.patsurvey.nudge.model.request.OtpRequest
import com.patsurvey.nudge.model.response.ApiResponseModel
import com.patsurvey.nudge.model.response.OtpVerificationModel
import com.patsurvey.nudge.utils.BLANK_STRING
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
        val secretKeyPass = getAESSecretKey()

        val otpRequest =
            OtpRequest(
                mobileNumber = AESHelper.encrypt(getMobileNumber(), secretKeyPass),
                otp = otpNumber
            ) //Text this code
        NudgeLogger.d("OtpVerificationRepository","validateOtp => ${Gson().toJson(otpRequest)}")
        return apiInterface.validateOtp(otpRequest);
    }

    fun saveAccessToken(token: String){
        prefRepo.saveAccessToken(token)
    }

    fun saveLoggedInUserType(userType:String){
        prefRepo.savePref(PREF_KEY_TYPE_NAME,userType)
    }

    fun saveLoggedInUserId(userType: String) {
        prefRepo.savePref(PREF_KEY_USER_NAME, userType)
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
        val secretKeyPass = getAESSecretKey()

        val loginRequest = LoginRequest(AESHelper.encrypt(getMobileNumber(), secretKeyPass))
        NudgeLogger.d("OtpVerificationRepository ","generateOtp=> ${Gson().toJson(loginRequest)}")
        return apiInterface.generateOtp(loginRequest);
    }

    private fun getAESSecretKey(): String {
        val secretKeyPass = String(
            Base64.decode(
                coreSharedPrefs.getPref(
                    AppConfigKeysEnum.SENSITIVE_INFO_KEY.name,
                    com.nudge.core.BLANK_STRING
                ), Base64.DEFAULT
            )
        )
        return secretKeyPass
    }

    fun getMobileNumber(): String {
       return prefRepo.getMobileNumber();
    }

    suspend fun fetchLastDateTimeFromServer(): ApiResponseModel<LastSyncResponseModel> {
        return apiInterface.fetchLastSyncStatus(getMobileNumber() ?: BLANK_STRING)
    }

    fun saveLastSyncDateTime(syncDateTime: Long) {
        prefRepo.savePref(LAST_SYNC_TIME, syncDateTime)
    }

    fun savePageFrom() {
        prefRepo.savePageOpenFromOTPScreen(true)
    }

}