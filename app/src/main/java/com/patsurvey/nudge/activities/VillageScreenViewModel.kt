package com.patsurvey.nudge.activities

import android.util.Log
import com.patsurvey.nudge.RetryHelper
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.ApiType
import com.patsurvey.nudge.utils.BPC_USER_TYPE
import com.patsurvey.nudge.utils.DEFAULT_LANGUAGE_ID
import com.patsurvey.nudge.utils.FAIL
import com.patsurvey.nudge.utils.LAST_SYNC_TIME
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.PREF_KEY_EMAIL
import com.patsurvey.nudge.utils.PREF_KEY_IDENTITY_NUMBER
import com.patsurvey.nudge.utils.PREF_KEY_NAME
import com.patsurvey.nudge.utils.PREF_KEY_PROFILE_IMAGE
import com.patsurvey.nudge.utils.PREF_KEY_ROLE_NAME
import com.patsurvey.nudge.utils.PREF_KEY_TYPE_NAME
import com.patsurvey.nudge.utils.PREF_KEY_USER_NAME
import com.patsurvey.nudge.utils.RESPONSE_CODE_CONFLICT
import com.patsurvey.nudge.utils.RESPONSE_CODE_UNAUTHORIZED
import com.patsurvey.nudge.utils.SUCCESS
import com.patsurvey.nudge.utils.updateLastSyncTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class VillageScreenViewModel @Inject constructor(
    val prefRepo: PrefRepo,
    val apiService: ApiService
): BaseViewModel() {

    /*fun init () {
        fetchUserDetails { success ->

        }
    }

    private fun fetchUserDetails(apiSuccess: (success: Boolean) -> Unit) {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val villageRequest = "2"
            val response = apiService.userAndVillageListAPI(villageRequest)
            if (!response.status.equals(SUCCESS, true))
                apiSuccess(false)

            response.data?.let {
                if (it.typeName.equals(BPC_USER_TYPE, true)) {
                    prefRepo.setIsUserBPC(true)
                } else {
                    prefRepo.setIsUserBPC(false)
                }
                apiSuccess(true)
            }
        }
    }*/


    fun isUserBpc() = prefRepo.isUserBPC()

    override fun onServerError(error: ErrorModel?) {
        TODO("Not yet implemented")
    }

    override fun onServerError(errorModel: ErrorModelWithApi?) {
        TODO("Not yet implemented")
    }
}