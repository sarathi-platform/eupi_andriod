package com.sarathi.dataloadingmangement.domain.use_case

import com.nudge.core.constants.DataLoadingTriggerType
import com.nudge.core.data.repository.BaseApiCallNetworkUseCase
import com.nudge.core.data.repository.IApiCallConfigRepository
import com.nudge.core.data.repository.IApiCallJournalRepository
import com.nudge.core.database.entities.api.ApiCallConfigEntity
import com.nudge.core.database.entities.api.ApiCallJournalEntity
import com.nudge.core.enums.ActivityTypeEnum
import com.nudge.core.json
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.usecase.FetchAppConfigFromNetworkUseCase
import com.nudge.core.usecase.caste.FetchCasteConfigNetworkUseCase
import com.nudge.core.usecase.language.LanguageConfigUseCase
import com.nudge.core.usecase.translation.FetchTranslationConfigUseCase
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.domain.FetchMissionDataUseCase
import com.sarathi.dataloadingmangement.domain.use_case.income_expense.FetchLivelihoodSaveEventUseCase
import com.sarathi.dataloadingmangement.domain.use_case.income_expense.FetchSubjectIncomeExpenseSummaryUseCase
import com.sarathi.dataloadingmangement.domain.use_case.income_expense.FetchSubjectLivelihoodEventHistoryUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.FetchAssetJournalUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.FetchDidiDetailsFromDbUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.FetchDidiDetailsWithLivelihoodMappingUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.FetchLivelihoodOptionNetworkUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.LivelihoodUseCase
import com.sarathi.dataloadingmangement.domain.use_case.smallGroup.FetchDidiDetailsFromNetworkUseCase
import com.sarathi.dataloadingmangement.domain.use_case.smallGroup.FetchSmallGroupAttendanceHistoryFromNetworkUseCase
import com.sarathi.dataloadingmangement.domain.use_case.smallGroup.FetchSmallGroupFromNetworkUseCase
import com.sarathi.dataloadingmangement.domain.use_case.smallGroup.FetchSmallGroupListsFromDbUseCase
import com.sarathi.dataloadingmangement.model.uiModel.ActivityInfoUIModel
import com.sarathi.dataloadingmangement.model.uiModel.MissionInfoUIModel
import com.sarathi.dataloadingmangement.network.SUBPATH_FETCH_LIVELIHOOD_OPTION
import com.sarathi.dataloadingmangement.network.SUBPATH_GET_ATTENDANCE_HISTORY_FROM_NETWORK
import com.sarathi.dataloadingmangement.network.SUBPATH_GET_FORM_DETAILS
import com.sarathi.dataloadingmangement.network.SUBPATH_GET_LIVELIHOOD_CONFIG
import com.sarathi.dataloadingmangement.network.SUBPATH_GET_MONEY_JOURNAL_DETAILS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

