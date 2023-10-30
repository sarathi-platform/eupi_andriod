package com.nrlm.baselinesurvey.ui.language.presentation

import com.nrlm.baselinesurvey.MainActivity

sealed class LanguageSelectionEvent {
    data class ToggleSelectedLanguageId(val id: Int): LanguageSelectionEvent()
    object SaveSelectedLanguage: LanguageSelectionEvent()
    data class UpdateSelectedVillage(val languageId: Int)
    data class ChangeAppLanguage(val mainActivity: MainActivity, val languageCode: String)
}
