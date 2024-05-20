package com.nrlm.baselinesurvey.ui.language.presentation

import com.nrlm.baselinesurvey.database.entity.LanguageEntity

data class LanguagesState(
    val languageList: List<LanguageEntity> = emptyList(),
    val selectedLanguageId: Int = 0
)
