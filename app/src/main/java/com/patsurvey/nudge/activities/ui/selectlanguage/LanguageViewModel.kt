package com.patsurvey.nudge.activities.ui.selectlanguage

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.model.dataModel.LanguageSelectionModel
import com.patsurvey.nudge.utils.DEFAULT_LANGUAGE_CODE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LanguageViewModel @Inject constructor(
  val prefRepo: PrefRepo
) :BaseViewModel(){
    private val _languageList= MutableStateFlow<List<LanguageSelectionModel>?>(emptyList())
    val languageList=_languageList.asStateFlow()
   val list= mutableStateListOf<LanguageSelectionModel>()
    val languagePosition= mutableStateOf(0)
    init {
        fetchLanguageList()
        if(prefRepo.getAppLanguage()?.equals(DEFAULT_LANGUAGE_CODE,true)==false){
            findSavedLanguage()
        }
    }

    private fun fetchLanguageList() {

        viewModelScope.launch {
                list.add(
                    LanguageSelectionModel(language = "English",
                        "en", isSelected = true))
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
    fun findSavedLanguage(){
        list.mapIndexed{index, languageSelectionModel ->
            if(languageSelectionModel.code == prefRepo.getAppLanguage()){
                languagePosition.value = index
            }
        }

    }
}