class FetchAllDataUseCase @Inject constructor(
    val fetchMissionActivityDetailDataUseCase: FetchMissionActivityDetailDataUseCase,
    val fetchMissionDataUseCase: FetchMissionDataUseCase,
    val fetchContentDataFromNetworkUseCase: FetchContentDataFromNetworkUseCase,
    val fetchSurveyDataFromNetworkUseCase: FetchSurveyDataFromNetworkUseCase,
    val contentDownloaderUseCase: ContentDownloaderUseCase,
    val fetchUserDetailUseCase: FetchUserDetailUseCase,
    val fetchSurveyAnswerFromNetworkUseCase: FetchSurveyAnswerFromNetworkUseCase,
    val formUseCase: FormUseCase,
    val moneyJournalUseCase: FetchMoneyJournalUseCase,
    val livelihoodUseCase: LivelihoodUseCase,
    val fetchLivelihoodOptionNetworkUseCase: FetchLivelihoodOptionNetworkUseCase,
    val fetchAppConfigFromNetworkUseCase: FetchAppConfigFromNetworkUseCase,
    val fetchSectionStatusFromNetworkUsecase: FetchSectionStatusFromNetworkUsecase,
    val fetchTranslationConfigUseCase: FetchTranslationConfigUseCase,
    val languageConfigUseCase: LanguageConfigUseCase,
    val fetchCasteConfigNetworkUseCase: FetchCasteConfigNetworkUseCase,
    val apiCallConfigRepository: IApiCallConfigRepository,
    val apiCallJournalRepository: IApiCallJournalRepository,
    private val coreSharedPrefs: CoreSharedPrefs,

    val fetchDidiDetailsFromNetworkUseCase: FetchDidiDetailsFromNetworkUseCase,
    val fetchDidiDetailsWithLivelihoodMappingUseCase: FetchDidiDetailsWithLivelihoodMappingUseCase,
    val fetchSubjectIncomeExpenseSummaryUseCase: FetchSubjectIncomeExpenseSummaryUseCase,
    val fetchSubjectLivelihoodEventHistoryUseCase: FetchSubjectLivelihoodEventHistoryUseCase,
    val assetJournalUseCase: FetchAssetJournalUseCase,
    val fetchLivelihoodSaveEventUseCase: FetchLivelihoodSaveEventUseCase,


    val fetchDidiDetailsFromDbUseCase: FetchDidiDetailsFromDbUseCase,
    val fetchSmallGroupListsFromDbUseCase: FetchSmallGroupListsFromDbUseCase,
    val fetchSmallGroupFromNetworkUseCase: FetchSmallGroupFromNetworkUseCase,
    val fetchSmallGroupAttendanceHistoryFromNetworkUseCase: FetchSmallGroupAttendanceHistoryFromNetworkUseCase,
) {

    val apiUseCaseList: Map<String, BaseApiCallNetworkUseCase> = mapOf(
        //Mission Screen ->
        "SUBPATH_USER_VIEW" to fetchUserDetailUseCase,
        "SUB_PATH_GET_MISSION_DETAILS" to fetchMissionDataUseCase,
        "SUBPATH_GET_LIVELIHOOD_CONFIG" to livelihoodUseCase,
        "SUB_PATH_CONTENT_MANAGER" to fetchContentDataFromNetworkUseCase,
        "SUB_PATH_GET_V3_CONFIG_LANGUAGE" to languageConfigUseCase,
        "SUB_PATH_GET_CONFIG_CASTE" to fetchCasteConfigNetworkUseCase,
        "SUB_PATH_REGISTRY_SERVICE_PROPERTY" to fetchAppConfigFromNetworkUseCase,
        "SUB_PATH_FETCH_TRANSLATIONS" to fetchTranslationConfigUseCase,

        //Data Tab screen
        "SUBPATH_GET_DIDI_LIST" to fetchDidiDetailsFromNetworkUseCase,
        "SUBPATH_GET_LIVELIHOOD_SAVE_EVENT" to fetchLivelihoodSaveEventUseCase,
        "SUBPATH_FETCH_LIVELIHOOD_OPTION" to fetchLivelihoodOptionNetworkUseCase,
        "SUBPATH_GET_ASSETS_JOURNAL_DETAILS" to assetJournalUseCase,
        "SUBPATH_GET_MONEY_JOURNAL_DETAILS" to moneyJournalUseCase,
        //  "SUBPATH_GET_LIVELIHOOD_CONFIG" to livelihoodUseCase,

        //Didi Tab Screen
        //  "SUBPATH_GET_DIDI_LIST" to fetchDidiDetailsFromNetworkUseCase,
        "SUBPATH_GET_SMALL_GROUP_MAPPING" to fetchSmallGroupFromNetworkUseCase,
        "SUBPATH_GET_ATTENDANCE_HISTORY_FROM_NETWORK" to fetchSmallGroupAttendanceHistoryFromNetworkUseCase,


        //Activity Screen
        "SUB_PATH_GET_ACTIVITY_DETAILS" to fetchMissionActivityDetailDataUseCase,
        "SUBPATH_FETCH_SURVEY_FROM_NETWORK" to fetchSurveyDataFromNetworkUseCase,
        "GET_SECTION_STATUS" to fetchSectionStatusFromNetworkUsecase,
        "SUBPATH_SURVEY_ANSWERS" to fetchSurveyAnswerFromNetworkUseCase,
        // "SUBPATH_FETCH_LIVELIHOOD_OPTION" to fetchLivelihoodOptionNetworkUseCase,
        // "SUBPATH_GET_LIVELIHOOD_CONFIG" to livelihoodUseCase,
        // "SUB_PATH_CONTENT_MANAGER" to fetchContentDataFromNetworkUseCase,
    )

    suspend fun invoke(
        screenName: String,
        dataLoadingTriggerType: DataLoadingTriggerType,
        moduleName: String,
        customData: Map<String, Any>,
        onComplete: (isSuccess: Boolean, successMsg: String) -> Unit,
        isRefresh: Boolean = true,
        totalNumberOfApi: (screenName: String, screenTotalApi: Int) -> Unit,
        apiPerStatus: suspend (apiName: String, requestPayload: String) -> Unit
    ) {
        val apiCallList =
            apiCallConfigRepository.getApiCallList(screenName, dataLoadingTriggerType.name)
        val nonLoopingApiCount = apiCallList.filter { it.isLoopingCall != true }.size
        totalNumberOfApi(screenName, nonLoopingApiCount)

        // Handle special cases for DidiTabScreen
        if (screenName.equals("DidiTabScreen", true)) {
            handleDidiTabScreen(
                screenName,
                dataLoadingTriggerType,
                customData,
                moduleName,
                apiCallList,
                apiPerStatus,
                totalNumberOfApi
            )
        }
        // Handle special cases for ActivityScreen
        else if (screenName.equals("ActivityScreen", true)) {
            handleActivityScreen(
                screenName,
                dataLoadingTriggerType,
                customData,
                moduleName,
                apiCallList,
                apiPerStatus
            )
        }
        // Handle general cases
        else {
            apiCallList.forEach { apiDetails ->
                apiUseCaseList[apiDetails.apiName]?.invoke(
                    screenName = screenName,
                    triggerType = dataLoadingTriggerType,
                    customData = customData,
                    moduleName = moduleName
                )
                apiPerStatus(
                    apiDetails.apiUrls,
                    generateRequestPacket(customData = customData, apiName = apiDetails.apiName)
                )
            }
        }

        // Launch background tasks
        launchContentDownloadTasks(screenName)

        onComplete(true, BLANK_STRING)
    }

    private fun generateRequestPacket(apiName: String, customData: Map<String, Any>): String {
        return when (apiName) {
            "SUB_PATH_REGISTRY_SERVICE_PROPERTY" -> customData.json()
            else -> "{}"
        }
    }


    private suspend fun handleDidiTabScreen(
        screenName: String,
        dataLoadingTriggerType: DataLoadingTriggerType,
        customData: Map<String, Any>,
        moduleName: String,
        apiCallList: List<ApiCallConfigEntity>,
        apiPerStatus: suspend (apiName: String, requestPayload: String) -> Unit,
        totalNumberOfApi: (screenName: String, screenTotalApi: Int) -> Unit,
    ) {
        val totalApiCount = apiCallList.filter { it.isLoopingCall != true }.size
        totalNumberOfApi(screenName, totalApiCount)

        apiCallList.forEach { apiDetails ->
            if (apiUseCaseList[apiDetails.apiName]?.getApiEndpoint()
                    ?.equals(SUBPATH_GET_ATTENDANCE_HISTORY_FROM_NETWORK) == true
            ) {
                val smallGroupList = fetchSmallGroupListsFromDbUseCase.invoke()
                totalNumberOfApi(
                    screenName,
                    apiCallList.filter { it.isLoopingCall != true }.size + smallGroupList.size
                )
                smallGroupList.forEach {
                    apiUseCaseList[apiDetails.apiName]?.invoke(
                        screenName = screenName,
                        triggerType = dataLoadingTriggerType,
                        customData = mapOf("smallGroupId" to it.smallGroupId),
                        moduleName = moduleName
                    )
                    apiPerStatus(
                        apiDetails.apiUrls,
                        mapOf("smallGroupId" to it.smallGroupId).json()
                    )
                }
            } else {
                apiUseCaseList[apiDetails.apiName]?.invoke(
                    screenName = screenName,
                    triggerType = dataLoadingTriggerType,
                    customData = customData,
                    moduleName = moduleName
                )
                apiPerStatus(
                    apiDetails.apiUrls,
                    generateRequestPacket(customData = customData, apiName = apiDetails.apiName)
                )
            }
        }
    }

    private suspend fun handleActivityScreen(
        screenName: String,
        dataLoadingTriggerType: DataLoadingTriggerType,
        customData: Map<String, Any>,
        moduleName: String,
        apiCallList: List<ApiCallConfigEntity>,
        apiPerStatus: suspend (apiName: String, requestPayload: String) -> Unit
    ) {
        val missionId = customData["MissionId"] as Int
        val activityTypes =
            fetchMissionActivityDetailDataUseCase.getActivityTypesForMission(missionId)

        apiCallList.forEach { apiDetails ->
            when {
                apiDetails.apiName.isRelevantForLivelihoodOptions(activityTypes) -> {
                    apiUseCaseList[apiDetails.apiName]?.invoke(
                        screenName = screenName,
                        triggerType = dataLoadingTriggerType,
                        customData = customData,
                        moduleName = moduleName
                    )
                }

                apiDetails.apiName.isRelevantForGrantOrLivelihoodPoP(activityTypes) -> {
                    apiUseCaseList[apiDetails.apiName]?.invoke(
                        screenName = screenName,
                        triggerType = dataLoadingTriggerType,
                        customData = customData,
                        moduleName = moduleName
                    )
                }

                else -> {
                    apiUseCaseList[apiDetails.apiName]?.invoke(
                        screenName = screenName,
                        triggerType = dataLoadingTriggerType,
                        customData = customData,
                        moduleName = moduleName
                    )
                }
            }
            apiPerStatus(apiDetails.apiUrls, customData.json())
        }
    }

    private fun String.isRelevantForLivelihoodOptions(activityTypes: List<String>): Boolean {
        return this.equals(SUBPATH_FETCH_LIVELIHOOD_OPTION, true) ||
                this.equals(SUBPATH_GET_LIVELIHOOD_CONFIG, true) ||
                this.equals(SUBPATH_GET_FORM_DETAILS, true)
    }

    private fun String.isRelevantForGrantOrLivelihoodPoP(activityTypes: List<String>): Boolean {
        return activityTypes.contains(ActivityTypeEnum.GRANT.name.lowercase(Locale.ENGLISH)) ||
                activityTypes.contains(ActivityTypeEnum.LIVELIHOOD_PoP.name.lowercase(Locale.ENGLISH)) ||
                this.equals(SUBPATH_GET_FORM_DETAILS, true) ||
                this.equals(SUBPATH_GET_MONEY_JOURNAL_DETAILS, true)
    }

    private fun launchContentDownloadTasks(screenName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            contentDownloaderUseCase.contentDownloader()
            contentDownloaderUseCase.livelihoodContentDownload()
            if (screenName.equals("ActivityScreen", true)) {
                contentDownloaderUseCase.surveyRelateContentDownlaod()
            }
        }
    }


