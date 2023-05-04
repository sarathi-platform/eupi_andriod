package com.patsurvey.nudge.activities.ui.login

import androidx.compose.runtime.mutableStateOf
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.model.request.LoginRequest
import com.patsurvey.nudge.model.request.OtpRequest
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.AUTH_TOKEN_PREFIX
import com.patsurvey.nudge.utils.FAIL
import com.patsurvey.nudge.utils.SUCCESS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class OtpVerificationViewModel @Inject constructor(
    val prefRepo: PrefRepo,
    val apiInterface: ApiService
) : BaseViewModel() {

    val otpNumber = mutableStateOf("")
    val showLoader = mutableStateOf(false)

    fun validateOtp(onOtpResponse: (success: Boolean, message: String) -> Unit) {
        showLoader.value = true
        val otpRequest =
            OtpRequest(mobileNumber = prefRepo.getMobileNumber() ?: "", otp = otpNumber.value)
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = apiInterface.validateOtp(otpRequest)
            withContext(Dispatchers.IO) {
                if (response.status.equals(SUCCESS, true)) {
                    response.data?.let {
                        prefRepo.saveAccessToken("$AUTH_TOKEN_PREFIX ${it.token}")
                    }
                    showLoader.value = false
                    withContext(Dispatchers.Main) {
                        onOtpResponse(true, response.message)
                    }
                } else if (response.status.equals(FAIL, true)) {
                    withContext(Dispatchers.Main) {
                        showLoader.value = false
                        onOtpResponse(false, response.message)
                    }
                } else {
                    onError("Error : ${response.message}")
                    withContext(Dispatchers.Main) {
                        showLoader.value = false
                        onOtpResponse(false, response.message)
                    }
                }
            }
        }
    }

    fun resendOtp(onResendOtpResponse: (success: Boolean, message: String) -> Unit) {
        val loginRequest = LoginRequest(mobileNumber = prefRepo.getMobileNumber() ?: "")
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = apiInterface.generateOtp(loginRequest)
            withContext(Dispatchers.IO) {
                if (response.status.equals(SUCCESS, true)) {
                    withContext(Dispatchers.Main) {
                        onResendOtpResponse(true, response.message)
                    }
                } else if (response.status.equals(FAIL, true)) {
                    withContext(Dispatchers.Main) {
                        onResendOtpResponse(false, response.message)
                    }
                } else {
                    onError("Error : ${response.message}")
                    withContext(Dispatchers.Main) {
                        showLoader.value = false
                        onResendOtpResponse(false, response.message)
                    }
                }
            }
        }
    }

}