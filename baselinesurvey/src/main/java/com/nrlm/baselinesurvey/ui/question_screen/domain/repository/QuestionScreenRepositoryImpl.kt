package com.nrlm.baselinesurvey.ui.question_screen.domain.repository

import android.util.Log
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.PREF_KEY_TYPE_NAME
import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.database.dao.DidiSectionProgressEntityDao
import com.nrlm.baselinesurvey.database.dao.QuestionEntityDao
import com.nrlm.baselinesurvey.database.dao.SectionAnswerEntityDao
import com.nrlm.baselinesurvey.database.dao.SectionEntityDao
import com.nrlm.baselinesurvey.database.dao.SurveyEntityDao
import com.nrlm.baselinesurvey.database.dao.SurveyeeEntityDao
import com.nrlm.baselinesurvey.database.entity.DidiSectionProgressEntity
import com.nrlm.baselinesurvey.database.entity.SectionAnswerEntity
import com.nrlm.baselinesurvey.model.datamodel.OptionsItem
import com.nrlm.baselinesurvey.model.datamodel.SectionListItem
import com.nrlm.baselinesurvey.model.datamodel.Sections
import com.nrlm.baselinesurvey.model.request.AnswerDetailDTOList
import com.nrlm.baselinesurvey.model.request.Options
import com.nrlm.baselinesurvey.model.request.SaveSurveyRequestModel
import com.nrlm.baselinesurvey.model.request.SectionList
import com.nrlm.baselinesurvey.network.interfaces.ApiService
import com.nrlm.baselinesurvey.utils.SectionStatus
import com.nrlm.baselinesurvey.utils.findItemBySectionId
import com.nrlm.baselinesurvey.utils.firstSampleList
import javax.inject.Inject

