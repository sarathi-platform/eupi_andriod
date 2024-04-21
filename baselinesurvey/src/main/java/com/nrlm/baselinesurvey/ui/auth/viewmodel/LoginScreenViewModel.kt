package com.nrlm.baselinesurvey.ui.auth.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.SUCCESS
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.model.datamodel.ErrorModel
import com.nrlm.baselinesurvey.ui.auth.presentation.LoginScreenEvent
import com.nrlm.baselinesurvey.ui.auth.presentation.MobileNumberState
import com.nrlm.baselinesurvey.ui.auth.use_case.LoginScreenUseCase
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.utils.states.LoaderState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    private val loginScreenUseCase: LoginScreenUseCase
): BaseViewModel() {

    private val TAG = LoginScreenViewModel::class.java.simpleName

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
                onEvent(LoaderEvent.UpdateLoaderState(true))
                job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
                        val loginResponse = loginScreenUseCase.generateOtpUseCase.invoke(event.mobileNumber.text) /*ApiResponseModel<String>(status = SUCCESS, "Otp successfully Send", data = "Otp successfully Send")*/
                        if (loginResponse.status.equals(SUCCESS, true)) {
                            _mobileNumberState.value = _mobileNumberState.value.copy(
                                isMobileNumberValidatedFromServer = true,
                                errorMessage = loginResponse.message
                            )
                            loginScreenUseCase.saveMobileNumberUseCase.invoke(event.mobileNumber.text)
                            onEvent(LoaderEvent.UpdateLoaderState(false))
                        } else {
                            _mobileNumberState.value = _mobileNumberState.value.copy(
                                isMobileNumberValidatedFromServer = false,
                                errorMessage = loginResponse.message
                            )
                            onEvent(LoaderEvent.UpdateLoaderState(false))
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

    override fun onServerError(error: ErrorModel?) {
        super.onServerError(error)
        _mobileNumberState.value = _mobileNumberState.value.copy(
            isMobileNumberValidatedFromServer = false,
            errorMessage = error?.message ?: "Something went wrong, please try again later!"
        )
        onEvent(LoaderEvent.UpdateLoaderState(false))
    }
    fun resetMobileNumberState() {
        _mobileNumberState.value = _mobileNumberState.value.copy(
            isMobileNumberValidatedFromServer = false,
            errorMessage = BLANK_STRING
        )
    }

}
