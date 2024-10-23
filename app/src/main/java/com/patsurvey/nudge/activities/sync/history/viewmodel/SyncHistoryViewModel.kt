package com.patsurvey.nudge.activities.sync.history.viewmodel

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.nudge.core.CoreDispatchers
import com.nudge.core.EventSyncStatus
import com.nudge.core.database.entities.Events
import com.nudge.core.isDataEvent
import com.nudge.core.isImageEvent
import com.patsurvey.nudge.activities.sync.history.domain.use_case.SyncHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SyncHistoryViewModel @Inject constructor(
        val syncHistoryUseCase: SyncHistoryUseCase
):ViewModel() {
        private val _eventList = mutableStateOf<List<Events>>(emptyList())
        val eventList: State<List<Events>> get() = _eventList
        val _countDataList = mutableStateOf<List<Pair<String, Int>>>(emptyList())
        val countDataList: State<List<Pair<String, Int>>> get() = _countDataList
        val eventStatusImageUIList = arrayListOf<Pair<String, Int>>()
        val eventStatusDataUIList = arrayListOf<Pair<String, Int>>()
        val totalDataEventCount = mutableStateOf(0)
        val totalImageEventCount = mutableStateOf(0)
        val lastSyncTime = mutableLongStateOf(0L)

        fun getAllEventStatusForUser(context: Context) {
                CoroutineScope(CoreDispatchers.ioDispatcher).launch {
                        _eventList.value =
                                syncHistoryUseCase.getSyncHistoryUseCase.getAllEventsForUser()
                        lastSyncTime.longValue =
                                syncHistoryUseCase.getSyncHistoryUseCase.getLastSyncTime()
                        val allDataEvents = eventList.value.filter { isDataEvent(it) }
                        val allImageEvents = eventList.value.filter { isImageEvent(it) }
                        totalDataEventCount.value = allDataEvents.size
                        totalImageEventCount.value = allImageEvents.size
                        eventStatusDataUIList.add(
                                Pair(
                                        EventSyncStatus.OPEN.eventSyncStatus,
                                        allDataEvents.filter { it.status == EventSyncStatus.OPEN.eventSyncStatus }.size
                                )
                        )

                        eventStatusDataUIList.add(
                                Pair(
                                        EventSyncStatus.PRODUCER_FAILED.eventSyncStatus,
                                        allDataEvents.filter { it.status == EventSyncStatus.PRODUCER_FAILED.eventSyncStatus }.size
                                )
                        )


                        eventStatusDataUIList.add(
                                Pair(
                                        EventSyncStatus.PRODUCER_SUCCESS.eventSyncStatus,
                                        allDataEvents.filter { it.status == EventSyncStatus.PRODUCER_SUCCESS.eventSyncStatus }.size
                                )
                        )

                        eventStatusDataUIList.add(
                                Pair(
                                        EventSyncStatus.CONSUMER_FAILED.eventSyncStatus,
                                        allDataEvents.filter { it.status == EventSyncStatus.CONSUMER_FAILED.eventSyncStatus }.size
                                )
                        )

                        eventStatusDataUIList.add(
                                Pair(
                                        EventSyncStatus.CONSUMER_SUCCESS.eventSyncStatus,
                                        allDataEvents.filter { it.status == EventSyncStatus.CONSUMER_SUCCESS.eventSyncStatus }.size
                                )
                        )

                        eventStatusImageUIList.add(
                                Pair(
                                        EventSyncStatus.OPEN.eventSyncStatus,
                                        allImageEvents.filter { it.status == EventSyncStatus.OPEN.eventSyncStatus }.size
                                )
                        )

                        eventStatusImageUIList.add(
                                Pair(
                                        EventSyncStatus.PRODUCER_FAILED.eventSyncStatus,
                                        allImageEvents.filter { it.status == EventSyncStatus.PRODUCER_FAILED.eventSyncStatus }.size
                                )
                        )


                        eventStatusImageUIList.add(
                                Pair(
                                        EventSyncStatus.PRODUCER_SUCCESS.eventSyncStatus,
                                        allImageEvents.filter { it.status == EventSyncStatus.PRODUCER_SUCCESS.eventSyncStatus }.size
                                )
                        )

                        eventStatusImageUIList.add(
                                Pair(
                                        EventSyncStatus.CONSUMER_FAILED.eventSyncStatus,
                                        allImageEvents.filter { it.status == EventSyncStatus.CONSUMER_FAILED.eventSyncStatus }.size
                                )
                        )

                        eventStatusImageUIList.add(
                                Pair(
                                        EventSyncStatus.CONSUMER_SUCCESS.eventSyncStatus,
                                        allImageEvents.filter { it.status == EventSyncStatus.CONSUMER_SUCCESS.eventSyncStatus }.size
                                )
                        )
                        eventStatusImageUIList.add(
                                Pair(
                                        EventSyncStatus.IMAGE_NOT_EXIST.eventSyncStatus,
                                        allImageEvents.filter { it.status == EventSyncStatus.IMAGE_NOT_EXIST.eventSyncStatus }.size
                                )
                        )

                        _countDataList.value = eventStatusDataUIList
                }
        }
}