package com.patsurvey.nudge.activities.sync.history.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.nudge.core.database.entities.EventStatusEntity
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
        val _countList = mutableStateOf<List<Pair<String, Int>>>(emptyList())
        val countList: State<List<Pair<String,Int>>> get() = _countList


        fun getAllEventsBetweenDates(startDate: Long, endDate: Long) {
                CoroutineScope(Dispatchers.IO).launch {
                        _eventList.value =
                                syncHistoryUseCase.getSyncHistoryUseCase.getAllEventsBetweenDates(
                                        startDate = startDate.toString(),
                                        endDate = endDate.toString()
                                )
                }
        }
}