package com.patsurvey.nudge.activities.ui.login

import android.os.CountDownTimer
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class OtpVerificationViewModel @Inject constructor(
    val prefRepo: PrefRepo
) :BaseViewModel() {


    fun resendOtp() {
        //TODO call resend OTP API
    }

}