package com.nrlm.baselinesurvey.ui.surveyee_screen.domain.repository

import com.nrlm.baselinesurvey.PREF_KEY_EMAIL
import com.nrlm.baselinesurvey.PREF_KEY_IDENTITY_NUMBER
import com.nrlm.baselinesurvey.PREF_KEY_NAME
import com.nrlm.baselinesurvey.PREF_KEY_PROFILE_IMAGE
import com.nrlm.baselinesurvey.PREF_KEY_ROLE_NAME
import com.nrlm.baselinesurvey.PREF_KEY_TYPE_NAME
import com.nrlm.baselinesurvey.PREF_KEY_USER_NAME
import com.nrlm.baselinesurvey.PREF_STATE_ID
import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.database.dao.ActivityTaskDao
import com.nrlm.baselinesurvey.database.dao.LanguageListDao
import com.nrlm.baselinesurvey.database.dao.MissionActivityDao
import com.nrlm.baselinesurvey.database.dao.MissionEntityDao
import com.nrlm.baselinesurvey.database.dao.OptionItemDao
import com.nrlm.baselinesurvey.database.dao.QuestionEntityDao
import com.nrlm.baselinesurvey.database.dao.SectionEntityDao
import com.nrlm.baselinesurvey.database.dao.SurveyEntityDao
import com.nrlm.baselinesurvey.database.dao.SurveyeeEntityDao
import com.nrlm.baselinesurvey.database.entity.ActivityTaskEntity
import com.nrlm.baselinesurvey.database.entity.LanguageEntity
import com.nrlm.baselinesurvey.database.entity.MissionActivityEntity
import com.nrlm.baselinesurvey.database.entity.MissionEntity
import com.nrlm.baselinesurvey.database.entity.OptionItemEntity
import com.nrlm.baselinesurvey.database.entity.QuestionEntity
import com.nrlm.baselinesurvey.database.entity.SectionEntity
import com.nrlm.baselinesurvey.database.entity.SurveyEntity
import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import com.nrlm.baselinesurvey.model.request.MissionRequest
import com.nrlm.baselinesurvey.model.request.SurveyRequestBodyModel
import com.nrlm.baselinesurvey.model.response.ApiResponseModel
import com.nrlm.baselinesurvey.model.response.BeneficiaryApiResponse
import com.nrlm.baselinesurvey.model.response.MissionResponseModel
import com.nrlm.baselinesurvey.model.response.SurveyResponseModel
import com.nrlm.baselinesurvey.model.response.UserDetailsResponse
import com.nrlm.baselinesurvey.network.interfaces.ApiService
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
    val activityTaskDao: ActivityTaskDao
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

    override fun saveSurveyToDb(surveyResponseModel: SurveyResponseModel, languageId: Int) {
        surveyEntityDao.deleteSurveyFroLanguage(surveyResponseModel.surveyId, languageId)
        val surveyEntity = SurveyEntity(
            id = 0,
            surveyId = surveyResponseModel.surveyId,
            surveyName = surveyResponseModel.surveyName,
            surveyPassingMark = surveyResponseModel.surveyPassingMark,
            thresholdScore = surveyResponseModel.thresholdScore,
            languageId = languageId
        )
        surveyEntityDao.insertSurvey(surveyEntity)
        surveyResponseModel.sections.forEach { section ->
            sectionEntityDao.deleteSurveySectionFroLanguage(section.sectionId, surveyResponseModel.surveyId, languageId)
            val sectionEntity = SectionEntity(
                id = 0,
                sectionId = section.sectionId,
                surveyId = surveyResponseModel.surveyId,
                sectionName = section.sectionName,
                sectionOrder = section.sectionOrder,
                sectionDetails = section.sectionDetails,
                sectionIcon = section.sectionIcon,
                languageId = languageId
            )
            sectionEntityDao.insertSection(sectionEntity)
            section.questionList.forEach { question ->
                if (question != null) {
                    questionEntityDao.deleteSurveySectionQuestionFroLanguage(
                        question.questionId!!,
                        section.sectionId,
                        surveyResponseModel.surveyId,
                        languageId
                    )
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
                        languageId = languageId
                    )
                    question.options.forEach { optionsItem ->
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
                                questionList = optionsItem.questionList,
                                conditional = optionsItem.conditional,
                                order = optionsItem.order,
                                values = optionsItem.values,
                                languageId = languageId
                            )
                            optionItemDao.insertOption(optionItemEntity)
                        }
                    }
                    questionEntityDao.insertQuestion(questionEntity)
                }
            }
        }
    }

    override fun saveUserDetails(userDetailsResponse: UserDetailsResponse) {
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

    override suspend fun deleteActivityTasksFromDB() {
        activityTaskDao.deleteActivityTask()
    }

}