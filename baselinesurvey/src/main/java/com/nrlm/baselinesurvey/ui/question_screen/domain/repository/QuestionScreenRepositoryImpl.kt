package com.nrlm.baselinesurvey.ui.question_screen.domain.repository

import android.util.Log
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.PREF_KEY_TYPE_NAME
import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.database.dao.DidiSectionProgressEntityDao
import com.nrlm.baselinesurvey.database.dao.FormQuestionResponseDao
import com.nrlm.baselinesurvey.database.dao.OptionItemDao
import com.nrlm.baselinesurvey.database.dao.QuestionEntityDao
import com.nrlm.baselinesurvey.database.dao.SectionAnswerEntityDao
import com.nrlm.baselinesurvey.database.dao.SectionEntityDao
import com.nrlm.baselinesurvey.database.dao.SurveyEntityDao
import com.nrlm.baselinesurvey.database.dao.SurveyeeEntityDao
import com.nrlm.baselinesurvey.database.entity.DidiSectionProgressEntity
import com.nrlm.baselinesurvey.database.entity.OptionItemEntity
import com.nrlm.baselinesurvey.database.entity.SectionAnswerEntity
import com.nrlm.baselinesurvey.database.entity.SectionEntity
import com.nrlm.baselinesurvey.model.datamodel.SectionListItem
import com.nrlm.baselinesurvey.model.request.AnswerDetailDTOList
import com.nrlm.baselinesurvey.model.request.Options
import com.nrlm.baselinesurvey.model.request.SaveSurveyRequestModel
import com.nrlm.baselinesurvey.model.request.SectionList
import com.nrlm.baselinesurvey.network.interfaces.ApiService
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.nrlm.baselinesurvey.utils.json
import com.nrlm.baselinesurvey.utils.states.SectionStatus
import javax.inject.Inject

