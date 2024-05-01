package com.nrlm.baselinesurvey.ui.section_screen.domain.repository

import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.database.dao.ActivityTaskDao
import com.nrlm.baselinesurvey.database.dao.ContentDao
import com.nrlm.baselinesurvey.database.dao.DidiSectionProgressEntityDao
import com.nrlm.baselinesurvey.database.dao.OptionItemDao
import com.nrlm.baselinesurvey.database.dao.QuestionEntityDao
import com.nrlm.baselinesurvey.database.dao.SectionEntityDao
import com.nrlm.baselinesurvey.database.dao.SurveyEntityDao
import com.nrlm.baselinesurvey.database.dao.SurveyeeEntityDao
import com.nrlm.baselinesurvey.database.entity.ActivityTaskEntity
import com.nrlm.baselinesurvey.database.entity.ContentEntity
import com.nrlm.baselinesurvey.database.entity.DidiSectionProgressEntity
import com.nrlm.baselinesurvey.database.entity.OptionItemEntity
import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import com.nrlm.baselinesurvey.model.datamodel.SectionListItem
import com.nrlm.baselinesurvey.network.interfaces.BaseLineApiService
import com.nrlm.baselinesurvey.utils.states.SectionStatus
import com.nrlm.baselinesurvey.utils.states.SurveyState

class SectionListScreenRepositoryImpl(
    private val prefRepo: PrefRepo,
    private val baseLineApiService: BaseLineApiService,
    private val surveyEntityDao: SurveyEntityDao,
    private val sectionEntityDao: SectionEntityDao,
    private val questionEntityDao: QuestionEntityDao,
    private val didiSectionProgressEntityDao: DidiSectionProgressEntityDao,
    private val optionItemDao: OptionItemDao,
    private val surveyeeEntityDao: SurveyeeEntityDao,
    private val contentDao: ContentDao,
    private val taskDao: ActivityTaskDao
): SectionListScreenRepository {
    override fun getSectionsListForDidi(
        didiId: Int,
        surveyId: Int,
        languageId: Int
    ): List<SectionListItem> {
        val survey = surveyEntityDao.getSurveyDetailForLanguage(
            userId = getBaseLineUserId(),
            surveyId,
            languageId
        )
        val sectionEntityList =
            sectionEntityDao.getAllSectionForSurveyInLanguage(
                userId = getBaseLineUserId(),
                survey?.surveyId ?: 0,
                languageId
            )
        val sectionList = mutableListOf<SectionListItem>()
        sectionEntityList.forEach { sectionEntity ->
            val questionList = questionEntityDao.getSurveySectionQuestionForLanguage(
                userId = getBaseLineUserId(),
                sectionEntity.sectionId,
                survey?.surveyId ?: 0,
                languageId
            )
            val optionItemList = optionItemDao.getSurveySectionQuestionOptionsForLanguage(
                userId = getBaseLineUserId(),
                sectionEntity.sectionId,
                survey?.surveyId ?: 0,
                languageId
            )
            val contents = mutableListOf<ContentEntity>()
            for (content in sectionEntity.contentEntities) {
                val contentEntity =
                    content.contentKey?.let { contentDao.getContentFromIds(it, languageId) }
                if (contentEntity != null) {
                    contents.add(contentEntity)
                }
            }
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
                    contentData = contents,
                    languageId = languageId,
                    questionList = questionList,
                    optionsItemMap = questionOptionMap,
                    questionSize = sectionEntity.questionSize,
                    questionContentMapping = mutableMapOf()
                )
            )
            /*val sectionProgressForDidiLocal =
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
            }*/
        }
        return sectionList
    }

    override fun getSectionListForSurvey(surveyId: Int, languageId: Int): List<SectionListItem> {
        val survey = surveyEntityDao.getSurveyDetailForLanguage(
            userId = getBaseLineUserId(),
            surveyId,
            languageId
        )
        val sectionEntityList =
            sectionEntityDao.getAllSectionForSurveyInLanguage(
                userId = getBaseLineUserId(),
                survey?.surveyId ?: 0,
                languageId
            )
        val sectionList = mutableListOf<SectionListItem>()
        sectionEntityList.forEach { sectionEntity ->
            val questionList = questionEntityDao.getSurveySectionQuestionForLanguage(
                userId = getBaseLineUserId(),
                sectionEntity.sectionId,
                survey?.surveyId ?: 0,
                languageId
            )
            val optionItemList = optionItemDao.getSurveySectionQuestionOptionsForLanguage(
                userId = getBaseLineUserId(),
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
                    languageId = languageId,
                    questionList = questionList,
                    optionsItemMap = questionOptionMap,
                    questionSize = sectionEntity.questionSize
                )
            )
            val sectionProgressForDidiLocal =
                didiSectionProgressEntityDao.getSectionProgressForDidi(
                    userId = getBaseLineUserId(),
                    survey?.surveyId ?: 0,
                    sectionEntity.sectionId,
                    0
                )
            if (sectionProgressForDidiLocal == null) {
                didiSectionProgressEntityDao.addDidiSectionProgress(
                    DidiSectionProgressEntity(
                        id = 0,
                        userId = getBaseLineUserId(),
                        sectionEntity.surveyId,
                        sectionEntity.sectionId,
                        0,
                        sectionStatus = SectionStatus.INPROGRESS.ordinal
                    )
                )
            }
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
        val survey = surveyEntityDao.getSurveyDetailForLanguage(
            userId = getBaseLineUserId(),
            surveyId,
            languageId
        )
        return didiSectionProgressEntityDao.getAllSectionProgressForDidi(
            userId = getBaseLineUserId(),
            survey?.surveyId ?: 0,
            didiId
        )
    }

    override fun getSurveyeDetails(didiId: Int): SurveyeeEntity {
        return surveyeeEntityDao.getDidi(didiId)
    }

    override suspend fun updateSubjectStatus(didiId: Int, surveyState: SurveyState) {
        surveyeeEntityDao.updateDidiSurveyStatus(
            didiSurveyStatus = surveyState.ordinal,
            didiId = didiId
        )
    }

    override suspend fun updateTaskStatus(didiId: Int, surveyState: SectionStatus) {
        taskDao.updateTaskStatus(getBaseLineUserId(), didiId, surveyState.ordinal)
    }

    override suspend fun updateTaskStatus(
        taskId: Int,
        activityId: Int,
        missionId: Int,
        status: String
    ) {
        taskDao.updateTaskStatus(
            userId = getBaseLineUserId(),
            taskId,
            activityId,
            missionId,
            status
        )
    }

    override suspend fun getTaskForSubjectId(surveyId: Int): ActivityTaskEntity? {
        return taskDao.getTaskFromSubjectId(userId = getBaseLineUserId(), surveyId)
    }

    override fun getBaseLineUserId(): String {
        return prefRepo.getUniqueUserIdentifier()
    }
}