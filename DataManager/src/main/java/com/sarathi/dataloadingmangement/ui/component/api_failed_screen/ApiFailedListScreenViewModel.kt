package com.sarathi.dataloadingmangement.ui.component.api_failed_screen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.nudge.core.data.repository.IApiCallJournalRepository
import com.nudge.core.database.entities.api.ApiCallJournalEntity
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.dataloadingmangement.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApiFailedListScreenViewModel @Inject constructor(val apiCallJournalRepository: IApiCallJournalRepository) :
    BaseViewModel() {
    private val _apiFailedList =
        mutableStateOf<List<ApiCallJournalEntity>>(emptyList())
    val apiFailedList: State<List<ApiCallJournalEntity>> get() = _apiFailedList
    override fun <T> onEvent(event: T) {
        when (event) {
            is InitDataEvent.InitApiFailedScreenState -> {
                loadAllData(event.screenName, event.moduleName)
            }
        }
    }

    private fun loadAllData(screenName: String, moduleName: String) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            _apiFailedList.value = apiCallJournalRepository.getFailedApiCallJournalEntity(
                screenName = screenName,
                moduleName = moduleName
            ) ?: listOf()
        }

    }
}