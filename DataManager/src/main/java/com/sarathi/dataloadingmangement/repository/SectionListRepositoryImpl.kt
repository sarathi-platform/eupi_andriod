package com.sarathi.dataloadingmangement.repository

import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.value
import com.sarathi.dataloadingmangement.data.dao.SectionEntityDao
import com.sarathi.dataloadingmangement.data.dao.SectionStatusEntityDao
import com.sarathi.dataloadingmangement.data.entities.SectionStatusEntity
import com.sarathi.dataloadingmangement.model.uiModel.SectionUiModel
import javax.inject.Inject

class SectionListRepositoryImpl @Inject constructor(
    val coreSharedPrefs: CoreSharedPrefs,
    val sectionEntityDao: SectionEntityDao,
    val sectionStatusEntityDao: SectionStatusEntityDao
) : SectionListRepository {


    override fun getSectionListForSurvey(surveyId: Int): List<SectionUiModel> {
        return sectionEntityDao.getAllSectionForSurveyInLanguage(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            surveyId = surveyId,
            languageCode = coreSharedPrefs.getSelectedLanguageCode()
        )
    }

    override suspend fun getSectionStatusForTask(
        surveyId: Int,
        taskId: Int,
    ): List<SectionStatusEntity> {
        return sectionStatusEntityDao.getStatusForTask(
            surveyId,
            taskId,
            coreSharedPrefs.getUniqueUserIdentifier()
        ).value()
    }

}