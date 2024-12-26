package com.sarathi.dataloadingmangement.domain.use_case

import com.nudge.core.enums.ActivityTypeEnum
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.usecase.FetchAppConfigFromNetworkUseCase
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.FetchLivelihoodOptionNetworkUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.LivelihoodUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
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
    val fetchSectionStatusFromNetworkUsecase: FetchSectionStatusFromNetworkUsecase,
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
            fetchContentDataFromNetworkUseCase.invoke()
            if (!isRefresh) {
                fetchAppConfigFromNetworkUseCase.invoke()
            }
            coreSharedPrefs.setDataLoaded(true)
            onComplete(true, BLANK_STRING)
            CoroutineScope(Dispatchers.IO).launch {
                contentDownloaderUseCase.contentDownloader()
            }

        } else {
            onComplete(true, BLANK_STRING)
        }
    }

//   suspend  fun fetchMissionRelatedData(
//        missionId: Int,
//        programId: Int,
//        isRefresh: Boolean,
//        onComplete: (isSuccess: Boolean, successMsg: String) -> Unit,
//    ) {
//             if (isRefresh || fetchMissionDataUseCase.isMissionLoaded(
//                     missionId = missionId,
//                     programId
//                 ) == 0
//             ) {
//
//            fetchMissionDataUseCase.invoke(missionId, programId)
//            fetchSurveyDataFromNetworkUseCase.invoke(missionId)
//            fetchSectionStatusFromNetworkUsecase.invoke(missionId)
//            val activityTypes = fetchMissionDataUseCase.getActivityTypesForMission(missionId)
//            fetchContentDataFromNetworkUseCase.invoke()
//            if (!isRefresh) {
//                fetchSurveyAnswerFromNetworkUseCase.invoke(missionId)
//                if (activityTypes.contains(ActivityTypeEnum.LIVELIHOOD.name.lowercase(Locale.ENGLISH))) {
//
//                         fetchLivelihoodOptionNetworkUseCase.invoke()
//                     }
//                     if (activityTypes.contains(ActivityTypeEnum.GRANT.name.lowercase(Locale.ENGLISH))) {
//                         formUseCase.invoke(missionId)
//                         moneyJournalUseCase.invoke()
//                     }
//                 }
//
//                 if (activityTypes.contains(ActivityTypeEnum.LIVELIHOOD.name.lowercase(Locale.ENGLISH))) {
//
//                     livelihoodUseCase.invoke()
//                 }
//
//                 CoroutineScope(Dispatchers.IO).launch {
//                     contentDownloaderUseCase.contentDownloader()
//                     contentDownloaderUseCase.surveyRelateContentDownlaod()
//                 }
//                 fetchMissionDataUseCase.setMissionLoaded(missionId = missionId, programId)
//                 onComplete(true, BLANK_STRING)
//
//             } else {
//                 onComplete(true, BLANK_STRING)
//
//             }
//
//         }


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
            val activityTypes = fetchMissionDataUseCase.getActivityTypesForMission(missionId)

            coroutineScope {
                val fetchSurveyData = async { fetchSurveyDataFromNetworkUseCase.invoke(missionId) }
                val fetchSectionStatus =
                    async { fetchSectionStatusFromNetworkUsecase.invoke(missionId) }
                val fetchContentData = async { fetchContentDataFromNetworkUseCase.invoke() }
                val livelihoodConfigData =
                    if (activityTypes.contains(ActivityTypeEnum.LIVELIHOOD.name.lowercase(Locale.ENGLISH))) {
                        async { livelihoodUseCase.invoke() }
                    } else null
                fetchSurveyData.await()
                fetchSectionStatus.await()
                fetchContentData.await()
                livelihoodConfigData?.await()
            }

            if (!isRefresh) {
                coroutineScope {
                    val fetchSurveyAnswer =
                        async { fetchSurveyAnswerFromNetworkUseCase.invoke(missionId) }
                    val fetchLivelihoodOption =
                        if (activityTypes.contains(ActivityTypeEnum.LIVELIHOOD.name.lowercase(Locale.ENGLISH))) {
                            async { fetchLivelihoodOptionNetworkUseCase.invoke() }
                        } else null
                    val fetchGrantData =
                        if (activityTypes.contains(ActivityTypeEnum.GRANT.name.lowercase(Locale.ENGLISH))) {
                            async {
                                formUseCase.invoke(missionId)
                                moneyJournalUseCase.invoke()
                            }
                        } else null

                    fetchSurveyAnswer.await()
                    fetchLivelihoodOption?.await()
                    fetchGrantData?.await()
                }
            }



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
}

