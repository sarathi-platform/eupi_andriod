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
import com.nrlm.baselinesurvey.database.dao.LanguageListDao
import com.nrlm.baselinesurvey.database.dao.QuestionEntityDao
import com.nrlm.baselinesurvey.database.dao.SectionEntityDao
import com.nrlm.baselinesurvey.database.dao.SurveyEntityDao
import com.nrlm.baselinesurvey.database.dao.SurveyeeEntityDao
import com.nrlm.baselinesurvey.database.entity.LanguageEntity
import com.nrlm.baselinesurvey.database.entity.QuestionEntity
import com.nrlm.baselinesurvey.database.entity.SectionEntity
import com.nrlm.baselinesurvey.database.entity.SurveyEntity
import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import com.nrlm.baselinesurvey.model.request.SurveyRequestBodyModel
import com.nrlm.baselinesurvey.model.response.ApiResponseModel
import com.nrlm.baselinesurvey.model.response.BeneficiaryApiResponse
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
    val questionEntityDao: QuestionEntityDao
): DataLoadingScreenRepository  {
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
        surveyResponseModel.surveyList.forEach { section ->
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
                    questionEntityDao.deleteSurveySectionQuestionFroLanguage(question.questionId!!, section.sectionId, surveyResponseModel.surveyId, languageId)
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
                        options = question.options,
                        languageId = languageId
                    )
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
        return prefRepo.getPref(PREF_KEY_USER_NAME,"")?.toInt() ?: 0
    }

    override fun deleteSurveyeeList() {
        surveyeeEntityDao.deleteSurveyees()
    }

    override fun saveSurveyeeList(surveyeeEntity: SurveyeeEntity) {
        surveyeeEntityDao.insertDidi(surveyeeEntity)
    }


}