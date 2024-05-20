package com.nrlm.baselinesurvey.ui.profile.viewmodel

import androidx.compose.runtime.mutableStateOf
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.ui.profile.domain.use_case.ProfileBSUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileBSViewModel @Inject constructor(
    val profileBSUseCase: ProfileBSUseCase):BaseViewModel() {

    val userName= mutableStateOf(BLANK_STRING)
    val userEmail= mutableStateOf(BLANK_STRING)
    val userMobileNumber= mutableStateOf(BLANK_STRING)
    val userIdentityNumber= mutableStateOf(BLANK_STRING)
    override fun <T> onEvent(event: T) {

    }
    init {
        getAllUserDetails()
    }

    fun getAllUserDetails(){
        userName.value = profileBSUseCase.getUserNameUseCase.invoke()
        userEmail.value = profileBSUseCase.getUserEmailUseCase.invoke()
        userMobileNumber.value = profileBSUseCase.getUserMobileNumberUseCase.invoke()
        userIdentityNumber.value = profileBSUseCase.getIdentityNumberUseCase.invoke()
    }
}