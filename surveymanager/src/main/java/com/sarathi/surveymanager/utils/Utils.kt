package com.sarathi.surveymanager.utils

import androidx.core.text.isDigitsOnly
import com.nudge.core.BLANK_STRING

fun onlyNumberField(value: String): Boolean {
    if (value.isDigitsOnly() && value != "_" && value != "N") {
        return true
    }
    return false
}

fun onlyNumberField(value: String, excludeBlankSpace: Boolean = false): Boolean {
    var result = false
    if (excludeBlankSpace) {
        result = value.isDigitsOnly() && value != "_" && value != "N" && value != BLANK_STRING
    } else {
        result = onlyNumberField(value)
    }
    return result
}