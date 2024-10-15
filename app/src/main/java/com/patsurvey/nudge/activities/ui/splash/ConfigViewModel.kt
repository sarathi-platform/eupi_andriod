package com.patsurvey.nudge.activities.ui.splash


import androidx.compose.runtime.mutableStateOf
import com.nudge.core.usecase.FetchAppConfigFromNetworkUseCase
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.utils.ApiType
import com.patsurvey.nudge.utils.FAIL
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.SPLASH_SCREEN_DURATION
import com.patsurvey.nudge.utils.SUCCESS
import com.patsurvey.nudge.utils.addDefaultLanguage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ConfigViewModel @Inject constructor(
    private val configRepository: ConfigRepository,
    val fetchAppConfigFromNetworkUseCase: FetchAppConfigFromNetworkUseCase
) : BaseViewModel() {

    fun isLoggedIn(): Boolean {
        return configRepository.getAccessToken()?.isNotEmpty() == true
    }

    fun getUserType(): String? {
        return configRepository.getUserType()
    }
    val showLoader = mutableStateOf(false)

    fun fetchLanguageDetails(callBack: (imageList: List<String>) -> Unit) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                NudgeLogger.d("ConfigViewModel", "fetchLanguageDetails -> start")
                NudgeLogger.d(
                    "ConfigViewModel",
                    "fetchLanguageDetails -> apiInterface.configDetails()"
                )
                    val response = configRepository.fetchLanguageFromAPI()
                    NudgeLogger.d(
                        "ConfigViewModel",
                        "fetchLanguageDetails -> response status = ${response.status}, message = ${response.message}, data = ${response.data.toString()}"
                    )
                    if (response.status.equals(SUCCESS, true)) {
                        response.data?.let {
                            it.languageList.forEach { language ->
                                NudgeLogger.d("ConfigViewModel", "$language")
                            }
                            NudgeLogger.d(
                                "ConfigViewModel",
                                "fetchLanguageDetails -> languageListDao.insertAll(it.languageList) before"
                            )
                            configRepository.insertAllLanguages(it)
                            NudgeLogger.d(
                                "ConfigViewModel",
                                "fetchLanguageDetails -> languageListDao.insertAll(it.languageList) after"
                            )
                            delay(SPLASH_SCREEN_DURATION)
                            withContext(Dispatchers.Main) {
                                callBack(it.image_profile_link)
                            }
                        }
                    } else if (response.status.equals(FAIL, true)) {
                        configRepository.addDefaultLanguage()
                        withContext(Dispatchers.Main) {
                            callBack(listOf())
                        }
                    }

            } catch (ex: Exception) {
                onCatchError(ex, ApiType.LANGUAGE_API)
                configRepository.addDefaultLanguage()
                withContext(Dispatchers.Main) {
                    callBack(listOf())
                }
            }
        }
    }



    override fun onServerError(error: ErrorModel?) {
        networkErrorMessage.value= error?.message.toString()
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            configRepository.onServerError(error)
        }
    }

    override fun onServerError(errorModel: ErrorModelWithApi?) {
        networkErrorMessage.value= errorModel?.message.toString()
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            configRepository.onServerError(errorModel)
        }
    }




    fun checkAndAddLanguage() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            NudgeLogger.d("ConfigViewModel", "checkAndAddLanguage -> called")
            val localLanguages = configRepository.getAllLanguages()
            NudgeLogger.d(
                "ConfigViewModel",
                "checkAndAddLanguage -> localLanguages: $localLanguages"
            )
            if (localLanguages.isEmpty())
                addDefaultLanguage(configRepository.languageListDao,configRepository.baselineLanguageDao)
        }
    }

    fun getLoggedInUserType(): String {
        return configRepository.getLoggedInUserType()
    }
    fun fetchAppConfigForProperties() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            fetchAppConfigFromNetworkUseCase.invoke()
        }
    }
}