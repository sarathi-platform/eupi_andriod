package com.sarathi.dataloadingmangement.domain.use_case

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.BLANK_STRING
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
            if (!isRefresh) {
                fetchSurveyAnswerFromNetworkUseCase.invoke(missionId)
                formUseCase.invoke(missionId)
                if (missionId == 2 || missionId == 3) {
                    moneyJournalUseCase.invoke()
                }
            }
            CoroutineScope(Dispatchers.IO).launch {
                contentDownloaderUseCase.surveyRelateContentDownlaod()
            }
            coreSharedPrefs.setMissionDataLoaded(
                isDataLoaded = true,
                missionId = missionId,
                programId = programId
            )
            fetchMissionDataUseCase.setMissionLoaded(missionId = missionId, programId)
            onComplete(true, BLANK_STRING)

        } else {
            onComplete(true, BLANK_STRING)

        }

    }


}