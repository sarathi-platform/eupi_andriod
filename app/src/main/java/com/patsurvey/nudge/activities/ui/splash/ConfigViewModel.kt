package com.patsurvey.nudge.activities.ui.splash

import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.LanguageEntity
import com.patsurvey.nudge.database.dao.LanguageListDao
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.SPLASH_SCREEN_DURATION
import com.patsurvey.nudge.utils.SUCCESS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class ConfigViewModel @Inject constructor(
    val prefRepo: PrefRepo,
    val apiInterface: ApiService,
    private val languageListDao: LanguageListDao
) : BaseViewModel() {

    fun isLoggedIn(): Boolean {
        return prefRepo.getAccessToken()?.isNotEmpty() == true
    }

    init {

    }

    fun fetchLanguageDetails(callBack: () -> Unit) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            if (isLoggedIn()) {
                delay(SPLASH_SCREEN_DURATION)
                withContext(Dispatchers.Main) {
                    callBack()
                }
            }else {
                val response = apiInterface.configDetails()
                withContext(Dispatchers.IO) {
                    if (response.status.equals(SUCCESS, true)) {
                        response.data?.let {
                            languageListDao.insertAll(it.languageList)
                            delay(SPLASH_SCREEN_DURATION)
                            withContext(Dispatchers.Main) {
                                callBack()
                            }
                        }
                    } else {
                        onError("Error : ${response.message} ")
                    }
                }
            }
        }
    }
}