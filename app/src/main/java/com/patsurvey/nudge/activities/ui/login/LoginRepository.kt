package com.patsurvey.nudge.activities.ui.login

import com.patsurvey.nudge.base.BaseRepository
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.model.request.LoginRequest
import com.patsurvey.nudge.model.response.ApiResponseModel
import com.patsurvey.nudge.network.interfaces.ApiService
import javax.inject.Inject

class LoginRepository @Inject constructor(
    val prefRepo: PrefRepo,
    val apiService: ApiService,
) : BaseRepository() {

    suspend fun generateOtp(mobileNumber: String): ApiResponseModel<String> {
        val loginRequest = LoginRequest(mobileNumber)
        return apiService.generateOtp(loginRequest);
    }

    fun saveMobileNumber(mobileNumber: String) {
        prefRepo.saveMobileNumber(mobileNumber)
    }

}