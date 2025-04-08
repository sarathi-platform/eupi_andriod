package com.sarathi.dataloadingmangement.domain.use_case.livelihood

import com.nudge.core.BLANK_STRING
import com.nudge.core.constants.DataLoadingTriggerType
import com.nudge.core.data.repository.BaseApiCallNetworkUseCase
import com.nudge.core.data.repository.IApiCallJournalRepository
import com.nudge.core.enums.ActivityTypeEnum
import com.nudge.core.enums.ApiStatus
import com.sarathi.dataloadingmangement.SUCCESS
import com.sarathi.dataloadingmangement.domain.use_case.FetchMissionActivityDetailDataUseCase
import com.sarathi.dataloadingmangement.enums.LivelihoodLanguageReferenceType
import com.sarathi.dataloadingmangement.model.response.LivelihoodResponse
import com.sarathi.dataloadingmangement.network.ApiException
import com.sarathi.dataloadingmangement.network.SUBPATH_GET_LIVELIHOOD_CONFIG
import com.sarathi.dataloadingmangement.repository.liveihood.ICoreLivelihoodRepository
import java.util.Locale
import javax.inject.Inject

class LivelihoodUseCase @Inject constructor(
    private val coreLivelihoodRepositoryImpl: ICoreLivelihoodRepository,
    apiCallJournalRepository: IApiCallJournalRepository,
    private val fetchMissionActivityDetailDataUseCase: FetchMissionActivityDetailDataUseCase,
) : BaseApiCallNetworkUseCase(apiCallJournalRepository) {
    override suspend operator fun invoke(
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
            if (!super.invoke(screenName, triggerType, moduleName, mapOf())) {
                return false
            }
            val apiResponse = coreLivelihoodRepositoryImpl.getLivelihoodConfigFromNetwork()
            if (apiResponse.status.equals(SUCCESS, true)) {
                apiResponse.data?.let {
                    coreLivelihoodRepositoryImpl.deleteLivelihoodCoreDataForUser()
                    saveLivelihoodConfigInDb(it)
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

    private suspend fun saveLivelihoodConfigInDb(livelihoodResponses: List<LivelihoodResponse>) {
        livelihoodResponses.forEach { livelihoodResponse ->
            livelihoodResponse.livelihood?.let { livelihood ->
                coreLivelihoodRepositoryImpl.saveLivelihoodItemToDB(
                    livelihood,
                    LivelihoodLanguageReferenceType.Livelihood.name
                )
            }
            livelihoodResponse.assets?.let { assets ->
                livelihoodResponse.livelihood?.programLivelihoodId?.let {
                    coreLivelihoodRepositoryImpl.saveLivelihoodItemListToDB(
                        assets,
                        it
                    )
                }
            }
            livelihoodResponse.products?.let { products ->
                livelihoodResponse.livelihood?.programLivelihoodId?.let {
                    coreLivelihoodRepositoryImpl.saveLivelihoodItemListToDB(
                        products,
                        it
                    )
                }
            }
            livelihoodResponse.events?.let { events ->
                livelihoodResponse.livelihood?.programLivelihoodId?.let {
                    coreLivelihoodRepositoryImpl.saveLivelihoodItemListToDB(
                        events,
                        it
                    )
                }
            }

        }
    }

    override fun getApiEndpoint(): String {
        return SUBPATH_GET_LIVELIHOOD_CONFIG
    }
}