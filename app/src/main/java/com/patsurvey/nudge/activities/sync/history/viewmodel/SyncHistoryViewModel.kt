package com.patsurvey.nudge.activities.sync.history.viewmodel

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.nudge.core.CoreDispatchers
import com.nudge.core.EventSyncStatus
import com.nudge.core.database.entities.Events
import com.nudge.core.isDataEvent
import com.nudge.core.isImageEvent
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.sync.history.domain.use_case.SyncHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SyncHistoryViewModel @Inject constructor(
        private val syncHistoryUseCase: SyncHistoryUseCase
):ViewModel() {
        private val _eventList = mutableStateOf<List<Events>>(emptyList())
        val eventList: State<List<Events>> get() = _eventList
        val _countDataList = mutableStateOf<List<Pair<String, Int>>>(emptyList())
        val countDataList: State<List<Pair<String, Int>>> get() = _countDataList

        val _countImageList = mutableStateOf<List<Pair<String, Int>>>(emptyList())
        val countImageList: State<List<Pair<String, Int>>> get() = _countImageList
        val eventStatusImageUIList = arrayListOf<Pair<String, Int>>()
        val eventStatusDataUIList = arrayListOf<Pair<String, Int>>()

        fun getAllEventStatusForUser(context: Context) {
                CoroutineScope(CoreDispatchers.ioDispatcher).launch {
                        _eventList.value =
                                syncHistoryUseCase.getSyncHistoryUseCase.getAllEventsForUser()

                        val allDataEvents = eventList.value.filter { isDataEvent(it) }
                        val allImageEvents = eventList.value.filter { isImageEvent(it) }

                        eventStatusDataUIList.add(
                                Pair(
                                        context.getString(R.string.producer_success_event_count),
                                        allDataEvents.filter { it.status == EventSyncStatus.OPEN.eventSyncStatus }.size
                                )
                        )

                        eventStatusDataUIList.add(
                                Pair(
                                        context.getString(R.string.producer_success_event_count),
                                        allDataEvents.filter { it.status == EventSyncStatus.PRODUCER_FAILED.eventSyncStatus }.size
                                )
                        )


                        eventStatusDataUIList.add(
                                Pair(
                                        context.getString(R.string.producer_success_event_count),
                                        allDataEvents.filter { it.status == EventSyncStatus.PRODUCER_SUCCESS.eventSyncStatus }.size
                                )
                        )

                        eventStatusDataUIList.add(
                                Pair(
                                        context.getString(R.string.producer_success_event_count),
                                        allDataEvents.filter { it.status == EventSyncStatus.CONSUMER_FAILED.eventSyncStatus }.size
                                )
                        )

                        eventStatusDataUIList.add(
                                Pair(
                                        context.getString(R.string.producer_success_event_count),
                                        allDataEvents.filter { it.status == EventSyncStatus.CONSUMER_SUCCESS.eventSyncStatus }.size
                                )
                        )

                        eventStatusDataUIList.add(
                                Pair(
                                        context.getString(R.string.producer_success_event_count),
                                        allDataEvents.filter { it.status == EventSyncStatus.OPEN.eventSyncStatus }.size
                                )
                        )

                        eventStatusDataUIList.add(
                                Pair(
                                        context.getString(R.string.producer_success_event_count),
                                        allDataEvents.filter { it.status == EventSyncStatus.PRODUCER_FAILED.eventSyncStatus }.size
                                )
                        )


                        eventStatusDataUIList.add(
                                Pair(
                                        context.getString(R.string.producer_success_event_count),
                                        allDataEvents.filter { it.status == EventSyncStatus.PRODUCER_SUCCESS.eventSyncStatus }.size
                                )
                        )

                        eventStatusDataUIList.add(
                                Pair(
                                        context.getString(R.string.producer_success_event_count),
                                        allDataEvents.filter { it.status == EventSyncStatus.CONSUMER_FAILED.eventSyncStatus }.size
                                )
                        )

                        eventStatusDataUIList.add(
                                Pair(
                                        context.getString(R.string.producer_success_event_count),
                                        allDataEvents.filter { it.status == EventSyncStatus.CONSUMER_SUCCESS.eventSyncStatus }.size
                                )
                        )

                }
        }
}