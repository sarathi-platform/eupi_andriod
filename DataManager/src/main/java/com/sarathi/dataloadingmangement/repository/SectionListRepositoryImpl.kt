package com.sarathi.dataloadingmangement.repository

import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.value
import com.sarathi.dataloadingmangement.data.dao.SectionEntityDao
import com.sarathi.dataloadingmangement.data.dao.SectionStatusEntityDao
import com.sarathi.dataloadingmangement.data.dao.SurveyEntityDao
import com.sarathi.dataloadingmangement.data.entities.SectionStatusEntity
import com.sarathi.dataloadingmangement.data.entities.SurveyEntity
import com.sarathi.dataloadingmangement.model.uiModel.SectionUiModel
import javax.inject.Inject

class SectionListRepositoryImpl @Inject constructor(
    val coreSharedPrefs: CoreSharedPrefs,
    val surveyEntityDao: SurveyEntityDao,
    val sectionEntityDao: SectionEntityDao,
    val sectionStatusEntityDao: SectionStatusEntityDao
) : SectionListRepository {


    override suspend fun getSectionListForSurvey(surveyId: Int): List<SectionUiModel> {
        return sectionEntityDao.getAllSectionForSurveyInLanguage(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            surveyId = surveyId,
            languageCode = coreSharedPrefs.getSelectedLanguageCode()
        )
    }

    override suspend fun getSectionStatusForTask(
        missionId: Int,
        surveyId: Int,
        taskId: Int,
    ): List<SectionStatusEntity> {
        return sectionStatusEntityDao.getStatusForTask(
            missionId = missionId,
            surveyId,
            taskId,
            coreSharedPrefs.getUniqueUserIdentifier()
        ).value()
    }

    override suspend fun getSurveyEntity(surveyId: Int): SurveyEntity? {
        return surveyEntityDao.getSurveyDetailForLanguage(
            coreSharedPrefs.getUniqueUserIdentifier(),
            surveyId
        )
    }

}