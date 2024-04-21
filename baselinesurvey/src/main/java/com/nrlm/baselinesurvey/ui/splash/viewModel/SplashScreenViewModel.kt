package com.nrlm.baselinesurvey.ui.splash.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.nrlm.baselinesurvey.ROOM_INTEGRITY_EXCEPTION
import com.nrlm.baselinesurvey.SPLASH_SCREEN_DURATION
import com.nrlm.baselinesurvey.SUCCESS
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.database.entity.LanguageEntity
import com.nrlm.baselinesurvey.model.datamodel.ErrorModel
import com.nrlm.baselinesurvey.ui.splash.domain.use_case.SplashScreenUseCase
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.utils.BaselineCore
import com.nrlm.baselinesurvey.utils.states.LoaderState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    val splashScreenUseCase: SplashScreenUseCase
): BaseViewModel() {

    private val _loaderState = mutableStateOf<LoaderState>(LoaderState())
    val loaderState: State<LoaderState> get() = _loaderState

    private val _isLoggedIn = splashScreenUseCase.loggedInUseCase.invoke()
    val isLoggedIn: Boolean = _isLoggedIn

    override fun <T> onEvent(event: T) {
        when (event) {
            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(
                    isLoaderVisible = event.showLoader
                )
            }
        }
    }

    fun checkAndAddLanguage() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            splashScreenUseCase.saveLanguageConfigUseCase.addDefaultLanguage()
        }
    }

    fun fetchLanguageConfigDetails(callBack: () -> Unit) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val languageConfigResponse = splashScreenUseCase.fetchLanguageConfigFromNetworkUseCase.invoke()
                if (languageConfigResponse.status.equals(SUCCESS, true)) {
                    languageConfigResponse.data?.let { configResponseModel ->

                        /**
                         * Please Remove this code after Backend Add new language
                         */
                        val langList:ArrayList<LanguageEntity> = arrayListOf()
                        langList.addAll(configResponseModel.languageList)
                        langList.add(LanguageEntity(id = 6, orderNumber = 6, langCode = "ky",
                            language = "Kokborok",
                            localName = "Kokborok"
                        ))

                        splashScreenUseCase.saveLanguageConfigUseCase.invoke(langList)
                        BaselineCore.downloadQuestionImages(configResponseModel.image_profile_link)
                        delay(SPLASH_SCREEN_DURATION)
                        withContext(Dispatchers.Main) {
                            callBack()
                        }
                    }
                } else {
                    splashScreenUseCase.saveLanguageConfigUseCase.addDefaultLanguage()
                    withContext(Dispatchers.Main) {
                        callBack()
                    }
                }
            } catch (ex: Exception) {
                if (ex.message?.contains(ROOM_INTEGRITY_EXCEPTION, true) == false) {
                    splashScreenUseCase.saveLanguageConfigUseCase.addDefaultLanguage()
                }
                withContext(Dispatchers.Main) {
                    callBack()
                }
                onCatchError(ex)

            }
        }
    }

    override fun onServerError(error: ErrorModel?) {
        super.onServerError(error)


    }

}