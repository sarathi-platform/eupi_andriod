package com.patsurvey.nudge.activities.ui.splash

import android.util.Log
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.LanguageEntity
import com.patsurvey.nudge.database.dao.LanguageListDao
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.FAIL
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

    fun fetchLanguageDetails(callBack: () -> Unit) {
        job = CoroutineScope(Dispatchers.IO).launch {
            try {

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
                        } else if (response.status.equals(FAIL, true)) {
                            addDefaultLanguage()
                            withContext(Dispatchers.Main) {
                                callBack()
                            }
                        } else {
                            onError(tag = "ConfigViewModel", "Error : ${response.message} ")
                            addDefaultLanguage()
                            withContext(Dispatchers.Main) {
                                callBack()
                            }
                        }
                    }

            } catch (ex: Exception) {
                onError(tag = "ConfigViewModel", "Error : ${ex.localizedMessage}")
                addDefaultLanguage()
                withContext(Dispatchers.Main) {
                    callBack()
                }
            }
        }
    }

    fun addDefaultLanguage() {
        languageListDao.insertLanguage(
            LanguageEntity(
                id = 1,
                language = "English",
                langCode = "en",
                orderNumber = 1,
                localName = "English"
            )
        )
    }

}