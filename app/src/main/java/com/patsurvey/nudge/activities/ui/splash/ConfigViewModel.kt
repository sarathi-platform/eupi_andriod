package com.patsurvey.nudge.activities.ui.splash


import android.content.Context
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.BpcScorePercentageEntity
import com.patsurvey.nudge.database.LanguageEntity
import com.patsurvey.nudge.database.dao.BpcScorePercentageDao
import com.patsurvey.nudge.database.dao.CasteListDao
import com.patsurvey.nudge.database.dao.LanguageListDao
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.FAIL
import com.patsurvey.nudge.utils.SPLASH_SCREEN_DURATION
import com.patsurvey.nudge.utils.SUCCESS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ConfigViewModel @Inject constructor(
    val prefRepo: PrefRepo,
    val apiInterface: ApiService,
    private val languageListDao: LanguageListDao,
    val casteListDao: CasteListDao,
    val bpcScorePercentageDao: BpcScorePercentageDao
) : BaseViewModel() {
    fun isLoggedIn(): Boolean {
        return prefRepo.getAccessToken()?.isNotEmpty() == true
    }

    fun fetchLanguageDetails(context: Context, callBack: (imageList:List<String>) -> Unit) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {

                val response = apiInterface.configDetails()
                val localCasteList = casteListDao.getAllCaste()
                /*if (localCasteList.isNotEmpty()) {
                    casteListDao.deleteCasteTable()
                }*/
                withContext(Dispatchers.IO) {
                    if (response.status.equals(SUCCESS, true)) {
                        response.data?.let {
                            languageListDao.insertAll(it.languageList)
                            /*it.image_profile_link.forEach {
                                //val imageUrl="https://cdn.pixabay.com/photo/2017/07/19/16/44/questions-2519654_960_720.png"
                                downloadImageItem(context,it)
                            }*/
                            it.bpcSurveyPercentage.forEach { bpcScorePercentage ->
                                bpcScorePercentageDao.insert(
                                    BpcScorePercentageEntity(
                                        percentage = bpcScorePercentage.percentage,
                                        name = bpcScorePercentage.name,
                                        stateId = bpcScorePercentage.id
                                    )
                                )
                            }
                            delay(SPLASH_SCREEN_DURATION)
                            withContext(Dispatchers.Main) {
                                callBack(it.image_profile_link)
                            }
                        }
                    } else if (response.status.equals(FAIL, true)) {
                        addDefaultLanguage()
                        withContext(Dispatchers.Main) {
                            callBack(listOf())
                        }
                    } else {
                        onError(tag = "ConfigViewModel", "Error : ${response.message} ")
                        addDefaultLanguage()
                        withContext(Dispatchers.Main) {
                            callBack(listOf())
                        }
                    }
                }

            } catch (ex: Exception) {
                onCatchError(ex)
                withContext(Dispatchers.Main) {
                    callBack(listOf())
                }
            }
        }
    }

    fun addDefaultLanguage() {
        languageListDao.insertLanguage(
            LanguageEntity(
                id = 2,
                language = "English",
                langCode = "en",
                orderNumber = 1,
                localName = "English"
            )
        )
    }

    override fun onServerError(error: ErrorModel?) {
        networkErrorMessage.value= error?.message.toString()
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            addDefaultLanguage()
        }
    }

    override fun onServerError(errorModel: ErrorModelWithApi?) {
        networkErrorMessage.value= errorModel?.message.toString()
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            addDefaultLanguage()
        }
    }




    fun checkAndAddLanguage() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val localLanguages = languageListDao.getAllLanguages()
            if (localLanguages.isEmpty())
                languageListDao.insertLanguage(
                    LanguageEntity(
                        id = 2,
                        language = "English",
                        langCode = "en",
                        orderNumber = 1,
                        localName = "English"
                    )
                )
        }
    }
}