package com.patsurvey.nudge.activities.ui.login

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.model.request.LoginRequest
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.SUCCESS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    val prefRepo: PrefRepo,
    val apiInterface: ApiService,
):BaseViewModel() {
    val mobileNumber = mutableStateOf(TextFieldValue())

    fun generateOtp(onLoginResponse: ()->Unit){
        val loginRequest=LoginRequest(mobileNumber ="9602854036" /*mobileNumber.value.text*/)
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = apiInterface.generateOtp(loginRequest)
            withContext(Dispatchers.IO){
                if(response.status.equals(SUCCESS,true)){
                    withContext(Dispatchers.Main){
                        onLoginResponse()
                    }
                }else{
                    onError("Error : ${response.message} ")
                }
            }
        }
    }
}