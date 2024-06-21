package com.patsurvey.nudge.activities.sync.history.viewmodel

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.nudge.core.EventSyncStatus
import com.nudge.core.database.entities.EventStatusEntity
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.sync.history.domain.use_case.SyncHistoryUseCase
import com.patsurvey.nudge.utils.BLANK_STRING
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SyncHistoryViewModel @Inject constructor(
        private val syncHistoryUseCase: SyncHistoryUseCase
):ViewModel() {
        val startDate = mutableStateOf(BLANK_STRING)
        val endDate = mutableStateOf(BLANK_STRING)
        val isDateSelected = mutableStateOf(false)
        private val _eventList = mutableStateOf<List<EventStatusEntity>>(emptyList())
        val eventList: State<List<EventStatusEntity>> get() = _eventList
        private val _countList = mutableStateOf<List<Pair<String,Int>>>(emptyList())
        val countList: State<List<Pair<String,Int>>> get() = _countList


        fun getAllEventsBetweenDates(context: Context,startDate:Long,endDate:Long){
                CoroutineScope(Dispatchers.IO).launch {
                        _eventList.value =
                                syncHistoryUseCase.getSyncHistoryUseCase.getAllEventsBetweenDates(
                                        startDate = startDate.toString(),
                                        endDate = endDate.toString()
                                )
                        val tempList= arrayListOf<Pair<String,Int>>()
                        val successConsumerCount = eventList.value.filter { it.status == EventSyncStatus.CONSUMER_SUCCESS.eventSyncStatus }.size
                        if(successConsumerCount>1){
                           tempList.add(Pair(context.getString(R.string.consumer_success_event_count),successConsumerCount))
                        }

                        val successProducerCount = eventList.value.filter { it.status == EventSyncStatus.PRODUCER_SUCCESS.eventSyncStatus }.size
                        if(successProducerCount>1){
                                tempList.add(Pair(context.getString(R.string.producer_success_event_count),successProducerCount))
                        }

                        val inProgressConsumerCount = eventList.value.filter { it.status == EventSyncStatus.CONSUMER_IN_PROGRESS.eventSyncStatus }.size
                        if(inProgressConsumerCount>1){
                                tempList.add(Pair(context.getString(R.string.consumer_inprogress_event_count),inProgressConsumerCount))
                        }

                        val inProgressProducerCount = eventList.value.filter { it.status == EventSyncStatus.PRODUCER_IN_PROGRESS.eventSyncStatus }.size
                        if(inProgressProducerCount>1){
                                tempList.add(Pair(context.getString(R.string.producer_inprogress_event_count),inProgressProducerCount))
                        }

                        val failedConsumerCount = eventList.value.filter { it.status == EventSyncStatus.CONSUMER_FAILED.eventSyncStatus }.size
                        if(failedConsumerCount>1){
                                tempList.add(Pair(context.getString(R.string.consumer_failed_event_count),failedConsumerCount))
                        }

                        val failedProducerCount = eventList.value.filter { it.status == EventSyncStatus.PRODUCER_FAILED.eventSyncStatus }.size
                        if(failedProducerCount>1){
                                tempList.add(Pair(context.getString(R.string.producer_failed_event_count),failedProducerCount))
                        }
                        _countList.value=tempList

                }
        }
}