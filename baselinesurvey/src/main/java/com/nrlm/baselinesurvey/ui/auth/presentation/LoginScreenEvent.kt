package com.nrlm.baselinesurvey.ui.auth.presentation

import androidx.compose.ui.text.input.TextFieldValue

sealed class LoginScreenEvent {
    data class OnValueChangeEvent(val mobileNumber: TextFieldValue)
    data class GenerateOtpEvent(val mobileNumber: TextFieldValue)
}
