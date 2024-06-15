package com.sarathi.missionactivitytask.ui.disbursement_summary_screen.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.sarathi.dataloadingmangement.domain.use_case.FormUseCase
import com.sarathi.dataloadingmangement.model.uiModel.DisbursementFormSummaryUiModel
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
class DisbursementFormSummaryScreenViewModel @Inject constructor(private val formUseCase: FormUseCase) :
    BaseViewModel() {
    private val _disbursementFormList =
        mutableStateOf<List<DisbursementFormSummaryUiModel>>(emptyList())
    val disbursementFormList: State<List<DisbursementFormSummaryUiModel>> get() = _disbursementFormList
    override fun <T> onEvent(event: T) {
        when (event) {
            is InitDataEvent.InitDataState -> {
                initDisbursementSummaryScreen()
            }

            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(
                    isLoaderVisible = event.showLoader
                )
            }
        }
    }

    private fun initDisbursementSummaryScreen() {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            _disbursementFormList.value = formUseCase.getFormData()
            withContext(Dispatchers.Main) {
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
    }

}