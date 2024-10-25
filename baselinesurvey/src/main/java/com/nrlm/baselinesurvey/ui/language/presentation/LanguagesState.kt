package com.nrlm.baselinesurvey.ui.language.presentation

import com.nudge.core.database.entities.language.LanguageEntity

data class LanguagesState(
    val languageList: List<LanguageEntity> = emptyList(),
    val selectedLanguageId: Int = 0
)
