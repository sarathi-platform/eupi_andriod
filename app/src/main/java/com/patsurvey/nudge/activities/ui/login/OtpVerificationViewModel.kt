package com.patsurvey.nudge.activities.ui.login

import androidx.compose.runtime.mutableStateOf
import com.nudge.core.analytics.mixpanel.AnalyticsEvents
import com.nudge.syncmanager.domain.usecase.SyncManagerUseCase
import com.patsurvey.nudge.RetryHelper
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.CRP_USER_TYPE
import com.patsurvey.nudge.utils.FAIL
import com.patsurvey.nudge.utils.SUCCESS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class OtpVerificationViewModel @Inject constructor(
    private val otpVerificationRepository: OtpVerificationRepository,
    private val syncManagerUseCase: SyncManagerUseCase,

    ) : BaseViewModel() {

    val otpNumber = mutableStateOf("")
    val showLoader = mutableStateOf(false)
    private val _villageList= MutableStateFlow<List<VillageEntity>?>(emptyList())
    val villageList=_villageList.asStateFlow()

    fun validateOtp(onOtpResponse: (userType: String, success: Boolean, message: String) -> Unit) {
        showLoader.value = true
        val otpNum = if (otpNumber.value == "") RetryHelper.autoReadOtp.value else otpNumber.value
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = otpVerificationRepository.validateOtp(otpNum)
            if (response.status.equals(SUCCESS, true)) {
                response.data?.let {
                    otpVerificationRepository.saveAccessToken(it.token)
                    otpVerificationRepository.saveLoggedInUserType(userType = it.typeName ?: BLANK_STRING)
                    otpVerificationRepository.setIsUserBPC(it.typeName ?: BLANK_STRING)
                    showLoader.value = false
                    getLastSyncDateTimeFromServer()
                    withContext(Dispatchers.Main) {
                        onOtpResponse(it.typeName?: CRP_USER_TYPE,true,response.message)
                    }
                }
                syncManagerUseCase.syncAnalyticsEventUseCase.sentAnalyticsEvent(AnalyticsEvents.LOGIN.eventName)

            } else {
                onError(tag = "OtpVerificationViewModel", "Error : ${response.message}")
                withContext(Dispatchers.Main) {
                    showLoader.value = false
                    onOtpResponse(CRP_USER_TYPE,false, response.message)
                }
            }
        }
    }

    fun resendOtp(onResendOtpResponse: (success: Boolean, message: String) -> Unit) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = otpVerificationRepository.generateOtp()
            if (response.status.equals(SUCCESS, true)) {
                withContext(Dispatchers.Main) {
                    onResendOtpResponse(true, response.message)
                }
            } else if (response.status.equals(FAIL, true)) {
                withContext(Dispatchers.Main) {
                    onResendOtpResponse(false, response.message)
                }
            }
        }
    }

    override fun onServerError(error: ErrorModel?) {
        showLoader.value = false
        networkErrorMessage.value= error?.message.toString()
    }

    override fun onServerError(errorModel: ErrorModelWithApi?) {
        TODO("Not yet implemented")
    }
    fun savePageFromOTPScreen() {
        otpVerificationRepository.savePageFrom()
    }

    fun getLastSyncDateTimeFromServer() {
        job = CoroutineScope(Dispatchers.IO).launch {
            val response = otpVerificationRepository.fetchLastDateTimeFromServer()
            if (response.status == SUCCESS) {
                response.data?.lastSyncDate?.let {
                    if (it > 0) {
                        otpVerificationRepository.saveLastSyncDateTime(it)
                    }
                }
            }
        }
    }
}