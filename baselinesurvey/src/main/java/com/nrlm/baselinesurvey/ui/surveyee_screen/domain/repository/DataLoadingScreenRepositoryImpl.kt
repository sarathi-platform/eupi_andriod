package com.nrlm.baselinesurvey.ui.surveyee_screen.domain.repository

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.DEFAULT_ID
import com.nrlm.baselinesurvey.PREF_CASTE_LIST
import com.nrlm.baselinesurvey.PREF_KEY_EMAIL
import com.nrlm.baselinesurvey.PREF_KEY_IDENTITY_NUMBER
import com.nrlm.baselinesurvey.PREF_KEY_NAME
import com.nrlm.baselinesurvey.PREF_KEY_PROFILE_IMAGE
import com.nrlm.baselinesurvey.PREF_KEY_ROLE_NAME
import com.nrlm.baselinesurvey.PREF_KEY_TYPE_NAME
import com.nrlm.baselinesurvey.PREF_KEY_USER_NAME
import com.nrlm.baselinesurvey.PREF_MOBILE_NUMBER
import com.nrlm.baselinesurvey.PREF_STATE_ID
import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.database.NudgeBaselineDatabase
import com.nrlm.baselinesurvey.database.dao.ActivityTaskDao
import com.nrlm.baselinesurvey.database.dao.ContentDao
import com.nrlm.baselinesurvey.database.dao.LanguageListDao
import com.nrlm.baselinesurvey.database.dao.MissionActivityDao
import com.nrlm.baselinesurvey.database.dao.MissionEntityDao
import com.nrlm.baselinesurvey.database.dao.OptionItemDao
import com.nrlm.baselinesurvey.database.dao.QuestionEntityDao
import com.nrlm.baselinesurvey.database.dao.SectionEntityDao
import com.nrlm.baselinesurvey.database.dao.SurveyEntityDao
import com.nrlm.baselinesurvey.database.dao.SurveyeeEntityDao
import com.nrlm.baselinesurvey.database.entity.ActivityTaskEntity
import com.nrlm.baselinesurvey.database.entity.ContentEntity
import com.nrlm.baselinesurvey.database.entity.LanguageEntity
import com.nrlm.baselinesurvey.database.entity.MissionActivityEntity
import com.nrlm.baselinesurvey.database.entity.MissionEntity
import com.nrlm.baselinesurvey.database.entity.OptionItemEntity
import com.nrlm.baselinesurvey.database.entity.QuestionEntity
import com.nrlm.baselinesurvey.database.entity.SectionEntity
import com.nrlm.baselinesurvey.database.entity.SurveyEntity
import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import com.nrlm.baselinesurvey.model.datamodel.CasteModel
import com.nrlm.baselinesurvey.model.datamodel.OptionsItem
import com.nrlm.baselinesurvey.model.datamodel.QuestionList
import com.nrlm.baselinesurvey.model.datamodel.Sections
import com.nrlm.baselinesurvey.model.mappers.FormQuestionEntityMapper.getFormQuestionEntity
import com.nrlm.baselinesurvey.model.mappers.InputTypeQuestionAnswerEntityMapper
import com.nrlm.baselinesurvey.model.mappers.SectionAnswerEntityMapper.getSectionAnswerEntity
import com.nrlm.baselinesurvey.model.request.ContentMangerRequest
import com.nrlm.baselinesurvey.model.request.GetSurveyAnswerRequest
import com.nrlm.baselinesurvey.model.request.MissionRequest
import com.nrlm.baselinesurvey.model.request.SurveyRequestBodyModel
import com.nrlm.baselinesurvey.model.response.ApiResponseModel
import com.nrlm.baselinesurvey.model.response.BeneficiaryApiResponse
import com.nrlm.baselinesurvey.model.response.ContentList
import com.nrlm.baselinesurvey.model.response.ContentResponse
import com.nrlm.baselinesurvey.model.response.MissionResponseModel
import com.nrlm.baselinesurvey.model.response.QuestionAnswerResponseModel
import com.nrlm.baselinesurvey.model.response.SurveyResponseModel
import com.nrlm.baselinesurvey.model.response.UserDetailsResponse
import com.nrlm.baselinesurvey.network.interfaces.ApiService
import com.nrlm.baselinesurvey.ui.Constants.QuestionType
import com.nrlm.baselinesurvey.ui.Constants.ResultType
import com.nrlm.baselinesurvey.utils.BaselineLogger
import javax.inject.Inject

