package com.tothenew.android_starter_project.module.login

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tothenew.android_starter_project.base.BaseViewModel
import com.tothenew.android_starter_project.model.response.ApiResponseModel
import com.tothenew.android_starter_project.model.responseModel.LoginResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : BaseViewModel() {
    private val validEmails =
        arrayListOf(
            "puneet.sharma@tothenew.com",
            "naveen.singh@tothenew.com",
            "anil.yadav@tothenew.com",
            "ankur.mishra@tothenew.com"
        )
    private val validPassword = arrayListOf("Test@1234", "Test@1234", "Test@1234", "Test@1234")


    var userCredential = MutableLiveData<UserCredential>().apply {
        value = UserCredential()
    }

    private var _userLoginResult = MutableLiveData<ApiResponseModel>()
    val userLoginResult: LiveData<ApiResponseModel> = _userLoginResult

    fun onEmailUpdate(text: String?) {
        val cred = userCredential.value ?: UserCredential()
        val credential = cred.copy(email = text).apply {
            isValidEmail = isValidEmail(text)
        }
        userCredential.value = credential
    }

    fun onPasswordUpdate(text: String?) {
        val cred = userCredential.value ?: UserCredential()
        val credential = cred.copy(password = text).apply {
            isValidPassword = isValidPassword(text)
        }
        userCredential.value = credential
    }

    private fun isValidEmail(text: String?): Boolean {
        val isValid =
            text != null && !TextUtils.isEmpty(text) && android.util.Patterns.EMAIL_ADDRESS.matcher(
                text
            ).matches();
        Log.i("LoginViewModel", "validate for text: $text isValid: $isValid")
        return isValid
    }

    private fun isValidPassword(password: String?): Boolean {
        password?.let {
            val passwordPattern =
                "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$"
            val passwordMatcher = Regex(passwordPattern)

            return passwordMatcher.find(password) != null
        } ?: return false
    }

    fun hitLogin() {
        val credential = userCredential.value
        val response = validateLogin(credential?.email ?: "", credential?.password ?: "")
        _userLoginResult.value = response
    }

    private fun validateLogin(email: String, password: String): ApiResponseModel {
        if (validEmails.contains(email)) {
            val index = validEmails.indexOf(email)
            return if (index >= 0 && validPassword.size > index && validPassword[index] == password) {
                ApiResponseModel(LoginResponse(email), null, null, null)
            } else {
                ApiResponseModel(
                    null,
                    404,
                    null,
                    null,
                    "Invalid Password",
                    httpCode = 404,
                    mError = null
                )
            }
        } else {
             return ApiResponseModel(
                null,
                404,
                null,
                null,
                "Invalid user email and password",
                httpCode = 404,
                mError = null
            )
        }
    }

    data class UserCredential(
        var email: String? = null,
        var password: String? = null,
        var isValidEmail: Boolean? = false,
        var isValidPassword: Boolean? = false
    )

}