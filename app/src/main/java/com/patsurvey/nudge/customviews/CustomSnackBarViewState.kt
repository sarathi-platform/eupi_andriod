package com.patsurvey.nudge.customviews

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class CustomSnackBarViewState {

    private val _message = mutableStateOf<String?>(null)
    val message: State<String?> = _message

    private val _messageIcon = mutableStateOf<Int?>(null)
    val messageIcon: State<Int?> = _messageIcon

    private val _isSuccess = mutableStateOf<Boolean>(true)
    val isSuccess: State<Boolean> = _isSuccess

    private val _isCustomIcon = mutableStateOf<Boolean>(false)
    val isCustomIcon: State<Boolean> = _isCustomIcon


    var updateState by mutableStateOf(false)
        private set

    fun addMessage(message: String,messageIcon:Int,isSuccess:Boolean,isCustomIcon:Boolean) {
        _message.value = message
        _messageIcon.value=messageIcon
        _isSuccess.value=isSuccess
        _isCustomIcon.value=isCustomIcon
        updateState = !updateState
    }

    fun isNotEmpty(): Boolean {
        return _message.value != null
    }

    fun addMessage(message: String, isSuccess: Boolean, isCustomIcon: Boolean) {
        _message.value = message
        _isSuccess.value=isSuccess
        _isCustomIcon.value=isCustomIcon
        updateState = !updateState
    }


}