class QuestionScreenRepositoryImpl @Inject constructor(
    private val prefRepo: PrefRepo,
    private val apiService: ApiService,
    private val surveyeeEntityDao: SurveyeeEntityDao,
    private val surveyEntityDao: SurveyEntityDao,
    private val sectionEntityDao: SectionEntityDao,
    private val questionEntityDao: QuestionEntityDao,
    private val didiSectionProgressEntityDao: DidiSectionProgressEntityDao,
    private val sectionAnswerEntityDao: SectionAnswerEntityDao,
    private val optionItemDao: OptionItemDao,
    private val formQuestionResponseDao: FormQuestionResponseDao
): QuestionScreenRepository {

    override suspend fun getSections(
        sectionId: Int,
        surveyId: Int,
        languageId: Int
    ): SectionListItem {
        val survey = surveyEntityDao.getSurveyDetailForLanguage(surveyId, languageId)
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
        val optionList = optionItemDao.getSurveySectionQuestionOptionForLanguage(
            sectionEntity.sectionId,
            survey?.surveyId ?: 0,
            languageId
        )
        val questionOptionMap = mutableMapOf<Int, List<OptionItemEntity>>()
        if (questionList.isNotEmpty()) {
            for (question in questionList) {
                val options = optionList.filter { it.questionId == question.questionId }
                if (!questionOptionMap.containsKey(question.questionId)) {
                    questionOptionMap[question.questionId!!] = options
                }
            }
        }

        return SectionListItem(
            sectionId = sectionEntity.sectionId,
            surveyId = survey?.surveyId ?: 0,
            sectionName = sectionEntity.sectionName,
            sectionIcon = sectionEntity.sectionIcon,
            sectionDetails = sectionEntity.sectionDetails,
            sectionOrder = sectionEntity.sectionOrder,
            contentList = emptyList(),
            languageId = languageId,
            questionList = questionList,
            optionsItemMap = questionOptionMap
        )
    }

    override fun getSelectedLanguage(): Int {
        return prefRepo.getAppLanguageId() ?: 2
    }

    override suspend fun updateSectionProgress(
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
        updateDidiSurveyStatus(surveyId = surveyId, didiId = didiId)
    }

    override fun saveSectionAnswerForDidi(
        didiId: Int,
        sectionId: Int,
        questionId: Int,
        surveyId: Int,
        optionItems: List<OptionItemEntity>,
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
        optionItems: List<OptionItemEntity>,
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

    private suspend fun getOptionsFromOptionsItems(answer: SectionAnswerEntity): List<Options> {
//        for (option in answer.optionItems)
//            updateOptionItem(answer.surveyId, answer.sectionId, answer.questionId, option)
        return Options.getOptionsFromOptionsItems(answer.optionItems)

    }

    override suspend fun saveSectionAnswersToServer(didiId: Int, surveyId: Int) {
        val saveSurveyRequestModel = mutableListOf<SaveSurveyRequestModel>()
        val villageId = surveyeeEntityDao.getVillageIdForDidi(didiId)
        val questionEntityList =
            questionEntityDao.getAllQuestionsForLanguage(surveyId, prefRepo.getAppLanguageId() ?: 2)
        val localSectionAnswersList = sectionAnswerEntityDao.getAllAnswerForDidi(didiId)
        val answerListDto = mutableListOf<AnswerDetailDTOList>()

        localSectionAnswersList.forEach { sectionAnswerEntity ->
            val mAnswerListDtoItem = AnswerDetailDTOList(
                questionId = sectionAnswerEntity.questionId ?: 0,
                questionName = questionEntityList[questionEntityList.map { it.questionId }.indexOf(sectionAnswerEntity.questionId)].questionDisplay ?: BLANK_STRING,
                questionType = sectionAnswerEntity.questionType ?: BLANK_STRING,
                questionSummary = sectionAnswerEntity.questionSummary ?: BLANK_STRING,
                section = sectionEntityDao.getSurveySectionForLanguage(sectionId = sectionAnswerEntity.sectionId, surveyId = surveyId, languageId = prefRepo.getAppLanguageId() ?: 2).sectionName,
                options = getOptionsFromOptionsItems(sectionAnswerEntity)
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
                    sectionStatus = it.sectionStatus
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

        // TODO @Anupam Uncomment this after checking with Backend whi API is not working.
        BaselineLogger.d(
            "QuestionScreenRepositoryImpl",
            "saveSectionAnswersToServer: saveAnswersToServerApiRequest -> ${saveSurveyRequestModel.json()}"
        )
        // val saveAnswersToServerApiResponse = apiService.saveAnswersToServer(saveSurveyRequestModel)
        //  BaselineLogger.d("QuestionScreenRepositoryImpl", "saveSectionAnswersToServer: saveAnswersToServerApiResponse -> ${saveAnswersToServerApiResponse.json()}")
    }

    override suspend fun updateDidiSurveyStatus(didiId: Int, surveyId: Int) {
        val surveyStatusForDidiFromDb =  didiSectionProgressEntityDao.getAllSectionProgressForDidi(surveyId = surveyId, didiId = didiId)
        val notStartedOrInProgressList = surveyStatusForDidiFromDb.filter {
            it.sectionStatus.equals(SectionStatus.INPROGRESS.ordinal) || it.sectionStatus.equals(SectionStatus.NOT_STARTED.ordinal) }
        if (notStartedOrInProgressList.isNotEmpty())
            surveyeeEntityDao.updateDidiSurveyStatus(SectionStatus.INPROGRESS.ordinal, didiId)
        else {
            surveyeeEntityDao.updateDidiSurveyStatus(SectionStatus.COMPLETED.ordinal, didiId)
            surveyeeEntityDao.moveSurveyeeToThisWeek(didiId = didiId, moveDidisToNextWeek = false)
        }
    }

    override suspend fun getSectionsList(surveyId: Int, languageId: Int): List<SectionEntity> {
        return sectionEntityDao.getAllSectionForSurveyInLanguage(surveyId, languageId)
    }

/*override suspend fun updateOptionItem(
    surveyId: Int,
    sectionId: Int,
    questionId: Int,
    optionItem: OptionItemEntity,

) {
    formQuestionResponseDao.updateOptionItemValue(
        surveyId,
        sectionId,
        questionId,
        optionItem.optionId ?: 0,
        optionItem.isSelected ?: false
    )
}

override suspend fun updateOptionItemValue(
    surveyId: Int,
    sectionId: Int,
    questionId: Int,
    optionId: Int,
    selectedValue: String
) {
    return optionItemDao.updateOptionItemValue(
        surveyId = surveyId,
        sectionId = sectionId,
        questionId = questionId,
        optionId = optionId,
        selectValue = selectedValue
    )
}*/
}