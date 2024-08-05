package com.nudge.incomeexpensemodule.ui.screens.dataTab.viewModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.nudge.core.enums.SubTabs
import com.nudge.incomeexpensemodule.ui.screens.dataTab.domain.useCase.DataTabUseCase
import com.sarathi.dataloadingmangement.data.entities.SubjectEntity
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.dataloadingmangement.util.event.LoaderEvent
import com.sarathi.dataloadingmangement.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DataTabScreenViewModel @Inject constructor(
    private val dataTabUseCase: DataTabUseCase
) : BaseViewModel() {

    val filters: List<String> =
        mutableListOf("All") //TODO make a method to fetch this in local language and create this filters list
    val countMap: MutableMap<SubTabs, Int> = mutableMapOf()

    val isFilterApplied = mutableStateOf(true)

    val _subjectList: MutableState<List<SubjectEntity>> = mutableStateOf(mutableListOf())
    val subjectList: State<List<SubjectEntity>> get() = _subjectList

    private val _filteredSubjectList: MutableState<List<SubjectEntity>> =
        mutableStateOf(mutableListOf())
    val filteredSubjectList: State<List<SubjectEntity>> get() = _filteredSubjectList

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
                if (isSuccess)
                    initDataTab()
                else
                    onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
    }

    private fun initDataTab() {
        ioViewModelScope {
            _subjectList.value = dataTabUseCase.fetchDidiDetailsFromDbUseCase.invoke()
            _filteredSubjectList.value = subjectList.value

            countMap.put(SubTabs.All, filteredSubjectList.value.size)
            withContext(mainDispatcher) {
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
    }

}
