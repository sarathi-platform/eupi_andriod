package com.nrlm.baselinesurvey.ui.section_screen.domain.repository

import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.database.dao.QuestionEntityDao
import com.nrlm.baselinesurvey.database.dao.SectionEntityDao
import com.nrlm.baselinesurvey.database.dao.SurveyEntityDao
import com.nrlm.baselinesurvey.model.datamodel.SectionListItem
import com.nrlm.baselinesurvey.network.interfaces.ApiService

class SectionListScreenRepositoryImpl(
    private val prefRepo: PrefRepo,
    private val apiService: ApiService,
    private val surveyEntityDao: SurveyEntityDao,
    private val sectionEntityDao: SectionEntityDao,
    private val questionEntityDao: QuestionEntityDao
): SectionListScreenRepository {
    override fun getSectionsList(lanuageId: Int): List<SectionListItem> {
        val survey = surveyEntityDao.getSurveyDetailForLanguage("BASE_LINE", lanuageId)
        val sectionEntityList = sectionEntityDao.getAllSectionForSurveyInLanguage(survey?.surveyId ?: 0, lanuageId)
        val sectionList = mutableListOf<SectionListItem>()
        sectionEntityList.forEach { sectionEntity ->
            val questionList = questionEntityDao.getSurveySectionQuestionForLanguage(sectionEntity.sectionId, survey?.surveyId ?: 0, lanuageId)
            sectionList.add(
                SectionListItem(
                    sectionId = sectionEntity.sectionId,
                    sectionName = sectionEntity.sectionName,
                    sectionIcon = sectionEntity.sectionIcon,
                    sectionDetails = sectionEntity.sectionDetails,
                    sectionOrder = sectionEntity.sectionOrder,
                    contentList = emptyList(),
                    languageId = lanuageId,
                    questionList = questionList
                )
            )
        }

        return sectionList
//        return secondSampleList
    }

    override fun getSelectedLanguage(): Int {
        return prefRepo.getAppLanguageId() ?: 2
    }
}