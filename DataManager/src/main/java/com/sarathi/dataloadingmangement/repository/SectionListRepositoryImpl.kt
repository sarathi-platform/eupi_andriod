package com.sarathi.dataloadingmangement.repository

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.SectionEntityDao
import com.sarathi.dataloadingmangement.model.uiModel.SectionUiModel
import javax.inject.Inject

class SectionListRepositoryImpl @Inject constructor(
    val coreSharedPrefs: CoreSharedPrefs,
    val sectionEntityDao: SectionEntityDao
) : SectionListRepository {


    override fun getSectionListForSurvey(surveyId: Int): List<SectionUiModel> {
        return sectionEntityDao.getAllSectionForSurveyInLanguage(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            surveyId = surveyId,
            languageCode = coreSharedPrefs.getSelectedLanguageCode()
        )
    }

}