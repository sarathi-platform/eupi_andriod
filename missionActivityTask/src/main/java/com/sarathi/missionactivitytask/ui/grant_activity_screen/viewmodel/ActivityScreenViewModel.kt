package com.sarathi.missionactivitytask.ui.grant_activity_screen.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.sarathi.missionactivitytask.data.entities.MissionActivityEntity
import com.sarathi.missionactivitytask.ui.grant_activity_screen.domain.usecase.GetActivityUseCase
import com.sarathi.missionactivitytask.utils.event.InitDataEvent
import com.sarathi.missionactivitytask.utils.event.LoaderEvent
import com.sarathi.missionactivitytask.viewmodels.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class ActivityScreenViewModel @Inject constructor(
    private val getActivityUseCase: GetActivityUseCase
) : BaseViewModel() {
    private val _activityList = mutableStateOf<List<MissionActivityEntity>>(emptyList())
    val activityList: State<List<MissionActivityEntity>> get() = _activityList
    override fun <T> onEvent(event: T) {
        when (event) {
            is InitDataEvent.InitDataState -> {
                initActivityScreen()
            }

            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(
                    isLoaderVisible = event.showLoader
                )
            }
        }
    }

    private fun initActivityScreen() {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            _activityList.value = getActivityUseCase.getActivities()
            withContext(Dispatchers.Main) {
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
    }

}