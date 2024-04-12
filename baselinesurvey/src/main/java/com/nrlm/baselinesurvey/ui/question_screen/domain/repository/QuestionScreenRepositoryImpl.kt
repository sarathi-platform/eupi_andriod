package com.nrlm.baselinesurvey.ui.question_screen.domain.repository

import android.util.Log
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.PREF_KEY_TYPE_NAME
import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.database.dao.ContentDao
import com.nrlm.baselinesurvey.database.dao.DidiSectionProgressEntityDao
import com.nrlm.baselinesurvey.database.dao.FormQuestionResponseDao
import com.nrlm.baselinesurvey.database.dao.InputTypeQuestionAnswerDao
import com.nrlm.baselinesurvey.database.dao.OptionItemDao
import com.nrlm.baselinesurvey.database.dao.QuestionEntityDao
import com.nrlm.baselinesurvey.database.dao.SectionAnswerEntityDao
import com.nrlm.baselinesurvey.database.dao.SectionEntityDao
import com.nrlm.baselinesurvey.database.dao.SurveyEntityDao
import com.nrlm.baselinesurvey.database.dao.SurveyeeEntityDao
import com.nrlm.baselinesurvey.database.entity.ContentEntity
import com.nrlm.baselinesurvey.database.entity.DidiSectionProgressEntity
import com.nrlm.baselinesurvey.database.entity.InputTypeQuestionAnswerEntity
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
    private val formQuestionResponseDao: FormQuestionResponseDao,
    private val inputTypeQuestionAnswerDao: InputTypeQuestionAnswerDao,
    private val contentDao: ContentDao
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
        val contents = mutableListOf<ContentEntity>()
        for (content in sectionEntity.contentEntities) {
            val contentEntity =
                content.contentKey?.let { contentDao.getContentFromIds(it, languageId) }
            if (contentEntity != null) {
                contents.add(contentEntity)
            }
        }
        val questionOptionMap = mutableMapOf<Int, List<OptionItemEntity>>()
        val questionContentMap = mutableMapOf<Int, List<ContentEntity>>()
        if (questionList.isNotEmpty()) {
            for (question in questionList) {
                val options = optionList.filter { it.questionId == question.questionId }
                if (!questionOptionMap.containsKey(question.questionId)) {
                    questionOptionMap[question.questionId!!] = options
                }
            }
            for (question in questionList) {
                for (content in question.contentEntities) {
                    val contents = mutableListOf<ContentEntity>()
                    val contentEntity =
                        content.contentKey?.let { contentDao.getContentFromIds(it, languageId) }
                    if (contentEntity != null) {
                        contents.add(contentEntity)
                        question.questionId?.let { questionContentMap.put(it, contents) }
                    }
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
            questionContentMapping = questionContentMap,
            contentData = contents,
            languageId = languageId,
            questionList = questionList,
            optionsItemMap = questionOptionMap,
            questionSize = sectionEntity.questionSize
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
        surveyId: Int,
        optionItems: List<OptionItemEntity>,
        questionType: String,
        questionSummary: String
    ) {
        sectionAnswerEntityDao.updateAnswer(
            didiId = didiId,
            sectionId = sectionId,
            questionId = questionId,
            surveyId = surveyId,
            optionItems = optionItems,
            questionType = questionType,
            questionSummary = questionSummary
        )
    }

    override fun isQuestionAlreadyAnswered(
        didiId: Int,
        questionId: Int,
        sectionId: Int,
        surveyId: Int
    ): Int {
        return sectionAnswerEntityDao.isQuestionAlreadyAnswered(
            didiId = didiId,
            questionId = questionId,
            sectionId = sectionId,
            surveyId = surveyId
        )
    }

    override fun isInputTypeQuestionAlreadyAnswered(
        surveyId: Int,
        sectionId: Int,
        didiId: Int,
        questionId: Int,
        optionItemId: Int
    ): Int {
        return inputTypeQuestionAnswerDao.isQuestionAlreadyAnswered(surveyId,
            sectionId,
            didiId,
            questionId,
            optionItemId)
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
//            surveyeeEntityDao.updateDidiSurveyStatus(SectionStatus.COMPLETED.ordinal, didiId)
            surveyeeEntityDao.moveSurveyeeToThisWeek(didiId = didiId, moveDidisToNextWeek = false)
        }
    }

    override suspend fun getSectionsList(surveyId: Int, languageId: Int): List<SectionEntity> {
        return sectionEntityDao.getAllSectionForSurveyInLanguage(surveyId, languageId)
    }

    override suspend fun updateInputTypeQuestionAnswer(
        surveyId: Int,
        sectionId: Int,
        questionId: Int,
        didiId: Int,
        optionId: Int,
        inputValue: String
    ) {
        inputTypeQuestionAnswerDao.updateInputTypeAnswersForQuestion(
            surveyId = surveyId,
            sectionId = sectionId,
            didiId = didiId,
            questionId = questionId,
            optionId = optionId,
            inputValue = inputValue
        )
    }

    override suspend fun saveInputTypeQuestionAnswer(
        surveyId: Int,
        sectionId: Int,
        questionId: Int,
        didiId: Int,
        optionId: Int,
        inputValue: String
    ) {
        val inputTypeQuestionAnswerEntity = InputTypeQuestionAnswerEntity(
            surveyId = surveyId,
            sectionId = sectionId,
            questionId = questionId,
            didiId = didiId,
            optionId = optionId,
            inputValue = inputValue
        )
        inputTypeQuestionAnswerDao.saveInputTypeAnswersForQuestion(inputTypeQuestionAnswerEntity)
    }

    override suspend fun getAllInputTypeQuestionAnswersForDidi(
        surveyId: Int,
        sectionId: Int,
        didiId: Int
    ): List<InputTypeQuestionAnswerEntity> {
        return inputTypeQuestionAnswerDao.getInputTypeAnswersForQuestionForDidi(
            surveyId = surveyId,
            sectionId = sectionId,
            didiId = didiId
        )
    }

    override suspend fun deleteInputTypeQuestion(
        surveyId: Int,
        sectionId: Int,
        questionId: Int,
        didiId: Int,
        optionId: Int
    ) {
        inputTypeQuestionAnswerDao.deleteInputTypeQuestion(
            surveyId,
            sectionId,
            questionId,
            didiId,
            optionId
        )
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