//    suspend fun invoke(
//        screenName: String,
//        dataLoadingTriggerType: DataLoadingTriggerType,
//        moduleName: String,
//        customData: Map<String, Any>,
//        onComplete: (isSuccess: Boolean, successMsg: String) -> Unit,
//        isRefresh: Boolean = true,
//        totalNumberOfApi: (screenName: String, screenTotalApi: Int) -> Unit,
//        apiPerStatus: suspend (apiName: String, requestPayload: String) -> Unit
//    ) {
//        totalNumberOfApi(
//            screenName,
//            apiCallConfigRepository.getApiCallList(screenName, dataLoadingTriggerType.name)
//                .filter { it.isLoopingCall != true }.size
//        )
//        //api config list using order  and then check with apiUseCaseList
//        apiCallConfigRepository.getApiCallList(screenName, dataLoadingTriggerType.name)
//            .forEach { apiDetails ->
//                if (screenName.equals(
//                        "DidiTabScreen",
//                        true
//                    ) && apiUseCaseList[apiDetails.apiName]?.getApiEndpoint()
//                        ?.equals(SUBPATH_GET_ATTENDANCE_HISTORY_FROM_NETWORK) == true
//                ) {
//                    val smallGroupList = fetchSmallGroupListsFromDbUseCase.invoke()
//                    totalNumberOfApi(
//                        screenName,
//                        apiCallConfigRepository.getApiCallList(
//                            screenName,
//                            dataLoadingTriggerType.name
//                        ).filter { it.isLoopingCall != true }.size + smallGroupList.size
//                    )
//                    smallGroupList.forEach {
//                        apiUseCaseList[apiDetails.apiName]?.invoke(
//                            screenName = screenName,
//                            triggerType = dataLoadingTriggerType,
//                            customData = mapOf("smallGroupId" to it.smallGroupId),
//                            moduleName = moduleName
//                        )
//                        apiPerStatus(
//                            apiDetails.apiUrls,
//                            mapOf("smallGroupId" to it.smallGroupId).json()
//                        )
//                    }
//                } else if (screenName.equals(
//                        "ActivityScreen",
//                        true)
//                                && (apiUseCaseList[apiDetails.apiName]?.getApiEndpoint()
//                            ?.equals(SUBPATH_FETCH_LIVELIHOOD_OPTION) == true) || apiUseCaseList[apiDetails.apiName]?.getApiEndpoint()
//                            ?.equals(SUBPATH_GET_LIVELIHOOD_CONFIG) == true ||
//                                apiUseCaseList[apiDetails.apiName]?.getApiEndpoint()
//                                    ?.equals(SUBPATH_GET_FORM_DETAILS) == true
//
//                ) {
//                    val misisonId=customData.get("MissionId") as Int
//                    val activityTypes =
//                        fetchMissionActivityDetailDataUseCase.getActivityTypesForMission(misisonId)
//                    if (activityTypes.contains(ActivityTypeEnum.LIVELIHOOD.name.lowercase(Locale.ENGLISH))) {
//                        if (apiUseCaseList[apiDetails.apiName]?.getApiEndpoint()
//                                ?.equals(SUBPATH_FETCH_LIVELIHOOD_OPTION) == true
//                        ) {
//                            apiUseCaseList[apiDetails.apiName]?.invoke(
//                                screenName = screenName,
//                                triggerType = dataLoadingTriggerType,
//                                customData = customData,
//                                moduleName = moduleName
//                            )
//                        } else if (apiUseCaseList[apiDetails.apiName]?.getApiEndpoint()
//                                ?.equals(SUBPATH_GET_LIVELIHOOD_CONFIG) == true
//                        ) {
//                            apiUseCaseList[apiDetails.apiName]?.invoke(
//                                screenName = screenName,
//                                triggerType = dataLoadingTriggerType,
//                                customData = customData,
//                                moduleName = moduleName
//                            )
//                        }
//                    } else if (activityTypes.contains(ActivityTypeEnum.GRANT.name.lowercase(Locale.ENGLISH))
//                        || activityTypes.contains(
//                            ActivityTypeEnum.LIVELIHOOD_PoP.name.lowercase(
//                                Locale.ENGLISH
//                            )
//                        )
//                    ) {
//                        if (apiUseCaseList[apiDetails.apiName]?.getApiEndpoint()
//                                ?.equals(SUBPATH_GET_FORM_DETAILS) == true
//                        ) {
//                            apiUseCaseList[apiDetails.apiName]?.invoke(
//                                screenName = screenName,
//                                triggerType = dataLoadingTriggerType,
//                                customData = customData,
//                                moduleName = moduleName
//                            )
//                        } else if (apiUseCaseList[apiDetails.apiName]?.getApiEndpoint()
//                                ?.equals(SUBPATH_GET_MONEY_JOURNAL_DETAILS) == true
//                        ) {
//                            apiUseCaseList[apiDetails.apiName]?.invoke(
//                                screenName = screenName,
//                                triggerType = dataLoadingTriggerType,
//                                customData = customData,
//                                moduleName = moduleName)
//                        }
//                    }
//                } else {
//                    apiUseCaseList[apiDetails.apiName]?.invoke(
//                        screenName = screenName,
//                        triggerType = dataLoadingTriggerType,
//                        customData =customData,
//                        moduleName = moduleName
//                    )
//                }
//                apiPerStatus(
//                    apiDetails.apiUrls,
//                    customData.json()
//                )
//            }
//        CoroutineScope(Dispatchers.IO).launch {
//            contentDownloaderUseCase.contentDownloader()
//            contentDownloaderUseCase.livelihoodContentDownload()
//            if (screenName.equals("ActivityScreen")) {
//                contentDownloaderUseCase.surveyRelateContentDownlaod()
//            }
//        }
//
//        onComplete(true, BLANK_STRING)
//
////        fetchUserDetailUseCase.invoke()
////
////        if (isRefresh || !coreSharedPrefs.isDataLoaded()) {
////            fetchMissionDataUseCase.getAllMissionList()
//// //           livelihoodUseCase.invoke()
////            contentDownloaderUseCase.livelihoodContentDownload()
////            fetchContentDataFromNetworkUseCase.invoke()
////            languageConfigUseCase.invoke()
////            fetchCasteConfigNetworkUseCase.invoke()
////            if (!isRefresh) {
////                fetchAppConfigFromNetworkUseCase.invoke()
////            }
////            coreSharedPrefs.setDataLoaded(true)
////            onComplete(true, BLANK_STRING)
////            CoroutineScope(Dispatchers.IO).launch {
////                contentDownloaderUseCase.contentDownloader()
////            }
////            fetchTranslationConfigUseCase.invoke()
////
////        } else {
////            onComplete(true, BLANK_STRING)
////        }
//        // onComplete(true, BLANK_STRING)
//    }


    suspend fun fetchMissionRelatedData(
        missionId: Int,
        programId: Int,
        screenName: String,
        moduleName: String,
        dataLoadingTriggerType: DataLoadingTriggerType,
        isRefresh: Boolean,
        onComplete: (isSuccess: Boolean, successMsg: String) -> Unit,
    ) {
        apiCallConfigRepository.getApiCallList(screenName, dataLoadingTriggerType.name).forEach {


            apiUseCaseList[it.apiName]?.invoke(
                screenName = screenName,
                moduleName = moduleName,
                triggerType = dataLoadingTriggerType,
                customData = mapOf("MissionId" to missionId, "ProgramId" to programId),
            )
        }

//        if (isRefresh || fetchMissionDataUseCase.isMissionLoaded(
//                missionId = missionId,
//                programId
//            ) == 0
//        ) {
//
//            fetchMissionDataUseCase.invoke(missionId, programId)
//            fetchSurveyDataFromNetworkUseCase.invoke(missionId)
//            fetchSectionStatusFromNetworkUsecase.invoke(missionId)
//            val activityTypes = fetchMissionDataUseCase.getActivityTypesForMission(missionId)
//            if (!isRefresh) {
//                fetchSurveyAnswerFromNetworkUseCase.invoke(missionId)
//                if (activityTypes.contains(ActivityTypeEnum.LIVELIHOOD.name.lowercase(Locale.ENGLISH))) {
//
//                    fetchLivelihoodOptionNetworkUseCase.invoke()
//                }
//                if (activityTypes.contains(ActivityTypeEnum.GRANT.name.lowercase(Locale.ENGLISH))
//                    || activityTypes.contains(ActivityTypeEnum.LIVELIHOOD_PoP.name.lowercase(Locale.ENGLISH))
//                ) {
//                    formUseCase.invoke(missionId)
//                    moneyJournalUseCase.invoke()
//                }
//            }
//
//            if (activityTypes.contains(ActivityTypeEnum.LIVELIHOOD.name.lowercase(Locale.ENGLISH))) {
//
//                livelihoodUseCase.invoke()
//            }
//            fetchContentDataFromNetworkUseCase.invoke()
//            CoroutineScope(Dispatchers.IO).launch {
//                contentDownloaderUseCase.contentDownloader()
//                contentDownloaderUseCase.surveyRelateContentDownlaod()
//
//            }
//            fetchMissionDataUseCase.setMissionLoaded(missionId = missionId, programId)
//            onComplete(true, BLANK_STRING)
//
//        } else {
//            onComplete(true, BLANK_STRING)
//
//        }
        onComplete(true, BLANK_STRING)
    }


    fun getStateId() = coreSharedPrefs.getStateId()

    suspend fun fetchMissionInfo(missionId: Int): MissionInfoUIModel {
        return fetchMissionDataUseCase.fetchMissionInfo(missionId)
            ?: MissionInfoUIModel.getDefaultValue()
    }

    suspend fun fetchActivityInfo(missionId: Int, activityId: Int): ActivityInfoUIModel? {
        return fetchMissionActivityDetailDataUseCase.fetchActivityInfo(missionId, activityId)
    }

    fun getAiCallNeed(): Boolean {
        return false
    }

    suspend fun getApiStatus(
        screenName: String,
        moduleName: String,
        apiUrl: String,
        requestPayload: String
    ): ApiCallJournalEntity? {
        return apiCallJournalRepository.getApiCallStatus(
            screenName = screenName,
            moduleName = moduleName,
            apiUrl = apiUrl,
            requestPayload = requestPayload

        )
    }

}


