package com.sarathi.smallgroupmodule.ui.didiTab.domain.use_case

import com.nudge.core.CoreDispatchers
import com.nudge.core.constants.DataLoadingTriggerType
import com.nudge.core.data.repository.BaseApiCallNetworkUseCase
import com.nudge.core.data.repository.IApiCallConfigRepository
import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.domain.use_case.ContentDownloaderUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.FetchDidiDetailsFromDbUseCase
import com.sarathi.dataloadingmangement.domain.use_case.smallGroup.FetchDidiDetailsFromNetworkUseCase
import com.sarathi.dataloadingmangement.domain.use_case.smallGroup.FetchSmallGroupAttendanceHistoryFromNetworkUseCase
import com.sarathi.dataloadingmangement.domain.use_case.smallGroup.FetchSmallGroupFromNetworkUseCase
import javax.inject.Inject

class DidiTabUseCase @Inject constructor(
    private val coreSharedPrefs: CoreSharedPrefs,
    val fetchDidiDetailsFromDbUseCase: FetchDidiDetailsFromDbUseCase,
    val fetchSmallGroupListsFromDbUseCase: FetchSmallGroupListsFromDbUseCase,
    val fetchDidiDetailsFromNetworkUseCase: FetchDidiDetailsFromNetworkUseCase,
    val fetchSmallGroupFromNetworkUseCase: FetchSmallGroupFromNetworkUseCase,
    val fetchSmallGroupAttendanceHistoryFromNetworkUseCase: FetchSmallGroupAttendanceHistoryFromNetworkUseCase,
    val contentDownloaderUseCase: ContentDownloaderUseCase,
    val apiCallConfigRepository: IApiCallConfigRepository,
) {
    val apiUseCaseList: Map<String, BaseApiCallNetworkUseCase> = mapOf(
//        "SUBPATH_GET_LIVELIHOOD_CONFIG" to livelihoodUseCase,
    )

    suspend fun invoke(
        screenName: String,
        dataLoadingTriggerType: DataLoadingTriggerType,
        onComplete: (isSuccess: Boolean, successMsg: String) -> Unit,
        isRefresh: Boolean = true,
        moduleName: String
    ) {
        apiCallConfigRepository.getApiCallList(screenName, dataLoadingTriggerType.name).forEach {
            apiUseCaseList[it.apiName]?.invoke(
                screenName = screenName,
                triggerType = dataLoadingTriggerType,
                customData = mapOf(),
                moduleName = moduleName
            )
        }
        //Todo handle commented code
//        try {
//            if (isRefresh || !coreSharedPrefs.isDidiTabDataLoaded()) {
//                fetchDidiDetailsFromNetworkUseCase.invoke()
//                fetchSmallGroupFromNetworkUseCase.invoke()
//                val smallGroupList = fetchSmallGroupListsFromDbUseCase.invoke()
//                delay(100)
//                smallGroupList.forEach {
//                    fetchSmallGroupAttendanceHistoryFromNetwork(it.smallGroupId)
//                }
//                coreSharedPrefs.setDidiTabDataLoaded(true)
//                withContext(CoreDispatchers.mainDispatcher) {
//                    onComplete(true, BLANK_STRING)
//                }
////                TODO getDidiUrls and uncomment the code.
////                contentDownloaderUseCase.didiImagesForSmallGroupDownload()
//            } else {
//                withContext(CoreDispatchers.mainDispatcher) {
//                    onComplete(true, BLANK_STRING)
//                }
//
//            }
//        } catch (ex: Exception) {
//            CoreLogger.e(
//                tag = "DidiTabUseCase",
//                msg = "invoke: exception -> ${ex.message}",
//                ex = ex,
//                stackTrace = true
//            )
//            withContext(CoreDispatchers.mainDispatcher) {
//                onComplete(true, BLANK_STRING)
//            }
//        }

    }

    private fun fetchSmallGroupAttendanceHistoryFromNetwork(smallGroupId: Int) {
        CoreDispatchers.ioCoroutineScope {
            //Todo handle commented code
            //  fetchSmallGroupAttendanceHistoryFromNetworkUseCase.invoke(smallGroupId)
        }
    }

    suspend fun isApiStatusFailed(): Boolean {
        return fetchDidiDetailsFromNetworkUseCase.isFetchDidiDetailsAPIStatusFailed()
    }

}