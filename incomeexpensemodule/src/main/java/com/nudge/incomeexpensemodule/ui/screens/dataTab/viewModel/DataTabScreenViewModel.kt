package com.nudge.incomeexpensemodule.ui.screens.dataTab.viewModel

import com.nudge.incomeexpensemodule.ui.screens.dataTab.domain.useCase.DadaTabUseCase
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.dataloadingmangement.util.event.LoaderEvent
import com.sarathi.dataloadingmangement.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class DataTabScreenViewModel(
    private val dataTabUseCase: DadaTabUseCase
) : BaseViewModel() {
    override fun <T> onEvent(event: T) {
        when (event) {
            is InitDataEvent.InitDataState -> {
                loadAddDataForDataTab(isRefresh = false)
            }

            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(
                    isLoaderVisible = event.showLoader
                )
            }
        }
    }

    private fun loadAddDataForDataTab(isRefresh: Boolean) {
        onEvent(LoaderEvent.UpdateLoaderState(true))
        ioViewModelScope {
            dataTabUseCase.invoke(isRefresh) { isSuccess, successMsg ->

            }
        }
    }

}
