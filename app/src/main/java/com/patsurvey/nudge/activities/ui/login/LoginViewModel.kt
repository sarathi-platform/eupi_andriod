package com.patsurvey.nudge.activities.ui.login

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.utils.SUCCESS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository
) : BaseViewModel() {
    val mobileNumber = mutableStateOf(TextFieldValue())

    val showLoader = mutableStateOf(false)
    fun generateOtp(onLoginResponse: (success: Boolean, message: String) -> Unit) {
        showLoader.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = loginRepository.generateOtp(mobileNumber.value.text)
            if (response.status.equals(SUCCESS, true)) {
                withContext(Dispatchers.Main) {
                    loginRepository.saveMobileNumber(mobileNumber.value.text)
                    showLoader.value = false
                    onLoginResponse(true, response.message)
                }
            } else {
                withContext(Dispatchers.Main) {
                    showLoader.value = false
                    onLoginResponse(false, response.message)
                }
            }
        }
    }

    override fun onServerError(error: ErrorModel?) {
        showLoader.value = false
        networkErrorMessage.value = error?.message.toString()
    }

    override fun onServerError(errorModel: ErrorModelWithApi?) {
        TODO("Not yet implemented")
    }
}