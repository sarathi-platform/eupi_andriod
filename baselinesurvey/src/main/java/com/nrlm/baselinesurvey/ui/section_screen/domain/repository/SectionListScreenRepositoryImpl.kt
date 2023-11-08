package com.nrlm.baselinesurvey.ui.section_screen.domain.repository

import android.util.Log
import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.database.dao.DidiSectionProgressEntityDao
import com.nrlm.baselinesurvey.database.dao.QuestionEntityDao
import com.nrlm.baselinesurvey.database.dao.SectionEntityDao
import com.nrlm.baselinesurvey.database.dao.SurveyEntityDao
import com.nrlm.baselinesurvey.database.entity.DidiSectionProgressEntity
import com.nrlm.baselinesurvey.model.datamodel.SectionListItem
import com.nrlm.baselinesurvey.network.interfaces.ApiService
import com.nrlm.baselinesurvey.utils.SectionStatus

class SectionListScreenRepositoryImpl(
    private val prefRepo: PrefRepo,
    private val apiService: ApiService,
    private val surveyEntityDao: SurveyEntityDao,
    private val sectionEntityDao: SectionEntityDao,
    private val questionEntityDao: QuestionEntityDao,
    private val didiSectionProgressEntityDao: DidiSectionProgressEntityDao
): SectionListScreenRepository {
    override fun getSectionsListForDidi(didiId: Int, languageId: Int): List<SectionListItem> {
        val survey = surveyEntityDao.getSurveyDetailForLanguage("BASE_LINE", languageId)
        val sectionEntityList = sectionEntityDao.getAllSectionForSurveyInLanguage(survey?.surveyId ?: 0, languageId)
        val sectionList = mutableListOf<SectionListItem>()
        sectionEntityList.forEach { sectionEntity ->
            val questionList = questionEntityDao.getSurveySectionQuestionForLanguage(sectionEntity.sectionId, survey?.surveyId ?: 0, languageId)
            sectionList.add(
                SectionListItem(
                    sectionId = sectionEntity.sectionId,
                    sectionName = sectionEntity.sectionName,
                    sectionIcon = sectionEntity.sectionIcon,
                    sectionDetails = sectionEntity.sectionDetails,
                    sectionOrder = sectionEntity.sectionOrder,
                    contentList = emptyList(),
                    languageId = languageId,
                    questionList = questionList
                )
            )
            val sectionProgressForDidiLocal =
                didiSectionProgressEntityDao.getSectionProgressForDidi(
                    survey?.surveyId ?: 0,
                    sectionEntity.sectionId,
                    didiId
                )
            if (sectionProgressForDidiLocal == null) {
                didiSectionProgressEntityDao.addDidiSectionProgress(
                    DidiSectionProgressEntity(
                        id = 0,
                        sectionEntity.surveyId,
                        sectionEntity.sectionId,
                        didiId,
                        sectionStatus = SectionStatus.INPROGRESS.ordinal
                    )
                )
            } else {
                didiSectionProgressEntityDao.updateSectionStatusForDidi(
                    sectionEntity.surveyId,
                    sectionEntity.sectionId,
                    didiId,
                    SectionStatus.INPROGRESS.ordinal
                )
            }
        }
        return sectionList
    }

    override fun getSelectedLanguage(): Int {
        return prefRepo.getAppLanguageId() ?: 2
    }

    override fun getSectionProgressForDidi(didiId: Int, languageId: Int): List<DidiSectionProgressEntity> {
        val survey = surveyEntityDao.getSurveyDetailForLanguage("BASE_LINE", languageId)
        return didiSectionProgressEntityDao.getAllSectionProgressForDidi(survey?.surveyId ?: 0, didiId)
    }
}