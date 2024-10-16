package com.patsurvey.nudge.activities.ui.progress.domain.repository.interfaces

import com.patsurvey.nudge.database.LanguageEntity

interface LanguageListRepository {

    suspend fun getAllLanguage(): List<LanguageEntity>

    suspend fun getLanguage(id: Int): LanguageEntity

    suspend fun insertLanguage(languageEntity: LanguageEntity)

    suspend fun insertAllLanguage(languageEntityList: List<LanguageEntity>)

}