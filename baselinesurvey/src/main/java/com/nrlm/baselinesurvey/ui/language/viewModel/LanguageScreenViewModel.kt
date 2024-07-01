package com.nrlm.baselinesurvey.ui.language.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.data.prefs.PrefBSRepo
import com.nrlm.baselinesurvey.ui.language.presentation.LanguageSelectionEvent
import com.nrlm.baselinesurvey.ui.language.presentation.LanguagesState
import com.nrlm.baselinesurvey.ui.language.domain.use_case.LanguageScreenUseCase
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.utils.states.LoaderState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LanguageScreenViewModel @Inject constructor(
    private val languageScreenUseCase: LanguageScreenUseCase,
    val prefBSRepo: PrefBSRepo
): BaseViewModel() {

    private val _languagesState = mutableStateOf<LanguagesState>(LanguagesState())
    val languagesState: State<LanguagesState> get() = _languagesState

    private val _loaderState = mutableStateOf<LoaderState>(LoaderState())
    val loaderState: State<LoaderState> get() = _loaderState

    val isLanguageVillageAvailable= mutableStateOf(true)

    fun init() {
        getLanguages()
    }

    private fun getLanguages() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            _languagesState.value = _languagesState.value.copy(
                languageList = languageScreenUseCase.getLanguageListFromDbUseCase.invoke()
            )
        }
    }


    override fun <T> onEvent(event: T) {
        when (event) {
            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(
                    isLoaderVisible = event.showLoader
                )
            }
            is LanguageSelectionEvent.ToggleSelectedLanguageId -> {
                _languagesState.value = _languagesState.value.copy(
                    selectedLanguageId = event.id
                )
            }
            is LanguageSelectionEvent.SaveSelectedLanguage -> {
                languageScreenUseCase.saveSelectedLanguageUseCase
                    .saveSelectedLanguageId(_languagesState.value.languageList[languagesState.value.selectedLanguageId].id)
            }
            is LanguageSelectionEvent.UpdateSelectedVillage -> {
                viewModelScope.launch {
                    val selectVillage = languageScreenUseCase.getSelectedVillageUseCase.invoke()
                    val selectedVillageForLanguage = languageScreenUseCase.getVillageDetailUseCase.invoke(
                        selectVillage.id,
                        _languagesState.value.languageList[languagesState.value.selectedLanguageId].id
                    )
                    if (selectedVillageForLanguage != null && selectVillage.id != 0) {
                        languageScreenUseCase.saveSelectedVillageUseCase.invoke(selectedVillageForLanguage)
                    } else {
                        isLanguageVillageAvailable.value=false
                    }
                }
            }
            is LanguageSelectionEvent.ChangeAppLanguage -> {
                languageScreenUseCase.saveSelectedLanguageUseCase.saveSelectedLanguageCode(event.mainActivity, event.languageCode)
            }
        }

    }

    fun getLanguageScreenOpenFrom():Boolean{
        return languageScreenUseCase.getLanguageScreenOpenFromUserCase.invoke()
    }

}