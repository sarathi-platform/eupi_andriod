package com.patsurvey.nudge.activities.ui.login

import android.os.CountDownTimer
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class OtpVerificationViewModel @Inject constructor(
    val prefRepo: PrefRepo
) :BaseViewModel() {
    val isResendOTPEnable= MutableStateFlow(false)
}