class QuestionScreenRepositoryImpl @Inject constructor(
    private val prefRepo: PrefRepo,
    private val apiService: ApiService,
    private val surveyeeEntityDao: SurveyeeEntityDao,
    private val surveyEntityDao: SurveyEntityDao,
    private val sectionEntityDao: SectionEntityDao,
    private val questionEntityDao: QuestionEntityDao,
    private val didiSectionProgressEntityDao: DidiSectionProgressEntityDao,
    private val sectionAnswerEntityDao: SectionAnswerEntityDao
): QuestionScreenRepository {

    override suspend fun getSections(sectionId: Int, languageId: Int): SectionListItem {
        val survey = surveyEntityDao.getSurveyDetailForLanguage("BASE_LINE", languageId)
        val sectionEntity = sectionEntityDao.getSurveySectionForLanguage(
            sectionId,
            survey?.surveyId ?: 0,
            languageId
        )
        val questionList = questionEntityDao.getSurveySectionQuestionForLanguage(
            sectionEntity.sectionId,
            survey?.surveyId ?: 0,
            languageId
        )

        return SectionListItem(
            sectionId = sectionEntity.sectionId,
            surveyId = survey?.surveyId ?: 0,
            sectionName = sectionEntity.sectionName,
            sectionIcon = sectionEntity.sectionIcon,
            sectionDetails = sectionEntity.sectionDetails,
            sectionOrder = sectionEntity.sectionOrder,
            contentList = emptyList(),
            languageId = languageId,
            questionList = questionList
        )
    }

    override fun getSelectedLanguage(): Int {
        return prefRepo.getAppLanguageId() ?: 2
    }

    override fun updateSectionProgress(
        surveyId: Int,
        sectionId: Int,
        didiId: Int,
        sectionStatus: SectionStatus
    ) {
        val sectionProgressForDidiLocal = didiSectionProgressEntityDao.getSectionProgressForDidi(surveyId, sectionId, didiId)
        Log.d("QuestionScreenRepositoryImpl", "updateSectionProgress: $sectionProgressForDidiLocal")
        if (sectionProgressForDidiLocal == null) {
            didiSectionProgressEntityDao.addDidiSectionProgress(
                DidiSectionProgressEntity(
                    id = 0,
                    surveyId,
                    sectionId,
                    didiId,
                    sectionStatus = sectionStatus.ordinal
                )
            )
        } else {
            didiSectionProgressEntityDao.updateSectionStatusForDidi(surveyId, sectionId, didiId, sectionStatus.ordinal)
        }
    }

    override fun saveSectionAnswerForDidi(
        didiId: Int,
        sectionId: Int,
        questionId: Int,
        surveyId: Int,
        optionItems: List<OptionsItem>,
        questionType: String,
        questionSummary: String
    ) {
        val villageIdForSurveyee = surveyeeEntityDao.getVillageIdForDidi(didiId)
        val sectionAnswerEntity = SectionAnswerEntity(
            id = 0,
            didiId = didiId,
            sectionId = sectionId,
            surveyId = surveyId,
            questionId = questionId,
            villageId = villageIdForSurveyee,
            optionItems = optionItems,
            questionType = questionType,
            questionSummary = questionSummary
        )
        sectionAnswerEntityDao.insertAnswer(sectionAnswerEntity)
    }

    override fun updateSectionAnswerForDidi(
        didiId: Int,
        sectionId: Int,
        questionId: Int,
        optionItems: List<OptionsItem>,
        questionType: String,
        questionSummary: String
    ) {
        sectionAnswerEntityDao.updateAnswer(
            didiId, sectionId, questionId, optionItems, questionType, questionSummary
        )
    }

    override fun isQuestionAlreadyAnswered(didiId: Int, questionId: Int, sectionId: Int): Int {
        return sectionAnswerEntityDao.isQuestionAlreadyAnswered(didiId, questionId, sectionId)
    }

    override fun getAllAnswersForDidi(didiId: Int): List<SectionAnswerEntity> {
        return sectionAnswerEntityDao.getAllAnswerForDidi(didiId)
    }

    override fun getSectionAnswerForDidi(sectionId: Int, didiId: Int): List<SectionAnswerEntity> {
       return sectionAnswerEntityDao.getSectionAnswerForDidi(sectionId, didiId)
    }

    override suspend fun saveSectionAnswersToServer(didiId: Int, surveyId: Int) {
        val saveSurveyRequestModel = mutableListOf<SaveSurveyRequestModel>()
        val villageId = surveyeeEntityDao.getVillageIdForDidi(didiId)
        val questionEntityList = questionEntityDao.getAllQuestionsForLanguage(surveyId, prefRepo.getAppLanguageId() ?: 2)
        val localSectionAnswersList = sectionAnswerEntityDao.getAllAnswerForDidi(didiId)
        val answerListDto = mutableListOf<AnswerDetailDTOList>()

        localSectionAnswersList.forEach { sectionAnswerEntity ->
            val mAnswerListDtoItem = AnswerDetailDTOList(
                questionId = sectionAnswerEntity.questionId ?: 0,
                questionName = questionEntityList[questionEntityList.map { it.questionId }.indexOf(sectionAnswerEntity.questionId)].questionDisplay ?: BLANK_STRING,
                questionType = sectionAnswerEntity.questionType ?: BLANK_STRING,
                questionSummary = sectionAnswerEntity.questionSummary ?: BLANK_STRING,
                section = sectionAnswerEntity.sectionId,
                options = Options.getOptionsFromOptionsItems(sectionAnswerEntity.optionItems)
            )
            answerListDto.add(mAnswerListDtoItem)
        }
        val didiSectionProgressEntityList = didiSectionProgressEntityDao.getAllSectionProgressForDidi(surveyId, didiId)
        val sectionList = mutableListOf<SectionList>()
        didiSectionProgressEntityList.forEach {
            sectionList.add(
                SectionList(
                    sectionId = it.sectionId,
                    sectionName = sectionEntityDao.getSurveySectionForLanguage(sectionId = it.sectionId, surveyId = surveyId, languageId = prefRepo.getAppLanguageId() ?: 2).sectionName,
                    sectionStatus = SectionStatus.getSectionStatusFromOrdinal(it.sectionStatus).name
                )
            )
        }
        val saveSurveyRequestModelItem = SaveSurveyRequestModel(
            beneficiaryId = didiId,
            languageId = prefRepo.getAppLanguageId() ?: 2,
            baselineSurveyStatus = SectionStatus.INPROGRESS.ordinal,
            shgFlag = 1,
            stateId = 4,
            surveyId = surveyId,
            userType = prefRepo.getPref(PREF_KEY_TYPE_NAME, BLANK_STRING) ?: BLANK_STRING,
            villageId = villageId,
            answerDetailDTOList = answerListDto,
            sectionList = sectionList
        )
        saveSurveyRequestModel.add(saveSurveyRequestModelItem)
        val saveAnswersToServerApiResponse = apiService.saveAnswersToServer(saveSurveyRequestModel)
        Log.d("QuestionScreenRepositoryImpl", "saveSectionAnswersToServer: saveAnswersToServerApiResponse -> $saveAnswersToServerApiResponse")
    }
}