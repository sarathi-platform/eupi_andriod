package com.patsurvey.nudge.activities.ui.login

import android.util.Base64
import com.google.gson.Gson
import com.nudge.core.enums.AppConfigKeysEnum
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.utils.AESHelper
import com.patsurvey.nudge.base.BaseRepository
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.model.request.LoginRequest
import com.patsurvey.nudge.model.response.ApiResponseModel
import com.patsurvey.nudge.utils.NudgeLogger
import javax.inject.Inject

class LoginRepository @Inject constructor(
    val prefRepo: PrefRepo,
    val coreSharedPrefs: CoreSharedPrefs
) : BaseRepository() {

    suspend fun generateOtp(mobileNumber: String): ApiResponseModel<String> {
        val secretKeyPass = String(
            Base64.decode(
                coreSharedPrefs.getPref(
                    AppConfigKeysEnum.SENSITIVE_INFO_KEY.name,
                    com.nudge.core.BLANK_STRING
                ), Base64.DEFAULT
            )
        )

        val loginRequest = LoginRequest(AESHelper.encrypt(mobileNumber, secretKeyPass))
        NudgeLogger.d("LoginRepository ","generateOtp=> ${Gson().toJson(loginRequest)}")
        return apiInterface.generateOtp(loginRequest);
    }

    fun saveMobileNumber(mobileNumber: String) {
        prefRepo.saveMobileNumber(mobileNumber)
        coreSharedPrefs.setMobileNo(mobileNumber)
    }

}