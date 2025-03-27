package com.sarathi.dataloadingmangement.domain.use_case.livelihood

import com.nudge.core.BLANK_STRING
import com.nudge.core.constants.DataLoadingTriggerType
import com.nudge.core.data.repository.BaseApiCallNetworkUseCase
import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.SUCCESS
import com.sarathi.dataloadingmangement.SUCCESS_CODE
import com.sarathi.dataloadingmangement.data.entities.livelihood.SubjectLivelihoodMappingEntity
import com.sarathi.dataloadingmangement.network.SUBPATH_FETCH_LIVELIHOOD_OPTION
import com.sarathi.dataloadingmangement.repository.liveihood.FetchLivelihoodOptionRepository
import javax.inject.Inject


class FetchLivelihoodOptionNetworkUseCase @Inject constructor(
    private val repository: FetchLivelihoodOptionRepository,
    private val coreSharedPrefs: CoreSharedPrefs,
) : BaseApiCallNetworkUseCase() {

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

                    subjectLivelihoodMapping.livelihoodDTO.forEach {
                        subjectLivelihoodMappingEntities.add(
                            SubjectLivelihoodMappingEntity.getSubjectLivelihoodMappingEntity(
                                userId = coreSharedPrefs.getUniqueUserIdentifier(),
                                subjectId = subjectLivelihoodMapping.didiId,
                                livelihoodId = it.programLivelihoodId,
                                status = 1,
                                type = it.order,
                                livelihoodType = it.type ?: BLANK_STRING
                            )
                        )
                    }
                }
                repository.saveAllSubjectLivelihoodDetails(subjectLivelihoodMappingEntities)
                return true
            }
        } else {
            return true
        }
        return false
    }

    override suspend fun invoke(
        screenName: String,
        triggerType: DataLoadingTriggerType,
        moduleName: String,
        customData: Map<String, Any>
    ): Boolean {
        try {
            if (!super.invoke(
                    screenName = screenName,
                    triggerType = triggerType,
                    moduleName = moduleName,
                    customData = customData,
                )
            ) {
                return false
            }
            if (!repository.isLivelihoodAlreadyFetched()) {
                var getActivityIdForLivelihood = repository.getActivityIdForLivelihood()
                getSubjectLivelihoodMapping(
                    activityId = getActivityIdForLivelihood,
                )
            }
        } catch (ex: Exception) {
            throw ex
        }
        return true
    }

    override fun getApiEndpoint(): String {
        return SUBPATH_FETCH_LIVELIHOOD_OPTION
    }
}
