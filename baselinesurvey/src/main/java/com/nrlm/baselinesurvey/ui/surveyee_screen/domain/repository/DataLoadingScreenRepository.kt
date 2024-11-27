package com.nrlm.baselinesurvey.ui.surveyee_screen.domain.repository

import com.nrlm.baselinesurvey.database.entity.ActivityTaskEntity
import com.nrlm.baselinesurvey.database.entity.ContentEntity
import com.nrlm.baselinesurvey.database.entity.LanguageEntity
import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import com.nrlm.baselinesurvey.model.datamodel.CasteModel
import com.nrlm.baselinesurvey.model.datamodel.MissionActivityModel
import com.nrlm.baselinesurvey.model.datamodel.MissionTaskModel
import com.nrlm.baselinesurvey.model.request.ContentMangerRequest
import com.nrlm.baselinesurvey.model.request.SurveyRequestBodyModel
import com.nrlm.baselinesurvey.model.response.ApiResponseModel
import com.nrlm.baselinesurvey.model.response.BeneficiaryApiResponse
import com.nrlm.baselinesurvey.model.response.ContentResponse
import com.nrlm.baselinesurvey.model.response.MissionResponseModel
import com.nrlm.baselinesurvey.model.response.SurveyResponseModel
import com.nrlm.baselinesurvey.model.response.UserDetailsResponse

interface DataLoadingScreenRepository {

    suspend fun fetchLocalLanguageList(): List<LanguageEntity>

    suspend fun fetchUseDetialsFromNetwork(userViewApiRequest: String): ApiResponseModel<UserDetailsResponse>

    suspend fun fetchSurveyeeListFromNetwork(userId: Int): ApiResponseModel<BeneficiaryApiResponse>

    suspend fun fetchSurveyFromNetwork(surveyRequestBodyModel: SurveyRequestBodyModel): ApiResponseModel<SurveyResponseModel>

    suspend fun saveSurveyToDb(
        surveyResponseModel: SurveyResponseModel,
        languageId: Int,
    )

    fun saveUserDetails(userDetailsResponse: UserDetailsResponse)

    fun getUserId(): Int

    fun deleteSurveyeeList()

    fun saveSurveyeeList(surveyeeEntity: SurveyeeEntity)

    suspend fun fetchSurveyeeListFromLocalDb(): List<SurveyeeEntity>

    suspend fun fetchSavedSurveyFromServer()
    suspend fun fetchMissionDataFromServer(
        languageCode: String,
        missionName: String
    ): ApiResponseModel<List<MissionResponseModel>>

    suspend fun saveMissionToDB(missions: List<MissionResponseModel>)
    suspend fun saveMissionsActivityToDB(
        activities: List<MissionActivityModel>,
        missionId: Int,
    )
    suspend fun deleteMissionsFromDB()
    suspend fun deleteMissionActivitiesFromDB()
    suspend fun deleteActivityTasksFromDB()

    suspend fun getCasteListFromNetwork(languageId: Int): ApiResponseModel<List<CasteModel>>
    fun saveCasteList(castes: String)
    fun getCasteList(): List<CasteModel>

    suspend fun updateActivityStatusForMission(
        missionId: Int,
        activityComplete: Int,
        pendingActivity: Int
    )

    suspend fun fetchContentsFromServer(contentMangerRequest: ContentMangerRequest): ApiResponseModel<List<ContentResponse>>
    suspend fun deleteContentFromDB()
    suspend fun saveContentsToDB(contents: List<ContentEntity>)
    suspend fun getContentKeyFromDB(): List<String?>
    suspend fun getSelectedLanguageId(): String
    suspend fun getSurveyAnswers()
    fun getStateId(): Int
    suspend fun getSectionStatus()
    suspend fun getTaskForSubjectId(didiId: Int?): ActivityTaskEntity?
    fun saveSettingScreenOpen()
    fun getAppLanguageId(): Int
    fun updateApiStatus(apiEndPoint: String, status: Int, errorMessage: String, errorCode: Int)
    fun insertApiStatus(apiEndPoint: String)
    fun isNeedToCallApi(apiEndPoint: String): Boolean
    fun getBaseLineUserId(): String
    fun saveMissionsActivityTaskToDB(
        missionId: Int,
        activityId: Int,
        activityName: String,
        activities: List<MissionTaskModel>
    )
    suspend fun updateSurveyeeActiveStatus(
        isDidiActive: Int,
        didiId: Int
    )
}
