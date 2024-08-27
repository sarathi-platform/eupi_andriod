package com.sarathi.dataloadingmangement.domain.use_case.livelihood

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.SUCCESS
import com.sarathi.dataloadingmangement.SUCCESS_CODE
import com.sarathi.dataloadingmangement.data.entities.livelihood.SubjectLivelihoodMappingEntity
import com.sarathi.dataloadingmangement.enums.LivelihoodTypeEnum
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
                            livelihoodId =subjectLivelihoodMapping.selectedPrimaryLivelihood!!,
                            primaryLivelihoodId =1,
                            secondaryLivelihoodId = 1,
                            status = 1,
                            type = LivelihoodTypeEnum.PRIMARY.typeId


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
