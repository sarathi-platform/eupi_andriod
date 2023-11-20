package com.nrlm.baselinesurvey.ui.auth.presentation

import androidx.compose.ui.text.input.TextFieldValue
import com.nrlm.baselinesurvey.BLANK_STRING

data class MobileNumberState(
    var mobileNumber: TextFieldValue = TextFieldValue(),
    var isMobileNumberValidatedFromServer: Boolean = false,
    val errorMessage: String = BLANK_STRING
)
