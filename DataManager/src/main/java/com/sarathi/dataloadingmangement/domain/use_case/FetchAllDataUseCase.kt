package com.sarathi.dataloadingmangement.domain.use_case

import com.nudge.core.constants.DataLoadingTriggerType
import com.nudge.core.data.repository.BaseApiCallNetworkUseCase
import com.nudge.core.data.repository.IApiCallConfigRepository
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.usecase.FetchAppConfigFromNetworkUseCase
import com.nudge.core.usecase.caste.FetchCasteConfigNetworkUseCase
import com.nudge.core.usecase.language.LanguageConfigUseCase
import com.nudge.core.usecase.translation.FetchTranslationConfigUseCase
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.FetchLivelihoodOptionNetworkUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.LivelihoodUseCase
import com.sarathi.dataloadingmangement.model.uiModel.ActivityInfoUIModel
import com.sarathi.dataloadingmangement.model.uiModel.MissionInfoUIModel
import javax.inject.Inject

class FetchAllDataUseCase @Inject constructor(
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
    private val coreSharedPrefs: CoreSharedPrefs
) {

    val apiUseCaseList: Map<String, BaseApiCallNetworkUseCase> = mapOf(
        "SUBPATH_USER_VIEW" to fetchUserDetailUseCase,
        "SUB_PATH_GET_MISSION_DETAILS" to fetchMissionDataUseCase,
        "SUBPATH_GET_LIVELIHOOD_CONFIG" to livelihoodUseCase,
        "SUB_PATH_CONTENT_MANAGER" to fetchContentDataFromNetworkUseCase,
        "SUB_PATH_GET_V3_CONFIG_LANGUAGE" to languageConfigUseCase,
        "SUB_PATH_GET_CONFIG_CASTE" to fetchCasteConfigNetworkUseCase,
        "SUB_PATH_REGISTRY_SERVICE_PROPERTY" to fetchAppConfigFromNetworkUseCase,
    )

    suspend fun invoke(
        screenName: String,
        dataLoadingTriggerType: DataLoadingTriggerType,
        onComplete: (isSuccess: Boolean, successMsg: String) -> Unit,
        isRefresh: Boolean = true
    ) {
        //       customData123["propertiesName"] = propertiesName ->>> val propertiesName: List<String> = AppConfigKeysEnum.values().map { it.name } for pass fetchAppConfigFromNetworkUseCase request map
        //api config list using order  and then check with apiUseCaseList
        apiCallConfigRepository.getApiCallList(screenName, dataLoadingTriggerType.name).forEach {
            apiUseCaseList[it.apiName]?.invoke(screenName, dataLoadingTriggerType, mapOf())
        }


//        fetchUserDetailUseCase.invoke()
//
//        if (isRefresh || !coreSharedPrefs.isDataLoaded()) {
//            fetchMissionDataUseCase.getAllMissionList()
// //           livelihoodUseCase.invoke()
//            contentDownloaderUseCase.livelihoodContentDownload()
//            fetchContentDataFromNetworkUseCase.invoke()
//            languageConfigUseCase.invoke()
//            fetchCasteConfigNetworkUseCase.invoke()
//            if (!isRefresh) {
//                fetchAppConfigFromNetworkUseCase.invoke()
//            }
//            coreSharedPrefs.setDataLoaded(true)
//            onComplete(true, BLANK_STRING)
//            CoroutineScope(Dispatchers.IO).launch {
//                contentDownloaderUseCase.contentDownloader()
//            }
//            fetchTranslationConfigUseCase.invoke()
//
//        } else {
//            onComplete(true, BLANK_STRING)
//        }
        onComplete(true, BLANK_STRING)
    }

    suspend fun fetchMissionRelatedData(
        missionId: Int,
        programId: Int,
        screenName: String,
        dataLoadingTriggerType: DataLoadingTriggerType,
        isRefresh: Boolean,
        onComplete: (isSuccess: Boolean, successMsg: String) -> Unit,
    ) {
        apiCallConfigRepository.getApiCallList(screenName, dataLoadingTriggerType.name).forEach {


            apiUseCaseList[it.apiName]?.invoke(
                screenName,
                dataLoadingTriggerType,
                mapOf("MissionId" to missionId, "ProgramId" to programId)
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
        return fetchMissionDataUseCase.fetchActivityInfo(missionId, activityId)
    }
}


