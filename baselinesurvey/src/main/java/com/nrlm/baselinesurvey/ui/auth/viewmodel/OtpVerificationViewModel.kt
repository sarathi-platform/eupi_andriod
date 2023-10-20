package com.nrlm.baselinesurvey.ui.auth.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.nrlm.baselinesurvey.SUCCESS
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.model.request.LoginRequest
import com.nrlm.baselinesurvey.model.request.OtpRequest
import com.nrlm.baselinesurvey.ui.auth.presentation.OtpVerificationEvent
import com.nrlm.baselinesurvey.ui.auth.use_case.OtpVerificationUseCase
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.utils.BaselineCore
import com.nrlm.baselinesurvey.utils.LoaderState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtpVerificationViewModel @Inject constructor(
    private val otpVerificationUseCase: OtpVerificationUseCase
): BaseViewModel() {

    private val _loaderState = mutableStateOf<LoaderState>(LoaderState())
    val loaderState: State<LoaderState> get() = _loaderState

    val otpNumber = mutableStateOf("")
    val validateApiSuccess = mutableStateOf(false)
    val resendApiSuccess = mutableStateOf(false)
    val message = mutableStateOf("")

    override fun <T> onEvent(event: T) {
        when (event) {
            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(
                    isLoaderVisible = event.showLoader
                )
            }
            is OtpVerificationEvent.ValidateOtpEvent -> {
                job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
                    val otpRequest =
                        OtpRequest(mobileNumber = otpVerificationUseCase.getMobileNumberUseCase.invoke() ?: "", otp = if (otpNumber.value == "") BaselineCore.autoReadOtp.value else otpNumber.value ) //Text this code

                    val validateOtpResponse = otpVerificationUseCase.validateOtpUseCase.invoke(otpRequest)
                    if (validateOtpResponse.status.equals(SUCCESS, true)) {
                        validateOtpResponse.data?.let { responseData ->
                            otpVerificationUseCase.saveAccessTokenUseCase.invoke(responseData.token)
                            validateApiSuccess.value = true
                        }
                    } else {
                        message.value = validateOtpResponse.message
                    }
                }
            }
            is OtpVerificationEvent.ResendOtpEvent -> {
                job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
                    val mobileNumber =
                       otpVerificationUseCase.getMobileNumberUseCase.invoke()

                    val resendOtpResponse = otpVerificationUseCase.resendOtpUseCase.invoke(mobileNumber)
                    if (resendOtpResponse.status.equals(SUCCESS, true)) {
                        resendOtpResponse.data?.let { responseData ->
                            resendApiSuccess.value = true
                        }
                    } else {
                        message.value = resendOtpResponse.message
                    }
                }
            }
        }
    }

}