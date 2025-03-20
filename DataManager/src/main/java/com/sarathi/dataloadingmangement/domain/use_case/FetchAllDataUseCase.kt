package com.sarathi.dataloadingmangement.domain.use_case

import com.nudge.core.enums.ActivityTypeEnum
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale
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
    private val coreSharedPrefs: CoreSharedPrefs
) {


    suspend fun invoke(
        onApiCallCompleted: () -> Unit = {},
        onApiCallFailure: () -> Unit = {},
        onComplete: (isSuccess: Boolean, successMsg: String) -> Unit,
        isRefresh: Boolean = true
    ) {
        suspend fun performApiCall(call: suspend () -> Boolean): Boolean {
            return try {
                call.invoke().also {
                    if (it) onApiCallCompleted() else onApiCallFailure()
                }
            } catch (exception: Exception) {
                onApiCallFailure()
                false
            }
        }
        performApiCall { fetchUserDetailUseCase.invoke() }
        if (isRefresh || !coreSharedPrefs.isDataLoaded()) {
            performApiCall { fetchMissionDataUseCase.getAllMissionList() }
            performApiCall { livelihoodUseCase.invoke() }
            contentDownloaderUseCase.livelihoodContentDownload()
            performApiCall { fetchContentDataFromNetworkUseCase.invoke() }
            performApiCall { languageConfigUseCase.invoke() }
            performApiCall { fetchCasteConfigNetworkUseCase.invoke() }
            if (!isRefresh) {
                performApiCall { fetchAppConfigFromNetworkUseCase.invoke() }
            }
            coreSharedPrefs.setDataLoaded(true)
            onComplete(true, BLANK_STRING)
            CoroutineScope(Dispatchers.IO).launch {
                contentDownloaderUseCase.contentDownloader()
            }
            fetchTranslationConfigUseCase.invoke()

        } else {
            onComplete(true, BLANK_STRING)
        }
    }

    suspend fun fetchMissionRelatedData(
        missionId: Int,
        programId: Int,
        isRefresh: Boolean,
        onComplete: (isSuccess: Boolean, successMsg: String) -> Unit,
    ) {
        if (isRefresh || fetchMissionDataUseCase.isMissionLoaded(
                missionId = missionId,
                programId
            ) == 0
        ) {

            fetchMissionDataUseCase.invoke(missionId, programId)
            fetchSurveyDataFromNetworkUseCase.invoke(missionId)
            fetchSectionStatusFromNetworkUsecase.invoke(missionId)
            val activityTypes = fetchMissionDataUseCase.getActivityTypesForMission(missionId)
            if (!isRefresh) {
                fetchSurveyAnswerFromNetworkUseCase.invoke(missionId)
                if (activityTypes.contains(ActivityTypeEnum.LIVELIHOOD.name.lowercase(Locale.ENGLISH))) {

                    fetchLivelihoodOptionNetworkUseCase.invoke()
                }
                if (activityTypes.contains(ActivityTypeEnum.GRANT.name.lowercase(Locale.ENGLISH))
                    || activityTypes.contains(ActivityTypeEnum.LIVELIHOOD_PoP.name.lowercase(Locale.ENGLISH))
                ) {
                    formUseCase.invoke(missionId)
                    moneyJournalUseCase.invoke()
                }
            }

            if (activityTypes.contains(ActivityTypeEnum.LIVELIHOOD.name.lowercase(Locale.ENGLISH))) {

                livelihoodUseCase.invoke()
            }
            fetchContentDataFromNetworkUseCase.invoke()
            CoroutineScope(Dispatchers.IO).launch {
                contentDownloaderUseCase.contentDownloader()
                contentDownloaderUseCase.surveyRelateContentDownlaod()

            }
            fetchMissionDataUseCase.setMissionLoaded(missionId = missionId, programId)
            onComplete(true, BLANK_STRING)

        } else {
            onComplete(true, BLANK_STRING)

        }

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

