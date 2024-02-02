package com.nrlm.baselinesurvey.ui.surveyee_screen.domain.repository

import com.nrlm.baselinesurvey.database.entity.LanguageEntity
import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import com.nrlm.baselinesurvey.model.request.SurveyRequestBodyModel
import com.nrlm.baselinesurvey.model.response.ApiResponseModel
import com.nrlm.baselinesurvey.model.response.BeneficiaryApiResponse
import com.nrlm.baselinesurvey.model.response.SurveyResponseModel
import com.nrlm.baselinesurvey.model.response.UserDetailsResponse

interface DataLoadingScreenRepository {

    suspend fun fetchLocalLanguageList(): List<LanguageEntity>

    suspend fun fetchUseDetialsFromNetwork(userViewApiRequest: String): ApiResponseModel<UserDetailsResponse>

    suspend fun fetchSurveyeeListFromNetwork(userId: Int): ApiResponseModel<BeneficiaryApiResponse>

    suspend fun fetchSurveyFromNetwork(surveyRequestBodyModel: SurveyRequestBodyModel): ApiResponseModel<List<SurveyResponseModel>>

    fun saveSurveyToDb(surveyResponseModel: SurveyResponseModel, languageId: Int)

    fun saveUserDetails(userDetailsResponse: UserDetailsResponse)

    fun getUserId(): Int

    fun deleteSurveyeeList()

    fun saveSurveyeeList(surveyeeEntity: SurveyeeEntity)

    suspend fun fetchSurveyeeListFromLocalDb(): List<SurveyeeEntity>

    suspend fun fetchSavedSurveyFromServer()
}