class DataLoadingScreenRepositoryImpl @Inject constructor(
    val prefRepo: PrefRepo,
    val apiService: ApiService,
    val languageListDao: LanguageListDao,
    val surveyeeEntityDao: SurveyeeEntityDao,
    val surveyEntityDao: SurveyEntityDao,
    val sectionEntityDao: SectionEntityDao,
    val questionEntityDao: QuestionEntityDao,
    val optionItemDao: OptionItemDao,
    val missionEntityDao: MissionEntityDao,
    val missionActivityDao: MissionActivityDao,
    val activityTaskDao: ActivityTaskDao,
    val contentDao: ContentDao,
    val baselineDatabase: NudgeBaselineDatabase
) : DataLoadingScreenRepository {
    override suspend fun fetchLocalLanguageList(): List<LanguageEntity> {
        return languageListDao.getAllLanguages()
    }

    override suspend fun fetchUseDetialsFromNetwork(userViewApiRequest: String): ApiResponseModel<UserDetailsResponse> {
        return apiService.userAndVillageListAPI(userViewApiRequest)
    }

    override suspend fun fetchSurveyeeListFromNetwork(userId: Int): ApiResponseModel<BeneficiaryApiResponse> {
        return apiService.getDidisFromNetwork(userId)
    }

    override suspend fun fetchSurveyFromNetwork(surveyRequestBodyModel: SurveyRequestBodyModel): ApiResponseModel<SurveyResponseModel> {
        return apiService.getSurveyFromNetwork(surveyRequestBodyModel)
    }

    override suspend fun saveSurveyToDb(
        surveyResponseModel: SurveyResponseModel,
        languageId: Int,
        surveyAnswerResponse: List<QuestionAnswerResponseModel>?
    ) {
        baselineDatabase.runInTransaction {
            surveyEntityDao.deleteSurveyFroLanguage(surveyResponseModel.surveyId, languageId)
            val surveyEntity = SurveyEntity(
                id = 0,
                surveyId = surveyResponseModel.surveyId,
                surveyName = surveyResponseModel.surveyName,
                surveyPassingMark = surveyResponseModel.surveyPassingMark,
                thresholdScore = surveyResponseModel.thresholdScore,
                languageId = languageId,
                referenceId = surveyResponseModel.referenceId
            )
            surveyEntityDao.insertSurvey(surveyEntity)
            surveyResponseModel.sections.forEach { section ->
                val subQuestionList = mutableListOf<QuestionList>()
                val subSubQuestionList = mutableListOf<QuestionList>()
                val contentLists = mutableListOf<ContentList>()
                sectionEntityDao.deleteSurveySectionFroLanguage(
                    section.sectionId,
                    surveyResponseModel.surveyId,
                    languageId
                )
                val sectionEntity = SectionEntity(
                    id = 0,
                    sectionId = section.sectionId,
                    surveyId = surveyResponseModel.surveyId,
                    sectionName = section.sectionName,
                    sectionOrder = section.sectionOrder,
                    sectionDetails = section.sectionDetails,
                    sectionIcon = section.sectionIcon,
                    languageId = languageId,
                    questionSize = section.questionList.size,
                    contentEntities = section.contentList
                )
                contentLists.addAll(section.contentList)
                sectionEntityDao.insertSection(sectionEntity)
                section.questionList.forEach { question ->
                    if (section.sectionId == 8) {
                        Log.d("invoke", "section.questionList.forEach -> ${question} \n\n\n")
                    }
                    saveQuestionAndOptionsToDb(
                        question = question,
                        section,
                        surveyResponseModel,
                        languageId
                    )
                    question?.options?.forEach { optionItem ->
                        if (optionItem?.conditions?.isNotEmpty()!!) {
                            Log.d(
                                "saveSurveyToDb",
                                "optionItem?.conditions?.isNotEmpty() -> ${optionItem.conditions}"
                            )
                            optionItem.conditions.forEach {
                                Log.d("saveSurveyToDb", "optionItem.conditions.forEach -> ${it}")
                                if (it?.resultType?.equals(ResultType.Options.name, true) == true) {
                                    it.resultList.forEach { ques ->
                                        if (ques.type?.equals(
                                                QuestionType.Form.name,
                                                true
                                            ) == true
                                        ) {
                                            ques.options?.forEach {
                                                it?.let { it1 ->
                                                    saveConditionalOptions(
                                                        it1,
                                                        question,
                                                        section,
                                                        surveyResponseModel,
                                                        languageId
                                                    )
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    subQuestionList.addAll(it?.resultList ?: emptyList())

                                }
                            }
                        }
                    }
                    contentLists.addAll(question?.contentList ?: listOf())
                }
                subQuestionList.forEach { conditionalItem ->
                    Log.d("saveSurveyToDb", "subQuestionList.forEach -> ${conditionalItem}")
                    saveQuestionAndOptionsToDb(
                        question = conditionalItem,
                        section,
                        surveyResponseModel,
                        languageId,
                        true
                    )
                    conditionalItem.options?.forEach { subQuestionOption ->
                        if (subQuestionOption?.conditions != null) {
                            subQuestionOption.conditions.forEach {
                                if (it?.resultType?.equals(ResultType.Questions.name, true) == true)
                                    subSubQuestionList.addAll(it.resultList)
                            }
                        }
                    }
                }
                subSubQuestionList.forEach { subConditionalItem ->
                    saveQuestionAndOptionsToDb(
                        question = subConditionalItem,
                        section,
                        surveyResponseModel,
                        languageId,
                        true
                    )
                }
            }
            saveSurveyAnswerToDb(questionAnswerResponseModels = surveyAnswerResponse)

        }

    }

    private fun saveQuestionAndOptionsToDb(question: QuestionList?, section: Sections, surveyResponseModel: SurveyResponseModel, languageId: Int, isSubQuestionList: Boolean = false) {
        try {
            if (question?.questionId != null) {
                val existingQuestion = questionEntityDao.getQuestionForSurveySectionForLanguage(question.questionId!!,
                    section.sectionId,
                    surveyResponseModel.surveyId,
                    languageId)
                if (existingQuestion != null) {
                    questionEntityDao.deleteSurveySectionQuestionFroLanguage(
                        question.questionId!!,
                        section.sectionId,
                        surveyResponseModel.surveyId,
                        languageId
                    )
                }
                val questionEntity = QuestionEntity(
                    id = 0,
                    questionId = question.questionId,
                    sectionId = section.sectionId,
                    surveyId = surveyResponseModel.surveyId,
                    questionDisplay = question.questionDisplay,
                    questionSummary = question.questionSummary,
                    gotoQuestionId = question.gotoQuestionId,
                    order = question.order,
                    type = question.type,
                    //  options = question.options,
                    languageId = languageId,
                    isConditional = isSubQuestionList,
                    tag = question.attributeTag ?: BLANK_STRING,
                    contentEntities = question.contentList
                )
                questionEntityDao.insertQuestion(questionEntity)
                question.options?.forEach { optionsItem ->
                    if (optionsItem != null) {
                        optionItemDao.deleteSurveySectionQuestionOptionFroLanguage(
                            optionsItem.optionId!!,
                            question.questionId!!,
                            section.sectionId,
                            surveyResponseModel.surveyId,
                            languageId
                        )
                        val optionItemEntity = OptionItemEntity(
                            id = 0,
                            optionId = optionsItem.optionId,
                            questionId = question.questionId,
                            sectionId = section.sectionId,
                            surveyId = surveyResponseModel.surveyId,
                            display = optionsItem.display,
                            weight = optionsItem.weight,
                            optionValue = optionsItem.optionValue,
                            summary = optionsItem.summary,
                            count = optionsItem.count,
                            optionImage = optionsItem.optionImage,
                            optionType = optionsItem.optionType,
                            conditional = optionsItem.conditional,
                            order = optionsItem.order,
                            values = optionsItem.values,
                            languageId = languageId,
                            conditions = optionsItem.conditions
                        )
                        optionItemDao.insertOption(optionItemEntity)

                        // TODO handle sub-options for question.
                        /*optionsItem.conditions?.forEach { conditionsDto ->
                            if (conditionsDto?.resultType?.equals(ResultType.Options.name) == true) {
                                conditionsDto.resultList.forEach {

                                }
                                val subOption = OptionItemEntity(
                                    id = 0,
                                    optionId = optionsItem.optionId,
                                    questionId = question.questionId,
                                    sectionId = section.sectionId,
                                    surveyId = surveyResponseModel.surveyId,
                                    display = optionsItem.display,
                                    weight = optionsItem.weight,
                                    optionValue = optionsItem.optionValue,
                                    summary = optionsItem.summary,
                                    count = optionsItem.count,
                                    optionImage = optionsItem.optionImage,
                                    optionType = optionsItem.optionType,
                                    questionList = optionsItem.questionList,
                                    conditional = optionsItem.conditional,
                                    order = optionsItem.order,
                                    values = optionsItem.values,
                                    languageId = languageId,
                                    conditions = optionsItem.conditions
                                )
                            }
                        }*/
                    }
                }
            }
        } catch (ex: Exception) {
            Log.e("DataLoadingScreenRepositoryImpl", "saveQuestionAndOptionsToDb: exception, question: $question", ex)
        }

    }

    private fun saveConditionalOptions(optionsItem: OptionsItem, question: QuestionList, section: Sections, surveyResponseModel: SurveyResponseModel, languageId: Int) {
        val isOptionExisting = optionItemDao.isOptionAlreadyPresent(questionId = question.questionId!!, sectionId = section.sectionId, surveyId = surveyResponseModel.surveyId)
        if (isOptionExisting == 0) {
            val optionItemEntity = OptionItemEntity(
                id = 0,
                optionId = optionsItem.optionId,
                questionId = question.questionId,
                sectionId = section.sectionId,
                surveyId = surveyResponseModel.surveyId,
                display = optionsItem.display,
                weight = optionsItem.weight,
                optionValue = optionsItem.optionValue,
                summary = optionsItem.summary,
                count = optionsItem.count,
                optionImage = optionsItem.optionImage,
                optionType = optionsItem.optionType,
                conditional = true,
                order = optionsItem.order,
                values = optionsItem.values,
                languageId = languageId,
                conditions = optionsItem.conditions
            )
            optionItemDao.insertOption(optionItemEntity)
        }
    }

    override fun saveUserDetails(userDetailsResponse: UserDetailsResponse) {
        BaselineLogger.d("User Details        ","Mobile Number: ${prefRepo.getPref(PREF_MOBILE_NUMBER,BLANK_STRING)}")
        BaselineLogger.d("User Details        ","User Email: ${userDetailsResponse.email}")
        prefRepo.savePref(PREF_KEY_USER_NAME, userDetailsResponse.username ?: "")
        prefRepo.savePref(PREF_KEY_NAME, userDetailsResponse.name ?: "")
        prefRepo.savePref(PREF_KEY_EMAIL, userDetailsResponse.email ?: "")
        prefRepo.savePref(PREF_KEY_IDENTITY_NUMBER, userDetailsResponse.identityNumber ?: "")
        prefRepo.savePref(PREF_KEY_PROFILE_IMAGE, userDetailsResponse.profileImage ?: "")
        prefRepo.savePref(PREF_KEY_ROLE_NAME, userDetailsResponse.roleName ?: "")
        prefRepo.savePref(PREF_KEY_TYPE_NAME, userDetailsResponse.typeName ?: "")
        prefRepo.savePref(PREF_STATE_ID, userDetailsResponse.referenceId.first().stateId ?: -1)
    }

    override fun getUserId(): Int {
        return prefRepo.getPref(PREF_KEY_USER_NAME, "")?.toInt() ?: 0
    }

    override fun deleteSurveyeeList() {
        surveyeeEntityDao.deleteSurveyees()
    }

    override fun saveSurveyeeList(surveyeeEntity: SurveyeeEntity) {
        surveyeeEntityDao.insertDidi(surveyeeEntity)
    }

    override suspend fun fetchSurveyeeListFromLocalDb(): List<SurveyeeEntity> {
        return surveyeeEntityDao.getAllDidis()
    }

    override suspend fun fetchSavedSurveyFromServer() {
        val savedSurveyAnswersRequest: List<Int> = emptyList()
//        surveyeeEntityDao
//        apiService.fetchSavedSurveyAnswersFromServer()
    }

    override suspend fun fetchMissionDataFromServer(
        languageCode: String,
        missionName: String
    ): ApiResponseModel<List<MissionResponseModel>> {
        val missionRequest = MissionRequest(languageCode, missionName)
        return apiService.getBaseLineMission(missionRequest)
    }

    override suspend fun saveMissionToDB(missions: MissionEntity) {
        missionEntityDao.insertMission(missions)
    }

    override suspend fun saveMissionsActivityToDB(activities: MissionActivityEntity) {
        missionActivityDao.insertMissionActivity(activities)
    }

    override suspend fun saveActivityTaskToDB(tasks: ActivityTaskEntity) {
        activityTaskDao.insertActivityTask(tasks)
    }

    override suspend fun deleteMissionsFromDB() {
        missionEntityDao.deleteMissions()
    }

    override suspend fun deleteMissionActivitiesFromDB() {
        missionActivityDao.deleteActivities()
    }

    override suspend fun updateActivityStatusForMission(
        missionId: Int,
        activityComplete: Int,
        pendingActivity: Int
    ) {
        missionEntityDao.updateMissionStatus(missionId, activityComplete, pendingActivity)
    }

    override suspend fun deleteActivityTasksFromDB() {
        activityTaskDao.deleteActivityTask()
    }

    override suspend fun getCasteListFromNetwork(languageId: Int): ApiResponseModel<List<CasteModel>> {
        return apiService.getCasteList(languageId)
    }

    override fun saveCasteList(castes: String) {
        prefRepo.savePref(PREF_CASTE_LIST, castes)
    }

    override fun getCasteList(): List<CasteModel> {
        val castes = prefRepo.getPref(PREF_CASTE_LIST, BLANK_STRING)
        return if ((castes?.isEmpty() == true) || castes.equals("[]")) emptyList()
        else {
            Gson().fromJson(castes, object : TypeToken<List<CasteModel>>() {}.type)
        }
    }

    override suspend fun fetchContentsFromServer(contentMangerRequest: ContentMangerRequest): ApiResponseModel<List<ContentResponse>> {
        return apiService.getAllContent(contentMangerRequest)
    }

    override suspend fun deleteContentFromDB() {
        contentDao.deleteContent()
    }

    override suspend fun saveContentsToDB(contents: List<ContentEntity>) {
        contentDao.insertContent(contents)
    }

    override suspend fun getContentKeyFromDB(): List<String?> {
        val contentKeys = mutableListOf<String?>()
        val sections = sectionEntityDao.getSections()
        val questions = questionEntityDao.getQuestions()
        sections?.forEach { section ->
            val sectionContentKey =
                section?.let { section.contentEntities.map { it.contentKey }.toList() }
            if (sectionContentKey != null) {
                contentKeys.addAll(sectionContentKey)
            }
        }
        questions?.forEach { question ->
            val questionContentKey =
                question?.let { question.contentEntities.map { it.contentKey }.toList() }
            if (questionContentKey != null) {
                contentKeys.addAll(questionContentKey)
            }
        }
        return contentKeys
    }

    override suspend fun getSelectedLanguageId(): String {
        return prefRepo.getAppLanguage() ?: "en"
    }

    override suspend fun getSurveyAnswers(getSurveyAnswerRequest: GetSurveyAnswerRequest): ApiResponseModel<List<QuestionAnswerResponseModel>> {
        return apiService.getSurveyAnswers(getSurveyAnswerRequest)
    }

    private fun saveSurveyAnswerToDb(questionAnswerResponseModels: List<QuestionAnswerResponseModel>?) {
        try {


        questionAnswerResponseModels?.forEach { questionAnswerResponseModel ->
            val question = questionEntityDao.getQuestionForSurveySectionForLanguage(
                questionId = questionAnswerResponseModel.question?.questionId ?: DEFAULT_ID,
                sectionId = questionAnswerResponseModel.sectionId.toInt(),
                surveyId = questionAnswerResponseModel.surveyId,
                languageId = questionAnswerResponseModel.languageId
            )
            if (questionAnswerResponseModel.question?.questionType.equals(QuestionType.Form.name)) {
                baselineDatabase.formQuestionResponseDao()
                    .addFormResponseList(getFormQuestionEntity(questionAnswerResponseModel))

            } else if (questionAnswerResponseModel.question?.questionType == QuestionType.InputNumber.name) {
                baselineDatabase.inputTypeQuestionAnswerDao().saveInputTypeAnswersForQuestion(
                    InputTypeQuestionAnswerEntityMapper.getInputTypeQuestionAnswerEntity(
                        questionAnswerResponseModel,
                        question
                    )
                )
            } else {
                val sectionAnswerEntity =
                    getSectionAnswerEntity(questionAnswerResponseModel, question)
                baselineDatabase.sectionAnswerEntityDao().insertAnswer(sectionAnswerEntity)
            }

        }
        } catch (exception: Exception) {
            Log.e("QuestionType", exception.message.toString())
        }

    }

    override fun getStateId(): Int {
        return prefRepo.getPref(PREF_STATE_ID, -1)
    }

}