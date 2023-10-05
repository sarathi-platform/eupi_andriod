package com.patsurvey.nudge.activities.ui.login

import com.patsurvey.nudge.base.BaseRepository
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.model.request.LoginRequest
import com.patsurvey.nudge.model.response.ApiResponseModel
import javax.inject.Inject

class LoginRepository @Inject constructor(
    val prefRepo: PrefRepo
) : BaseRepository() {

    suspend fun generateOtp(loginRequest: LoginRequest): ApiResponseModel<String> {
        return apiInterface.generateOtp(loginRequest);
    }

    fun saveMobileNumber(mobileNumber: String) {
        prefRepo.saveMobileNumber(mobileNumber)
    }

}