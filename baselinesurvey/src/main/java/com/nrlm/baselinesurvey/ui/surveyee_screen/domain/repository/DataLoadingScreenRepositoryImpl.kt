package com.nrlm.baselinesurvey.ui.surveyee_screen.domain.repository

import android.text.TextUtils
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.DEFAULT_ID
import com.nrlm.baselinesurvey.DEFAULT_LANGUAGE_ID
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
import com.nrlm.baselinesurvey.SUCCESS_CODE
import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.database.NudgeBaselineDatabase
import com.nrlm.baselinesurvey.database.dao.ActivityTaskDao
import com.nrlm.baselinesurvey.database.dao.ContentDao
import com.nrlm.baselinesurvey.database.dao.DidiSectionProgressEntityDao
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
import com.nrlm.baselinesurvey.model.datamodel.ConditionDtoWithParentId
import com.nrlm.baselinesurvey.model.datamodel.MissionActivityModel
import com.nrlm.baselinesurvey.model.datamodel.MissionTaskModel
import com.nrlm.baselinesurvey.model.datamodel.OptionsItem
import com.nrlm.baselinesurvey.model.datamodel.QuestionList
import com.nrlm.baselinesurvey.model.datamodel.Sections
import com.nrlm.baselinesurvey.model.mappers.DidiInfoEntityMapper.getDidiDidiInfoEntity
import com.nrlm.baselinesurvey.model.mappers.DidiSectionStatusEntityMapper.getDidiSectionStatusEntity
import com.nrlm.baselinesurvey.model.mappers.FormQuestionEntityMapper.getFormQuestionEntity
import com.nrlm.baselinesurvey.model.mappers.InputTypeQuestionAnswerEntityMapper
import com.nrlm.baselinesurvey.model.mappers.SectionAnswerEntityMapper.getSectionAnswerEntity
import com.nrlm.baselinesurvey.model.request.ContentMangerRequest
import com.nrlm.baselinesurvey.model.request.GetSurveyAnswerRequest
import com.nrlm.baselinesurvey.model.request.MissionRequest
import com.nrlm.baselinesurvey.model.request.SectionStatusRequest
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
import com.nrlm.baselinesurvey.ui.question_type_screen.presentation.component.OptionItemEntityState
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.nrlm.baselinesurvey.utils.convertFormTypeQuestionListToOptionItemEntity
import com.nrlm.baselinesurvey.utils.convertQuestionListToOptionItemEntity
import com.nrlm.baselinesurvey.utils.states.SectionStatus
import com.nudge.core.database.dao.ApiStatusDao
import com.nudge.core.database.entities.ApiStatusEntity
import com.nudge.core.enums.ApiStatus
import com.nudge.core.toDate
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
    val baselineDatabase: NudgeBaselineDatabase,
    val didiSectionProgressEntityDao: DidiSectionProgressEntityDao,
    val apiStatusDao: ApiStatusDao
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
    ) {
        baselineDatabase.runInTransaction {
            surveyEntityDao.deleteSurveyFroLanguage(
                userId = getBaseLineUserId(),
                surveyResponseModel.surveyId,
                languageId
            )
            val surveyEntity = SurveyEntity(
                userId = getBaseLineUserId(),
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
                val subQuestionList = mutableListOf<ConditionDtoWithParentId>()
                val subSubQuestionList = mutableListOf<ConditionDtoWithParentId>()
                val contentLists = mutableListOf<ContentList>()
                sectionEntityDao.deleteSurveySectionFroLanguage(
                    userId = getBaseLineUserId(),
                    section.sectionId,
                    surveyResponseModel.surveyId,
                    languageId
                )
                val sectionEntity = SectionEntity(
                    id = 0,
                    userId = getBaseLineUserId(),
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
                                    val conditionDtoWithParentIdList =
                                        mutableListOf<ConditionDtoWithParentId>()
                                    (it?.resultList ?: emptyList()).forEach {
                                        val conditionDtoWithParentId =
                                            ConditionDtoWithParentId(it, question.questionId ?: 0)
                                        conditionDtoWithParentIdList.add(conditionDtoWithParentId)
                                    }
                                    subQuestionList.addAll(conditionDtoWithParentIdList)

                                }
                            }
                        }
                    }
                    contentLists.addAll(question?.contentList ?: listOf())
                }
                subQuestionList.forEach { conditionalItem ->
                    Log.d("saveSurveyToDb", "subQuestionList.forEach -> ${conditionalItem}")
                    saveQuestionAndOptionsToDb(
                        question = conditionalItem.resultList,
                        section,
                        surveyResponseModel,
                        languageId,
                        true,
                        parentId = conditionalItem.parentId
                    )
                    conditionalItem.resultList.options?.forEach { subQuestionOption ->
                        if (subQuestionOption?.conditions != null) {
                            subQuestionOption.conditions.forEach {
                                if (it?.resultType?.equals(
                                        ResultType.Questions.name,
                                        true
                                    ) == true
                                ) {
                                    val conditionDtoWithParentIdList =
                                        mutableListOf<ConditionDtoWithParentId>()
                                    it.resultList.forEach {
                                        val conditionDtoWithParentId = ConditionDtoWithParentId(
                                            it,
                                            parentId = conditionalItem.resultList.questionId ?: 0
                                        )
                                        conditionDtoWithParentIdList.add(conditionDtoWithParentId)
                                    }
                                    subSubQuestionList.addAll(conditionDtoWithParentIdList)
                                }
                            }
                        }
                    }
                }
                subSubQuestionList.forEach { subConditionalItem ->
                    saveQuestionAndOptionsToDb(
                        question = subConditionalItem.resultList,
                        section,
                        surveyResponseModel,
                        languageId,
                        true,
                        parentId = subConditionalItem.parentId
                    )
                }
            }

        }

    }

    private fun saveQuestionAndOptionsToDb(
        question: QuestionList?,
        section: Sections,
        surveyResponseModel: SurveyResponseModel,
        languageId: Int,
        isSubQuestionList: Boolean = false,
        parentId: Int = 0
    ) {
        try {
            if (question?.questionId != null) {
                val existingQuestion = questionEntityDao.getQuestionForSurveySectionForLanguage(
                    userid = getBaseLineUserId(),
                    question.questionId!!,
                    section.sectionId,
                    surveyResponseModel.surveyId,
                    languageId
                )
                if (existingQuestion != null) {
                    questionEntityDao.deleteSurveySectionQuestionFroLanguage(
                        userid = getBaseLineUserId(),
                        question.questionId!!,
                        section.sectionId,
                        surveyResponseModel.surveyId,
                        languageId
                    )
                }
                Log.d("TAG", "saveQuestionAndOptionsToDb: question -> ${question.questionDisplay}")
                val questionEntity = QuestionEntity(
                    id = 0,
                    userId = getBaseLineUserId(),
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
                    tag = question.attributeTag ?: 0,
                    contentEntities = question.contentList,
                    parentQuestionId = parentId
                )

                questionEntityDao.insertQuestion(questionEntity)
                question.options?.forEach { optionsItem ->
                    if (optionsItem != null) {
                        optionItemDao.deleteSurveySectionQuestionOptionFroLanguage(
                            userId = getBaseLineUserId(),
                            optionsItem.optionId!!,
                            question.questionId!!,
                            section.sectionId,
                            surveyResponseModel.surveyId,
                            languageId
                        )
                        val optionItemEntity = OptionItemEntity(
                            id = 0,
                            userId = getBaseLineUserId(),
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
                            conditions = optionsItem.conditions,
                            optionTag = optionsItem.tag ?: 0,
                            contentEntities = optionsItem.contentList
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
        val isOptionExisting = optionItemDao.isOptionAlreadyPresent(
            userId = getBaseLineUserId(),
            questionId = question.questionId!!,
            sectionId = section.sectionId,
            surveyId = surveyResponseModel.surveyId
        )
        if (isOptionExisting == 0) {
            val optionItemEntity = OptionItemEntity(
                id = 0,
                userId = getBaseLineUserId(),
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
                conditions = optionsItem.conditions,
                optionTag = optionsItem.tag ?: 0
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
        if (TextUtils.isEmpty(prefRepo.getPref(PREF_KEY_USER_NAME, ""))) {
            return 0
        }
        return prefRepo.getPref(PREF_KEY_USER_NAME, "")?.toInt() ?: 0
    }

    override fun deleteSurveyeeList() {
        surveyeeEntityDao.deleteSurveyees(getBaseLineUserId())
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

    override suspend fun saveMissionToDB(missions: List<MissionResponseModel>) {

        missionEntityDao.softDeleteMission(getBaseLineUserId())
        missions.forEach { mission ->
            val missionCount = missionEntityDao.getMissionCount(
                userId = getBaseLineUserId(),
                missionId = mission.missionId
            )
            if (missionCount == 0) {
                missionEntityDao.insertMission(
                    MissionEntity.getMissionEntity(
                        userId = getBaseLineUserId(),
                        activityTaskSize = mission.activities.size,
                        mission = mission
                    )
                )
            } else {
                missionEntityDao.updateMissionActiveStatus(mission.missionId, getBaseLineUserId())
            }
        }



    }

    override suspend fun saveMissionsActivityToDB(
        activities: List<MissionActivityModel>,
        missionId: Int,
    ) {
        missionActivityDao.softDeleteActivity(missionId, getBaseLineUserId())
        activities.forEach { missionActivityModel ->
            val activityCount = missionActivityDao.getActivityCount(
                userId = getBaseLineUserId(),
                missionActivityModel.activityId
            )
            if (activityCount == 0) {
                missionActivityDao.insertMissionActivity(
                    MissionActivityEntity.getMissionActivityEntity(
                        getBaseLineUserId(),
                        missionId,
                        missionActivityModel.tasks.size,
                        missionActivityModel
                    )
                )
            } else {
                missionActivityDao.updateActivityActiveStatus(
                    missionId,
                    getBaseLineUserId(),
                    1,
                    missionActivityModel.activityId
                )
            }
        }

    }

    override fun saveMissionsActivityTaskToDB(
        missionId: Int,
        activityId: Int,
        activityName: String,
        activities: List<MissionTaskModel>
    ) {
        activityTaskDao.softDeleteActivityTask(getBaseLineUserId(), activityId, missionId)
        activities.forEach { task ->
            val taskCount =
                activityTaskDao.getTaskByIdCount(
                    userId = getBaseLineUserId(),
                    taskId = task.id ?: 0
                )
            if (taskCount == 0) {
                activityTaskDao.insertActivityTask(
                    ActivityTaskEntity.getActivityTaskEntity(
                        userId = getBaseLineUserId(),
                        missionId = missionId,
                        activityId = activityId,
                        activityName = activityName,
                        task = task,
                    )
                )
            } else {
                activityTaskDao.updateActiveTaskStatus(1, task.id ?: 0, getBaseLineUserId())
            }

        }
    }



    override suspend fun deleteMissionsFromDB() {
        missionEntityDao.deleteMissions(
            userId = getBaseLineUserId()
        )
    }

    override suspend fun deleteMissionActivitiesFromDB() {
        missionActivityDao.deleteActivities(
            userId = getBaseLineUserId()
        )
    }

    override suspend fun updateActivityStatusForMission(
        missionId: Int,
        activityComplete: Int,
        pendingActivity: Int
    ) {
        missionEntityDao.updateMissionStatus(
            userId = getBaseLineUserId(),
            missionId, activityComplete, pendingActivity
        )
    }

    override suspend fun deleteActivityTasksFromDB() {
        activityTaskDao.deleteActivityTask(
            userId = getBaseLineUserId()
        )
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
        val sections = sectionEntityDao.getSections(
            userId = getBaseLineUserId(),
        )
        val questions = questionEntityDao.getQuestions(userid = getBaseLineUserId())
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

    override suspend fun getSurveyAnswers() {
        getSurveyId()?.forEach {
            val surveyAnswersResponse = apiService.getSurveyAnswers(
                GetSurveyAnswerRequest(
                    surveyId = it,
                    mobileNumber = prefRepo.getMobileNumber() ?: "",
                    userId = prefRepo.getUserId().toInt()
                )
            )
            if (surveyAnswersResponse.status.equals(
                    SUCCESS_CODE,
                    true
                ) && surveyAnswersResponse.data != null
            ) {
                saveSurveyAnswerToDb(surveyAnswersResponse.data)
            }
        }
    }

    private fun getSurveyId(): List<Int>? {
        return surveyEntityDao.getSurveyIds(getBaseLineUserId())
    }

    private fun saveSurveyAnswerToDb(questionAnswerResponseModels: List<QuestionAnswerResponseModel>?) {
        try {
            questionAnswerResponseModels?.forEach { questionAnswerResponseModel ->
                val question = questionEntityDao.getQuestionForSurveySectionForLanguage(
                    userid = getBaseLineUserId(),
                    questionId = questionAnswerResponseModel.question?.questionId ?: DEFAULT_ID,
                    sectionId = questionAnswerResponseModel.sectionId.toInt(),
                    surveyId = questionAnswerResponseModel.surveyId,
                    languageId = questionAnswerResponseModel.languageId
                )
                val optionsList = optionItemDao.getSurveySectionQuestionOptions(
                    userId = getBaseLineUserId(),
                    questionId = questionAnswerResponseModel.question?.questionId ?: DEFAULT_ID,
                    sectionId = questionAnswerResponseModel.sectionId.toInt(),
                    surveyId = questionAnswerResponseModel.surveyId,
                    languageId = questionAnswerResponseModel.languageId
                )

                if (questionAnswerResponseModel.question?.questionType.equals(QuestionType.Form.name)) {
                    val formQuestionEntityList =
                        getFormQuestionEntity(
                            questionAnswerResponseModel
                        )
                    formQuestionEntityList.forEach { formQuestionResponseEntity ->
                        formQuestionResponseEntity.userId = getBaseLineUserId()
                        val isQuestionAnswered =
                            baselineDatabase.formQuestionResponseDao()
                                .isQuestionOptionAlreadyAnswered(
                                    userId = getBaseLineUserId(),
                                    surveyId = formQuestionResponseEntity.surveyId,
                                    sectionId = formQuestionResponseEntity.sectionId,
                                    questionId = formQuestionResponseEntity.questionId,
                                    didiId = formQuestionResponseEntity.didiId,
                                    optionId = formQuestionResponseEntity.optionId,
                                    referenceId = formQuestionResponseEntity.referenceId
                                )
                        if (isQuestionAnswered > 0) {
                            baselineDatabase.formQuestionResponseDao().updateOptionItemValue(
                                userId = getBaseLineUserId(),
                                surveyId = formQuestionResponseEntity.surveyId,
                                sectionId = formQuestionResponseEntity.sectionId,
                                questionId = formQuestionResponseEntity.questionId,
                                optionId = formQuestionResponseEntity.optionId,
                                selectedValue = formQuestionResponseEntity.selectedValue,
                                referenceId = formQuestionResponseEntity.referenceId,
                                didiId = formQuestionResponseEntity.didiId
                            )
                        } else {
                            baselineDatabase.formQuestionResponseDao()
                                .addFormResponse(formQuestionResponseEntity)
                        }
                    }
                } else if (questionAnswerResponseModel.question?.questionType == QuestionType.InputNumber.name) {
                    val inputTypeQuestionAnswerEntity =
                        InputTypeQuestionAnswerEntityMapper.getInputTypeQuestionAnswerEntity(
                            questionAnswerResponseModel,
                            question,
                            optionsList
                        )
                    inputTypeQuestionAnswerEntity.forEach { inputTypeQuestionAnswerEntity ->
                        inputTypeQuestionAnswerEntity.userId = getBaseLineUserId()
                        val isQuestionAlreadyAnswered =
                            baselineDatabase.inputTypeQuestionAnswerDao().isQuestionAlreadyAnswered(
                                userId = getBaseLineUserId(),
                                inputTypeQuestionAnswerEntity.surveyId,
                                inputTypeQuestionAnswerEntity.sectionId,
                                inputTypeQuestionAnswerEntity.didiId,
                                inputTypeQuestionAnswerEntity.questionId,
                                inputTypeQuestionAnswerEntity.optionId
                            )
                        if (isQuestionAlreadyAnswered > 0) {
                            baselineDatabase.inputTypeQuestionAnswerDao()
                                .updateInputTypeAnswersForQuestion(
                                    userId = getBaseLineUserId(),
                                    inputTypeQuestionAnswerEntity.surveyId,
                                    inputTypeQuestionAnswerEntity.sectionId,
                                    inputTypeQuestionAnswerEntity.didiId,
                                    inputTypeQuestionAnswerEntity.questionId,
                                    inputTypeQuestionAnswerEntity.optionId,
                                    inputTypeQuestionAnswerEntity.inputValue
                                )
                        } else {
                            baselineDatabase.inputTypeQuestionAnswerDao()
                                .saveInputTypeAnswersForQuestion(
                                    inputTypeQuestionAnswerEntity
                                )
                        }
                    }

                } else if (questionAnswerResponseModel.question?.questionType.equals(QuestionType.DidiDetails.name)) {

                    val didiIntoEntity = getDidiDidiInfoEntity(questionAnswerResponseModel)
                    baselineDatabase.didiInfoEntityDao()
                        .checkAndUpdateDidiInfo(didiIntoEntity, getBaseLineUserId())

                } else {
                    val sectionAnswerEntity =
                        getSectionAnswerEntity(questionAnswerResponseModel, question, optionsList)
                    sectionAnswerEntity.userId = getBaseLineUserId()
                    val isQuestionAlreadyAnswer = baselineDatabase.sectionAnswerEntityDao()
                        .isQuestionAlreadyAnswered(
                            userId = getBaseLineUserId(),
                            sectionAnswerEntity.didiId,
                            sectionAnswerEntity.questionId,
                            sectionAnswerEntity.sectionId,
                            sectionAnswerEntity.surveyId
                        )
                    if (isQuestionAlreadyAnswer > 0) {
                        baselineDatabase.sectionAnswerEntityDao().updateAnswer(
                            userId = getBaseLineUserId(),
                            didiId = sectionAnswerEntity.didiId,
                            sectionId = sectionAnswerEntity.sectionId,
                            questionId = sectionAnswerEntity.questionId,
                            surveyId = sectionAnswerEntity.surveyId,
                            optionItems = sectionAnswerEntity.optionItems,
                            questionType = sectionAnswerEntity.questionType,
                            questionSummary = sectionAnswerEntity.questionType
                        )
                    } else {
                        baselineDatabase.sectionAnswerEntityDao().insertAnswer(sectionAnswerEntity)
                    }
                }

            }
        } catch (exception: Exception) {
            BaselineLogger.e("QuestionType", exception.message.toString())
        }

    }

    override fun getStateId(): Int {
        return prefRepo.getPref(PREF_STATE_ID, -1)
    }

    override suspend fun getSectionStatus() {
        try {
            getSurveyId()?.forEach {
                val sectionStatusResponse = apiService.getSectionStatus(
                    SectionStatusRequest(
                        sectionId = 2,
                        surveyId = it,
                        userId = prefRepo.getUserId().toInt()
                    )
                )
                if (sectionStatusResponse.status.equals(
                        SUCCESS_CODE,
                        true
                    )
                ) {
                    val didiSectionStatusEntity = getDidiSectionStatusEntity(
                        sectionStatusResponse.data!!, getBaseLineUserId()
                    )
                    didiSectionProgressEntityDao.addDidiSectionProgress(
                        didiSectionStatusEntity
                    )
                    didiSectionStatusEntity.groupBy { it.didiId }.forEach { map ->
                        if (map.value.any { it.sectionStatus == SectionStatus.INPROGRESS.ordinal || it.sectionStatus == SectionStatus.COMPLETED.ordinal }) {
                            surveyeeEntityDao.updateDidiSurveyStatusAfterCheck(
                                map.key,
                                SectionStatus.INPROGRESS.ordinal
                            )
                        }
                    }
                }
            }
        } catch (ecxpetion: Exception) {
            Log.e("SectionStatus", ecxpetion.message.toString())
        }

    }

    override suspend fun getTaskForSubjectId(didiId: Int?): ActivityTaskEntity? {
        return activityTaskDao.getTaskFromSubjectId(getBaseLineUserId(), didiId ?: 0)
    }

    override fun getAppLanguageId(): Int {
        return prefRepo.getAppLanguageId() ?: DEFAULT_LANGUAGE_ID
    }

    override fun updateApiStatus(
        apiEndPoint: String,
        status: Int,
        errorMessage: String,
        errorCode: Int
    ) {
        apiStatusDao.updateApiStatus(apiEndPoint, status = status, errorMessage, errorCode)
    }

    override fun insertApiStatus(apiEndPoint: String) {
        val apiStatusEntity = ApiStatusEntity(
            apiEndpoint = apiEndPoint,
            status = ApiStatus.INPROGRESS.ordinal,
            modifiedDate = System.currentTimeMillis().toDate(),
            createdDate = System.currentTimeMillis().toDate(),
            errorCode = 0,
            errorMessage = ""
        )
        apiStatusDao.insert(apiStatusEntity)
    }

    override fun getBaseLineUserId(): String {
        return prefRepo.getUniqueUserIdentifier()
    }
    override fun isNeedToCallApi(apiEndPoint: String): Boolean {
        return if (apiStatusDao.getFailedAPICount() > 0) {
            val apiStatusEntity = apiStatusDao.getAPIStatus(apiEndPoint)
            apiStatusEntity?.status != ApiStatus.SUCCESS.ordinal
        } else {
            true
        }
    }

}