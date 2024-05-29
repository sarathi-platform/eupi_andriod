package com.sarathi.surveymanager.utils

import androidx.core.text.isDigitsOnly

fun onlyNumberField(value: String): Boolean {
    if (value.isDigitsOnly() && value != "_" && value != "N") {
        return true
    }
    return false
}