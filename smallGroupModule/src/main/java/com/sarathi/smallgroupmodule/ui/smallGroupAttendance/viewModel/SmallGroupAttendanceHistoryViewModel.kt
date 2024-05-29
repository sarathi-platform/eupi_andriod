package com.sarathi.smallgroupmodule.ui.smallGroupAttendance.viewModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.sarathi.dataloadingmangement.model.uiModel.SmallGroupSubTabUiModel
import com.sarathi.dataloadingmangement.viewmodel.BaseViewModel
import com.sarathi.smallgroupmodule.ui.smallGroupAttendance.domain.useCase.SmallGroupAttendanceHistoryUseCase
import com.sarathi.smallgroupmodule.ui.smallGroupAttendance.presentation.event.SmallGroupAttendanceHistoryEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SmallGroupAttendanceHistoryViewModel @Inject constructor(
    private val smallGroupAttendanceHistoryUseCase: SmallGroupAttendanceHistoryUseCase
) : BaseViewModel() {

    private val _smallGroupDetails: MutableState<SmallGroupSubTabUiModel> =
        mutableStateOf(SmallGroupSubTabUiModel.getEmptyModel())
    val smallGroupDetails: State<SmallGroupSubTabUiModel> get() = _smallGroupDetails

    override fun <T> onEvent(event: T) {
        when (event) {
            is SmallGroupAttendanceHistoryEvent.LoadSmallGroupDetailsForSmallGroupIdEvent -> {

                viewModelScope.launch(Dispatchers.IO) {

                    val details =
                        smallGroupAttendanceHistoryUseCase.fetchSmallGroupDetailsFromDbUseCase.invoke(
                            event.smallGroupId
                        )
                    withContext(Dispatchers.Main) {
                        _smallGroupDetails.value = details
                    }

                }
            }
        }
    }

}
