package com.sarathi.dataloadingmangement.repository.liveihood

import com.nudge.core.model.ApiResponseModel
import com.sarathi.dataloadingmangement.data.entities.ActivityConfigEntity
import com.sarathi.dataloadingmangement.data.entities.livelihood.SubjectLivelihoodMappingEntity
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.LivelihoodOptionResponse

interface FetchLivelihoodOptionRepository {
    suspend fun getLivelihoodOptionNetwork(
        activityId: Int,
    ): ApiResponseModel<List<LivelihoodOptionResponse>>

    suspend fun saveFromToDB(
        subjectId: Int,
        activityId: Int,
        selectedPrimaryLivelihood: Int,
        selectedSecondaryLivelihood: Int
    ): SubjectLivelihoodMappingEntity
    suspend fun deleteContentFromDB()
    suspend fun saveAllFormDetails(formDetails: List<SubjectLivelihoodMappingEntity>)
    suspend fun getActivityConfigUiModel(): List<ActivityConfigEntity>?
suspend fun getActivityIdForLivelihood():Int

}