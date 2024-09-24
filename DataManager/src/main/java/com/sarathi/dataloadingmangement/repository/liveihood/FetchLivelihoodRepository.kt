package com.sarathi.dataloadingmangement.repository.liveihood

import com.nudge.core.model.ApiResponseModel
import com.sarathi.dataloadingmangement.data.entities.ActivityConfigEntity
import com.sarathi.dataloadingmangement.data.entities.livelihood.SubjectLivelihoodMappingEntity
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.LivelihoodOptionResponse

interface FetchLivelihoodOptionRepository {
    suspend fun getLivelihoodOptionNetwork(
        activityId: Int,
    ): ApiResponseModel<List<LivelihoodOptionResponse>>
    suspend fun saveAllSubjectLivelihoodDetails(subjectLivelihoodMappingEntity: List<SubjectLivelihoodMappingEntity>)
    suspend fun getActivityConfigUiModel(): List<ActivityConfigEntity>?
suspend fun getActivityIdForLivelihood():Int

}