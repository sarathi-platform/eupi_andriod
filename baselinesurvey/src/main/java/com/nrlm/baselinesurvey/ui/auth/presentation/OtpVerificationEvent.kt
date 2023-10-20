package com.nrlm.baselinesurvey.ui.auth.presentation

sealed class OtpVerificationEvent {
    data class ValidateOtpEvent(val otp: String)
    data class ResendOtpEvent(val mobileNumber: String)
}
