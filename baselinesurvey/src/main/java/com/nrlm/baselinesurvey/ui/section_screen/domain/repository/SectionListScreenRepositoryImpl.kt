package com.nrlm.baselinesurvey.ui.section_screen.domain.repository

import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.database.dao.DidiSectionProgressEntityDao
import com.nrlm.baselinesurvey.database.dao.OptionItemDao
import com.nrlm.baselinesurvey.database.dao.QuestionEntityDao
import com.nrlm.baselinesurvey.database.dao.SectionEntityDao
import com.nrlm.baselinesurvey.database.dao.SurveyEntityDao
import com.nrlm.baselinesurvey.database.dao.SurveyeeEntityDao
import com.nrlm.baselinesurvey.database.entity.DidiSectionProgressEntity
import com.nrlm.baselinesurvey.database.entity.OptionItemEntity
import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import com.nrlm.baselinesurvey.model.datamodel.SectionListItem
import com.nrlm.baselinesurvey.network.interfaces.ApiService
import com.nrlm.baselinesurvey.utils.states.SectionStatus

class SectionListScreenRepositoryImpl(
    private val prefRepo: PrefRepo,
    private val apiService: ApiService,
    private val surveyEntityDao: SurveyEntityDao,
    private val sectionEntityDao: SectionEntityDao,
    private val questionEntityDao: QuestionEntityDao,
    private val didiSectionProgressEntityDao: DidiSectionProgressEntityDao,
    private val optionItemDao: OptionItemDao,
    private val surveyeeEntityDao: SurveyeeEntityDao,
): SectionListScreenRepository {
    override fun getSectionsListForDidi(
        didiId: Int,
        surveyId: Int,
        languageId: Int
    ): List<SectionListItem> {
        val survey = surveyEntityDao.getSurveyDetailForLanguage(surveyId, languageId)
        val sectionEntityList =
            sectionEntityDao.getAllSectionForSurveyInLanguage(survey?.surveyId ?: 0, languageId)
        val sectionList = mutableListOf<SectionListItem>()
        sectionEntityList.forEach { sectionEntity ->
            val questionList = questionEntityDao.getSurveySectionQuestionForLanguage(
                sectionEntity.sectionId,
                survey?.surveyId ?: 0,
                languageId
            )
            val optionItemList = optionItemDao.getSurveySectionQuestionOptionForLanguage(
                sectionEntity.sectionId,
                survey?.surveyId ?: 0,
                languageId
            )

            val questionOptionMap = mutableMapOf<Int, List<OptionItemEntity>>()
            if (questionList.isNotEmpty()) {
                for (question in questionList) {
                    val options = optionItemList.filter { it.questionId == question.questionId }
                    if (!questionOptionMap.containsKey(question.questionId)) {
                        questionOptionMap[question.questionId!!] = options
                    }
                }
            }

            sectionList.add(
                SectionListItem(
                    sectionId = sectionEntity.sectionId,
                    sectionName = sectionEntity.sectionName,
                    surveyId = sectionEntity.surveyId,
                    sectionIcon = sectionEntity.sectionIcon,
                    sectionDetails = sectionEntity.sectionDetails,
                    sectionOrder = sectionEntity.sectionOrder,
                    contentList = emptyList(),
                    languageId = languageId,
                    questionList = questionList,
                    optionsItemMap = questionOptionMap,
                    questionSize = sectionEntity.questionSize
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
            } /*else {
                didiSectionProgressEntityDao.updateSectionStatusForDidi(
                    sectionEntity.surveyId,
                    sectionEntity.sectionId,
                    didiId,
                    SectionStatus.INPROGRESS.ordinal
                )
            }*/
        }
        return sectionList
    }

    override fun getSelectedLanguage(): Int {
        return prefRepo.getAppLanguageId() ?: 2
    }

    override fun getSectionProgressForDidi(
        didiId: Int,
        surveyId: Int,
        languageId: Int
    ): List<DidiSectionProgressEntity> {
        val survey = surveyEntityDao.getSurveyDetailForLanguage(surveyId, languageId)
        return didiSectionProgressEntityDao.getAllSectionProgressForDidi(
            survey?.surveyId ?: 0,
            didiId
        )
    }

    override fun getSurveyeDetails(didiId: Int): SurveyeeEntity {
        return surveyeeEntityDao.getDidi(didiId)
    }
}