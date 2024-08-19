package com.sarathi.dataloadingmangement.domain.use_case.livelihood

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.SUCCESS
import com.sarathi.dataloadingmangement.SUCCESS_CODE
import com.sarathi.dataloadingmangement.data.entities.livelihood.SubjectLivelihoodMappingEntity
import com.sarathi.dataloadingmangement.repository.liveihood.FetchLivelihoodOptionRepository
import javax.inject.Inject


class FetchLivelihoodOptionNetworkUseCase @Inject constructor(
    private val repository: FetchLivelihoodOptionRepository,
    private val coreSharedPrefs: CoreSharedPrefs,
) {

    private suspend fun getSubjectLivelihoodMapping (activityId: Int): Boolean {
        val apiResponse = repository.getLivelihoodOptionNetwork(
            activityId = activityId,
        )
        if (apiResponse.status.equals(SUCCESS_CODE, true) || apiResponse.status.equals(
                SUCCESS,
                true
            )
        ) {
            val subjectLivelihoodMappingEntities = mutableListOf<SubjectLivelihoodMappingEntity>()
            apiResponse.data?.let { subjectLivelihoodMappingDetail ->
                subjectLivelihoodMappingDetail.forEach { subjectLivelihoodMapping ->
                    subjectLivelihoodMappingEntities.add(
                        SubjectLivelihoodMappingEntity.getSubjectLivelihoodMappingEntity(
                            userId = coreSharedPrefs.getUniqueUserIdentifier(),
                            subjectId = subjectLivelihoodMapping.subjectId,
                            primaryLivelihoodId =subjectLivelihoodMapping.selectedPrimaryLivelihood!! ,
                            secondaryLivelihoodId = subjectLivelihoodMapping.selectedSecondaryLivelihood
                        )
                    )
                }
                repository.saveAllSubjectLivelihoodDetails(subjectLivelihoodMappingEntities)
                return true
            }
        } else {
            return true
        }
        return false
    }
    suspend fun saveLivelihoodMappingData(
        subjectId: Int,

        selectedPrimaryLivelihood: Int,
        selectedSecondaryLivelihood: Int,
        activityId: Int,
    ): SubjectLivelihoodMappingEntity {
        return repository.saveFromToDB(
            subjectId = subjectId,
            selectedPrimaryLivelihood = selectedPrimaryLivelihood,
            selectedSecondaryLivelihood = selectedSecondaryLivelihood,
            activityId = activityId
        )
    }
    suspend fun invoke(): Boolean {
        try {

            var getActivityIdForLivelihood =repository.getActivityIdForLivelihood()
            getSubjectLivelihoodMapping(
                activityId = getActivityIdForLivelihood,
                )
        } catch (ex: Exception) {
            throw ex
        }
        return true
    }
}
