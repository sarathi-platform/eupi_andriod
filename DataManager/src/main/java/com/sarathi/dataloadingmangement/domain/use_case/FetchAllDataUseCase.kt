package com.sarathi.dataloadingmangement.domain.use_case

import android.util.Log
import com.nudge.core.constants.DataLoadingTriggerType
import com.nudge.core.data.repository.BaseApiCallNetworkUseCase
import com.nudge.core.data.repository.IApiCallConfigRepository
import com.nudge.core.data.repository.IApiCallJournalRepository
import com.nudge.core.database.entities.api.ApiCallJournalEntity
import com.nudge.core.database.entities.api.ApiExpression
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
import java.util.UUID
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

    //  @SuppressLint("SuspiciousIndentation")
    suspend fun invoke(
        screenName: String,
        dataLoadingTriggerType: DataLoadingTriggerType,
        moduleName: String,
        customData: Map<String, Any>,
        onComplete: (isSuccess: Boolean, successMsg: String) -> Unit,
        isRefresh: Boolean = true,
        totalNumberOfApi: suspend (screenName: String, moduleName: String, customData: Map<String, Any>, transactionId: String) -> Unit,
        apiPerStatus: suspend (apiName: String, requestPayload: String) -> Unit
    ) {
        val apiCallList =
            apiCallConfigRepository.getApiCallList(screenName, dataLoadingTriggerType.name)
        val transactionId = UUID.randomUUID().toString()
        totalNumberOfApi(
            screenName,
            moduleName,
            customData,
            transactionId
        )
        apiCallList.forEach { apiDetails ->
            try {
                if (apiDetails.expression == null || expressionCheckerForApiCall(
                        apiExpressions = apiDetails.expression,
                        customData = customData
                    )
                ) {
                    apiUseCaseList[apiDetails.apiName]?.invoke(
                        screenName = screenName,
                        triggerType = dataLoadingTriggerType,
                        moduleName = moduleName,
                        transactionId = transactionId,
                        customData = customData
                    )

                    apiPerStatus(
                        apiDetails.apiUrls,
                        generateRequestPacket(
                            customData = customData,
                            apiName = apiDetails.apiName
                        )
                    )
                }
            } catch (e: retrofit2.HttpException) {
                Log.e("API Error", "HttpException occurred: ${e.message()}")
                apiPerStatus(
                    apiUseCaseList[apiDetails.apiName]?.getApiEndpoint().toString(),
                    generateRequestPacket(customData = customData, apiName = apiDetails.apiName)
                )
                return@forEach
            } catch (e: Exception) {
                Log.e("API Error", "Unexpected error occurred: ${e.message}")
                apiPerStatus(
                    apiUseCaseList[apiDetails.apiName]?.getApiEndpoint().toString(),
                    generateRequestPacket(customData = customData, apiName = apiDetails.apiName)
                )
                return@forEach
            }
        }
        onComplete(true, BLANK_STRING)
    }

    private fun generateRequestPacket(apiName: String, customData: Map<String, Any>): String {
        return when (apiName) {
            "SUB_PATH_REGISTRY_SERVICE_PROPERTY" -> customData.json()
            else -> "{}"
        }
    }

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
                triggerType = dataLoadingTriggerType,
                moduleName = moduleName,
                transactionId = "",
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

    suspend fun getApiInProgressCount(
        screenName: String,
        moduleName: String,
        customData: Map<String, Any>,
        triggerPoint: String
    ): Int {
        val apiConfigs = apiCallConfigRepository.getApiCallConfigForScreenAndModule(
            screenName = screenName,
            moduleName = moduleName,
            triggerPoint = triggerPoint
        )
        return apiConfigs.count {
            it.expression == null || expressionCheckerForApiCall(
                apiExpressions = it.expression,
                customData = customData
            )
        }
    }

    private suspend fun expressionCheckerForApiCall(
        apiExpressions: List<ApiExpression>?,
        customData: Map<String, Any>
    ): Boolean {
        val missionId = customData["MissionId"] as? Int ?: -1
        val activityTypes =
            fetchMissionActivityDetailDataUseCase.getActivityTypesForMission(missionId)
        return apiExpressions?.any { expression ->
            expression.condition == "activityTypes" &&
                    expression.activityType.any { activityName ->
                        activityTypes.contains(
                            activityName
                        )
                    }
        } ?: true
    }

}


