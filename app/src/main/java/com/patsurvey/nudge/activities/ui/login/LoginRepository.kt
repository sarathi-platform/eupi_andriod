package com.patsurvey.nudge.activities.ui.login

import com.google.gson.Gson
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nudge.core.preference.CorePrefRepo
import com.patsurvey.nudge.base.BaseRepository
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.model.request.LoginRequest
import com.patsurvey.nudge.model.response.ApiResponseModel
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.PREF_MOBILE_NUMBER
import javax.inject.Inject

class LoginRepository @Inject constructor(
    val prefRepo: PrefRepo,
    val corePrefRepo: CorePrefRepo
) : BaseRepository() {

    suspend fun generateOtp(mobileNumber: String): ApiResponseModel<String> {
        val loginRequest = LoginRequest(mobileNumber)
        NudgeLogger.d("LoginRepository ","generateOtp=> ${Gson().toJson(loginRequest)}")
        return apiInterface.generateOtp(loginRequest);
    }

    fun saveMobileNumber(mobileNumber: String) {
        prefRepo.saveMobileNumber(mobileNumber)
        corePrefRepo.savePref(PREF_MOBILE_NUMBER, mobileNumber)
    }

}