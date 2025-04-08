package com.sarathi.dataloadingmangement.domain.use_case.livelihood

import com.nudge.core.BLANK_STRING
import com.nudge.core.constants.DataLoadingTriggerType
import com.nudge.core.data.repository.BaseApiCallNetworkUseCase
import com.nudge.core.data.repository.IApiCallJournalRepository
import com.nudge.core.enums.ActivityTypeEnum
import com.nudge.core.enums.ApiStatus
import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.SUCCESS
import com.sarathi.dataloadingmangement.SUCCESS_CODE
import com.sarathi.dataloadingmangement.data.entities.livelihood.SubjectLivelihoodMappingEntity
import com.sarathi.dataloadingmangement.domain.use_case.FetchMissionActivityDetailDataUseCase
import com.sarathi.dataloadingmangement.network.ApiException
import com.sarathi.dataloadingmangement.network.SUBPATH_FETCH_LIVELIHOOD_OPTION
import com.sarathi.dataloadingmangement.repository.liveihood.FetchLivelihoodOptionRepository
import java.util.Locale
import javax.inject.Inject


class FetchLivelihoodOptionNetworkUseCase @Inject constructor(
    private val repository: FetchLivelihoodOptionRepository,
    private val coreSharedPrefs: CoreSharedPrefs,
    apiCallJournalRepository: IApiCallJournalRepository,
    private val fetchMissionActivityDetailDataUseCase: FetchMissionActivityDetailDataUseCase
) : BaseApiCallNetworkUseCase(apiCallJournalRepository) {

    private suspend fun getSubjectLivelihoodMapping(
        activityId: Int,
        screenName: String,
        triggerType: DataLoadingTriggerType,
        moduleName: String,
        customData: Map<String, Any>
    ): Boolean {
        try {
            val apiResponse = repository.getLivelihoodOptionNetwork(
                activityId = activityId,
            )
            if (apiResponse.status.equals(SUCCESS_CODE, true) || apiResponse.status.equals(
                    SUCCESS,
                    true
                )
            ) {
                val subjectLivelihoodMappingEntities =
                    mutableListOf<SubjectLivelihoodMappingEntity>()
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
                }
                updateApiCallStatus(
                    screenName = screenName,
                    moduleName = moduleName,
                    triggerType = triggerType,
                    status = ApiStatus.SUCCESS.name,
                    customData = mapOf(),
                    errorMsg = BLANK_STRING
                )
                return true
            } else {
                updateApiCallStatus(
                    screenName = screenName,
                    moduleName = moduleName,
                    triggerType = triggerType,
                    status = ApiStatus.FAILED.name,
                    customData = mapOf(),
                    errorMsg = apiResponse.message
                )
                return false
            }
        } catch (apiException: ApiException) {
            updateApiCallStatus(
                screenName = screenName,
                moduleName = moduleName,
                triggerType = triggerType,
                status = ApiStatus.FAILED.name,
                customData = mapOf(),
                errorMsg = apiException.stackTraceToString()
            )

            throw apiException
        } catch (ex: Exception) {
            updateApiCallStatus(
                screenName = screenName,
                moduleName = moduleName,
                triggerType = triggerType,
                status = ApiStatus.FAILED.name,
                customData = mapOf(),
                errorMsg = ex.stackTraceToString()
            )
            throw ex
        }
    }

    override suspend fun invoke(
        screenName: String,
        triggerType: DataLoadingTriggerType,
        moduleName: String,
        customData: Map<String, Any>
    ): Boolean {
        try {
            val missionId: Int = if (customData["MissionId"] != null) {
                customData["MissionId"] as? Int ?: -1
            } else {
                -1
            }
            val activityTypes =
                fetchMissionActivityDetailDataUseCase.getActivityTypesForMission(missionId)
            if (screenName == "ActivityScreen" && !activityTypes.contains(
                    ActivityTypeEnum.LIVELIHOOD.name.lowercase(
                        Locale.ENGLISH
                    )
                )
            ) {
                return false
            }
            if (!super.invoke(
                    screenName = screenName,
                    triggerType = triggerType,
                    moduleName = moduleName,
                    customData = mapOf(),
                )
            ) {
                return false
            }
            if (!repository.isLivelihoodAlreadyFetched()) {
                var getActivityIdForLivelihood = repository.getActivityIdForLivelihood()
                getSubjectLivelihoodMapping(
                    screenName = screenName,
                    moduleName = moduleName,
                    triggerType = triggerType,
                    customData = mapOf(),
                    activityId = getActivityIdForLivelihood,
                )
            }
        } catch (ex: Exception) {
            throw ex
        }
        return false
    }

    override fun getApiEndpoint(): String {
        return SUBPATH_FETCH_LIVELIHOOD_OPTION
    }
}
