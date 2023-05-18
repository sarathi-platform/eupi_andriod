package com.patsurvey.nudge.activities.ui.splash


import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.LanguageEntity
import com.patsurvey.nudge.database.dao.CasteListDao
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
    private val languageListDao: LanguageListDao,
    val casteListDao: CasteListDao
) : BaseViewModel() {
    init {

    }

    fun isLoggedIn(): Boolean {
        return prefRepo.getAccessToken()?.isNotEmpty() == true
    }

    fun fetchLanguageDetails(callBack: () -> Unit) {
        job = CoroutineScope(Dispatchers.IO).launch {
            try {

                    val response = apiInterface.configDetails()
                val localCasteList = casteListDao.getAllCaste()
                if(localCasteList.isNotEmpty()){
                    casteListDao.deleteCasteTable()
                }
                    withContext(Dispatchers.IO) {
                        if (response.status.equals(SUCCESS, true)) {
                            response.data?.let {
                                languageListDao.insertAll(it.languageList)
                                it.languageList.forEach { language->
                                    launch {
                                        // Fetch CasteList from Server
                                        val casteResponse = apiInterface.getCasteList(language.id)
                                        if(casteResponse.status.equals(SUCCESS,true)){
                                            casteResponse.data?.let { casteList->
                                                casteList.forEach { casteEntity ->
                                                    casteEntity.languageId=language.id
                                                }
                                                casteListDao.insertAll(casteList)
                                            }
                                        }
                                    }
                                }
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