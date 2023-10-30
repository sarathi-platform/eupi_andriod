package com.nrlm.baselinesurvey.ui.auth.presentation

import androidx.compose.ui.text.input.TextFieldValue

data class MobileNumberState(
    var mobileNumber: TextFieldValue = TextFieldValue(),
    var isMobileNumberValidatedFromServer: Boolean = false,
    val errorMessage: String = ""
)
