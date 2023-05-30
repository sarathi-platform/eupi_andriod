package com.patsurvey.nudge.activities.ui.login

import androidx.compose.runtime.mutableStateOf
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.model.request.LoginRequest
import com.patsurvey.nudge.model.request.OtpRequest
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.network.model.ErrorModel
import com.patsurvey.nudge.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class OtpVerificationViewModel @Inject constructor(
    val prefRepo: PrefRepo,
    val apiInterface: ApiService,
    val villageListDao: VillageListDao
) : BaseViewModel() {

    val otpNumber = mutableStateOf("")
    val showLoader = mutableStateOf(false)
    private val _villageList= MutableStateFlow<List<VillageEntity>?>(emptyList())
    val villageList=_villageList.asStateFlow()

    fun validateOtp(onOtpResponse: (success: Boolean, message: String) -> Unit) {
        showLoader.value = true
        val otpRequest =
            OtpRequest(mobileNumber = prefRepo.getMobileNumber() ?: "", otp = otpNumber.value)
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = apiInterface.validateOtp(otpRequest)
            withContext(Dispatchers.IO) {
                if (response.status.equals(SUCCESS, true)) {
                    response.data?.let {
                        prefRepo.saveAccessToken(it.token)
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
                    onError(tag = "OtpVerificationViewModel", "Error : ${response.message}")
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
                    onError(tag = "OtpVerificationViewModel", "Error : ${response.message}")
                    withContext(Dispatchers.Main) {
                        showLoader.value = false
                        onResendOtpResponse(false, response.message)
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