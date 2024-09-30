package com.sarathi.smallgroupmodule.ui.didiTab.domain.use_case

import com.nudge.core.BLANK_STRING
import com.nudge.core.CoreDispatchers
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.utils.CoreLogger
import com.sarathi.dataloadingmangement.domain.use_case.ContentDownloaderUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.FetchDidiDetailsFromDbUseCase
import com.sarathi.dataloadingmangement.domain.use_case.smallGroup.FetchDidiDetailsFromNetworkUseCase
import com.sarathi.dataloadingmangement.domain.use_case.smallGroup.FetchSmallGroupAttendanceHistoryFromNetworkUseCase
import com.sarathi.dataloadingmangement.domain.use_case.smallGroup.FetchSmallGroupFromNetworkUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DidiTabUseCase @Inject constructor(
    private val coreSharedPrefs: CoreSharedPrefs,
    val fetchDidiDetailsFromDbUseCase: FetchDidiDetailsFromDbUseCase,
    val fetchSmallGroupListsFromDbUseCase: FetchSmallGroupListsFromDbUseCase,
    val fetchDidiDetailsFromNetworkUseCase: FetchDidiDetailsFromNetworkUseCase,
    val fetchSmallGroupFromNetworkUseCase: FetchSmallGroupFromNetworkUseCase,
    val fetchSmallGroupAttendanceHistoryFromNetworkUseCase: FetchSmallGroupAttendanceHistoryFromNetworkUseCase,
    val contentDownloaderUseCase: ContentDownloaderUseCase,
) {

    suspend operator fun invoke(
        isRefresh: Boolean = true,
        onComplete: (isSuccess: Boolean, successMsg: String) -> Unit,
    ) {

        try {
            if (isRefresh || !coreSharedPrefs.isDidiTabDataLoaded()) {
                fetchDidiDetailsFromNetworkUseCase.invoke()
                fetchSmallGroupFromNetworkUseCase.invoke()
                val smallGroupList = fetchSmallGroupListsFromDbUseCase.invoke()
                delay(100)
                smallGroupList.forEach {
                    fetchSmallGroupAttendanceHistoryFromNetwork(it.smallGroupId)
                }
                coreSharedPrefs.setDidiTabDataLoaded(true)
                withContext(CoreDispatchers.mainDispatcher) {
                    onComplete(true, BLANK_STRING)
                }
//                TODO getDidiUrls and uncomment the code.
//                contentDownloaderUseCase.didiImagesForSmallGroupDownload()
            } else {
                withContext(CoreDispatchers.mainDispatcher) {
                    onComplete(true, BLANK_STRING)
                }

            }
        } catch (ex: Exception) {
            CoreLogger.e(
                tag = "DidiTabUseCase",
                msg = "invoke: exception -> ${ex.message}",
                ex = ex,
                stackTrace = true
            )
            withContext(CoreDispatchers.mainDispatcher) {
                onComplete(true, BLANK_STRING)
            }
        }

    }

    private fun fetchSmallGroupAttendanceHistoryFromNetwork(smallGroupId: Int) {
        CoreDispatchers.ioCoroutineScope {
            fetchSmallGroupAttendanceHistoryFromNetworkUseCase.invoke(smallGroupId)
        }
    }

    suspend fun isApiStatusFailed(): Boolean {
        return fetchDidiDetailsFromNetworkUseCase.isFetchDidiDetailsAPIStatusFailed()
    }

}