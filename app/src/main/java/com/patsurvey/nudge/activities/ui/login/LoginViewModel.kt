package com.patsurvey.nudge.activities.ui.login

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.model.request.LoginRequest
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.network.model.ErrorModel
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.FAIL
import com.patsurvey.nudge.utils.SUCCESS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    val prefRepo: PrefRepo,
    val apiInterface: ApiService,
) : BaseViewModel() {
    val mobileNumber = mutableStateOf(TextFieldValue())

    val showLoader = mutableStateOf(false)
    fun generateOtp(onLoginResponse: (success: Boolean, message: String) -> Unit) {
        showLoader.value = true
        val loginRequest = LoginRequest(mobileNumber = mobileNumber.value.text)
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = apiInterface.generateOtp(loginRequest)
            withContext(Dispatchers.IO) {
                if (response.status.equals(SUCCESS, true)) {
                    withContext(Dispatchers.Main) {
                        prefRepo.saveMobileNumber(mobileNumber.value.text)
                        showLoader.value = false
                        onLoginResponse(true, response.message)
                    }
                } else if (response.status.equals(FAIL, true)) {
                    withContext(Dispatchers.Main) {
                        showLoader.value = false
                        onLoginResponse(false, response.message)
                    }
                }
                else {
                    withContext(Dispatchers.Main) {
                        showLoader.value = false
                        onLoginResponse(false, response.message)
                    }

                }
            }
        }
    }

    override fun onServerError(error: ErrorModel?) {
        showLoader.value = false
        networkErrorMessage.value= error?.message.toString()
    }
}