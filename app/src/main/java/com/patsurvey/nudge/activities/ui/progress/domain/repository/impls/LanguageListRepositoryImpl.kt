package com.patsurvey.nudge.activities.ui.progress.domain.repository.impls

import com.patsurvey.nudge.activities.ui.progress.domain.repository.interfaces.LanguageListRepository
import com.patsurvey.nudge.database.LanguageEntity
import com.patsurvey.nudge.database.dao.LanguageListDao
import javax.inject.Inject

class LanguageListRepositoryImpl @Inject constructor(
    val languageListDao: LanguageListDao
) : LanguageListRepository {

    override suspend fun getAllLanguage(): List<LanguageEntity> {
        return languageListDao.getAllLanguages()
    }

    override suspend fun getLanguage(id: Int): LanguageEntity {
        return languageListDao.getLanguage(id)
    }

    override suspend fun insertLanguage(languageEntity: LanguageEntity) {
        languageListDao.insertLanguage(languageEntity)
    }

    override suspend fun insertAllLanguage(languageEntityList: List<LanguageEntity>) {
        languageListDao.insertAll(languageEntityList)
    }


}