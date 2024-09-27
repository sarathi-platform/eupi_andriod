package com.patsurvey.nudge.activities.ui.selectlanguage

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.nudge.core.DEFAULT_LANGUAGE_CODE
import com.nudge.core.DEFAULT_LANGUAGE_ID
import com.nudge.core.DEFAULT_LANGUAGE_LOCAL_NAME
import com.nudge.core.DEFAULT_LANGUAGE_NAME
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.database.LanguageEntity
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.utils.NudgeLogger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LanguageViewModel @Inject constructor(
    val languageRepository: LanguageRepository
) : BaseViewModel() {


    private val _languageList = MutableStateFlow<List<LanguageEntity>?>(emptyList())
    val languageList = _languageList.asStateFlow()
    val list = mutableStateListOf<LanguageEntity>()
    val languagePosition = mutableStateOf(0)

    init {
        fetchLanguageList()
    }

    fun fetchLanguageList() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val list = languageRepository.getAllLanguages()
                if (list.isNullOrEmpty()) {
                    _languageList.value = listOf(
                        getDefaultLanguage()
                    )
                } else {
                    _languageList.value = list
                }
            } catch (ex: Exception) {
                NudgeLogger.e("LanguageViewModel", "fetchLanguageList: ", ex)
                _languageList.value = listOf(
                    getDefaultLanguage()
                )
            }

        }
    }

    override fun onServerError(error: ErrorModel?) {
        networkErrorMessage.value = error?.message.toString()
    }

    override fun onServerError(errorModel: ErrorModelWithApi?) {
        networkErrorMessage.value = errorModel?.message.toString()
    }

    fun updateSelectedVillage(languageId: Int, onVillageSelectionFailed: () -> Unit) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                languageRepository.fetchVillageDetailsForLanguage(languageId)
            } catch (ex: Exception) {
                onVillageSelectionFailed()
            }
        }
    }

    fun getDefaultLanguage(): LanguageEntity {
        return LanguageEntity(
            id = DEFAULT_LANGUAGE_ID,
            language = DEFAULT_LANGUAGE_NAME,
            langCode = DEFAULT_LANGUAGE_CODE,
            orderNumber = 1,
            localName = DEFAULT_LANGUAGE_LOCAL_NAME
        )
    }
}