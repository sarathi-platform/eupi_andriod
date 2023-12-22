package com.patsurvey.nudge.activities.ui.login

import com.google.gson.Gson
import com.patsurvey.nudge.base.BaseRepository
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.model.request.LoginRequest
import com.patsurvey.nudge.model.response.ApiResponseModel
import com.patsurvey.nudge.utils.NudgeLogger
import javax.inject.Inject

class LoginRepository @Inject constructor(
    val prefRepo: PrefRepo,
) : BaseRepository() {

    suspend fun generateOtp(mobileNumber: String): ApiResponseModel<String> {
        val loginRequest = LoginRequest(mobileNumber)
        NudgeLogger.d("LoginRepository ","generateOtp=> ${Gson().toJson(loginRequest)}")
        return apiInterface.generateOtp(loginRequest);
    }

    fun saveMobileNumber(mobileNumber: String) {
        prefRepo.saveMobileNumber(mobileNumber)
    }

}