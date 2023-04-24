package com.patsurvey.nudge.activities.ui.selectlanguage

import androidx.compose.runtime.mutableStateListOf

import androidx.lifecycle.viewModelScope
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.model.dataModel.LanguageSelectionModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CommonViewModel :BaseViewModel() {
    private val _languageList= MutableStateFlow<List<LanguageSelectionModel>?>(emptyList())
    val languageList=_languageList.asStateFlow()
   val list= mutableStateListOf<LanguageSelectionModel>()
    init {
        fetchLanguageList()
    }

    private fun fetchLanguageList() {
        viewModelScope.launch {
                list.add(
                    LanguageSelectionModel(language = "English",
                        "en", isSelected = false))
            list.add(
                    LanguageSelectionModel(language = "Hindi",
                        "hi", isSelected = false))
            list.add(
                    LanguageSelectionModel(language = "Bangali",
                        "bn-rIN", isSelected = false))
            list.add(
                    LanguageSelectionModel(language = "Bhojpuri",
                        "bh", isSelected = false))
            list.add(
                    LanguageSelectionModel(language = "Kannad",
                        "kn", isSelected = false))

            _languageList.value=list
        }
    }
}