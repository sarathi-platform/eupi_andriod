package com.sarathi.dataloadingmangement.domain.use_case

import com.nudge.core.enums.ActivityTypeEnum
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.usecase.FetchAppConfigFromNetworkUseCase
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.FetchLivelihoodOptionNetworkUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.LivelihoodUseCase
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
    val fetchLanguageUseCase: FetchLanguageUseCase,
    val fetchUserDetailUseCase: FetchUserDetailUseCase,
    val fetchSurveyAnswerFromNetworkUseCase: FetchSurveyAnswerFromNetworkUseCase,
    val formUseCase: FormUseCase,
    val moneyJournalUseCase: FetchMoneyJournalUseCase,
    val livelihoodUseCase: LivelihoodUseCase,
    val fetchLivelihoodOptionNetworkUseCase: FetchLivelihoodOptionNetworkUseCase,
    val fetchAppConfigFromNetworkUseCase: FetchAppConfigFromNetworkUseCase,
    private val coreSharedPrefs: CoreSharedPrefs
) {


    suspend fun invoke(
        onComplete: (isSuccess: Boolean, successMsg: String) -> Unit,
        isRefresh: Boolean = true
    ) {
        if (isRefresh || !coreSharedPrefs.isDataLoaded()) {
            fetchLanguageUseCase.invoke()
            fetchUserDetailUseCase.invoke()
            fetchMissionDataUseCase.getAllMissionList()
            fetchAppConfigFromNetworkUseCase.invoke()
            fetchContentDataFromNetworkUseCase.invoke()

            coreSharedPrefs.setDataLoaded(true)
            onComplete(true, BLANK_STRING)
            CoroutineScope(Dispatchers.IO).launch {
                contentDownloaderUseCase.contentDownloader()
            }

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
            val activityTypes = fetchMissionDataUseCase.getActivityTypesForMission(missionId)
            if (!isRefresh) {
                fetchSurveyAnswerFromNetworkUseCase.invoke(missionId)
                if (activityTypes.contains(ActivityTypeEnum.LIVELIHOOD.name.lowercase(Locale.ENGLISH))) {

                    fetchLivelihoodOptionNetworkUseCase.invoke()
                }
                if (activityTypes.contains(ActivityTypeEnum.GRANT.name.lowercase(Locale.ENGLISH))) {
                    formUseCase.invoke(missionId)
                    moneyJournalUseCase.invoke()
                }
            }
            if (activityTypes.contains(ActivityTypeEnum.LIVELIHOOD.name.lowercase(Locale.ENGLISH))) {

                livelihoodUseCase.invoke()
            }

            CoroutineScope(Dispatchers.IO).launch {
                contentDownloaderUseCase.surveyRelateContentDownlaod()
            }
            fetchMissionDataUseCase.setMissionLoaded(missionId = missionId, programId)
            onComplete(true, BLANK_STRING)

        } else {
            onComplete(true, BLANK_STRING)

        }

    }


    fun getStateId() = coreSharedPrefs.getStateId()
}

