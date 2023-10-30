package com.nrlm.baselinesurvey.ui.auth.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.nrlm.baselinesurvey.SUCCESS
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.model.response.ApiResponseModel
import com.nrlm.baselinesurvey.ui.auth.presentation.LoginScreenEvent
import com.nrlm.baselinesurvey.ui.auth.presentation.MobileNumberState
import com.nrlm.baselinesurvey.ui.auth.use_case.LoginScreenUseCase
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.utils.LoaderState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    private val loginScreenUseCase: LoginScreenUseCase
): BaseViewModel() {

    private val _loaderState = mutableStateOf<LoaderState>(LoaderState())
    val loaderState: State<LoaderState> get() = _loaderState

    private val _mobileNumberState = mutableStateOf<MobileNumberState>(MobileNumberState())
    val mobileNumberState: State<MobileNumberState> get() = _mobileNumberState

    override fun <T> onEvent(event: T) {
        when (event) {
            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(
                    isLoaderVisible = event.showLoader
                )
            }
            is LoginScreenEvent.GenerateOtpEvent -> {
                job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
                    val loginResponse = /*loginScreenUseCase.generateOtpUseCase.invoke(event.mobileNumber.text)*/ ApiResponseModel<String>(status = SUCCESS, "Otp successfully Send", data = "Otp successfully Send")
                    if (loginResponse.status.equals(SUCCESS, true)) {
                        _mobileNumberState.value = _mobileNumberState.value.copy(
                            isMobileNumberValidatedFromServer = true,
                            errorMessage = loginResponse.message
                        )
                        loginScreenUseCase.saveMobileNumberUseCase.invoke(event.mobileNumber.text)
                    } else {
                        _mobileNumberState.value = _mobileNumberState.value.copy(
                            isMobileNumberValidatedFromServer = false,
                            errorMessage = loginResponse.message
                        )
                    }
                }
            }
            is LoginScreenEvent.OnValueChangeEvent -> {
                _mobileNumberState.value = _mobileNumberState.value.copy(
                    mobileNumber = event.mobileNumber
                )
            }
        }
    }

}
