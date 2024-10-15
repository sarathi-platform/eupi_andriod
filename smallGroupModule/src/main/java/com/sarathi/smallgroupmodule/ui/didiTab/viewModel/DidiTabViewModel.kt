package com.sarathi.smallgroupmodule.ui.didiTab.viewModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.nudge.core.enums.SubTabs
import com.nudge.core.ui.events.CommonEvents
import com.sarathi.dataloadingmangement.data.entities.SubjectEntity
import com.sarathi.dataloadingmangement.model.uiModel.SmallGroupSubTabUiModel
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.dataloadingmangement.util.event.LoaderEvent
import com.sarathi.dataloadingmangement.viewmodel.BaseViewModel
import com.sarathi.smallgroupmodule.ui.didiTab.domain.use_case.DidiTabUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DidiTabViewModel @Inject constructor(
    val didiTabUseCase: DidiTabUseCase
) : BaseViewModel() {

    private val _totalDidiCount: MutableState<Int> = mutableStateOf(0)
    val totalCount: State<Int> get() = _totalDidiCount

    private val _totalSmallGroupCount: MutableState<Int> = mutableStateOf(0)
    val totalSmallGroupCount: State<Int> get() = _totalSmallGroupCount

    private val _didiList: MutableState<List<SubjectEntity>> = mutableStateOf(mutableListOf())
    val didiList: State<List<SubjectEntity>> get() = _didiList

    private val _filteredDidiList: MutableState<List<SubjectEntity>> =
        mutableStateOf(mutableListOf())
    val filteredDidiList: State<List<SubjectEntity>> get() = _filteredDidiList

    private val _smallGroupList: MutableState<List<SmallGroupSubTabUiModel>> =
        mutableStateOf(mutableListOf())
    val smallGroupList: State<List<SmallGroupSubTabUiModel>> get() = _smallGroupList

    private val _filteredSmallGroupList: MutableState<List<SmallGroupSubTabUiModel>> =
        mutableStateOf(mutableListOf())
    val filteredSmallGroupList: State<List<SmallGroupSubTabUiModel>> get() = _filteredSmallGroupList

    val countMap = mutableMapOf<SubTabs, Int>()
    val isSubjectApiStatusFailed =
        mutableStateOf(false)



    override fun <T> onEvent(event: T) {
        when (event) {
            is InitDataEvent.InitDataState -> {
                loadAllDataForDidiTab(false)
            }

            is CommonEvents.SearchValueChangedEvent -> {

                when (event.addArgs) {
                    0 -> {
                        searchByDidis(event.searchQuery)
                    }

                    1 -> {
                        searchBySmallGroup(event.searchQuery)
                    }

                    else -> {
                        searchByDidis(event.searchQuery)
                    }
                }

            }
            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(
                    isLoaderVisible = event.showLoader
                )
            }
        }


    }

    private fun loadAllDataForDidiTab(isRefresh: Boolean) {
        onEvent(LoaderEvent.UpdateLoaderState(true))
        ioViewModelScope {
            didiTabUseCase.invoke(isRefresh) { isSuccess, successMsg ->
                if (isSuccess) {
                    initDidiTab()
                } else {
                    onEvent(LoaderEvent.UpdateLoaderState(false))
                }
            }
            isSubjectApiStatusFailed.value = didiTabUseCase.isApiStatusFailed()
        }
    }


    private fun initDidiTab() {
        ioViewModelScope {
            _didiList.value = didiTabUseCase.fetchDidiDetailsFromDbUseCase.invoke()
            _filteredDidiList.value = didiList.value.sortedBy { it.subjectName }
            _smallGroupList.value =
                didiTabUseCase.fetchSmallGroupListsFromDbUseCase.invoke()
            _filteredSmallGroupList.value = smallGroupList.value
            delay(100)
            _totalDidiCount.value = didiList.value.size
            _totalSmallGroupCount.value = smallGroupList.value.size
            countMap.put(SubTabs.DidiTab, totalCount.value)
            countMap.put(SubTabs.SmallGroupTab, totalSmallGroupCount.value)
            withContext(mainDispatcher) {
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
    }

    private fun searchBySmallGroup(searchQuery: String) {
        _filteredSmallGroupList.value = if (searchQuery.isNotEmpty()) {
            val filteredList = ArrayList<SmallGroupSubTabUiModel>()
            smallGroupList.value.forEach { smallGroup ->
                if (smallGroup.smallGroupName.trim().lowercase().contains(
                        searchQuery.trim().lowercase()
                    )
                ) {
                    filteredList.add(smallGroup)
                }
            }
            filteredList
        } else {
            smallGroupList.value
        }
    }

    private fun searchByDidis(searchQuery: String) {
        _filteredDidiList.value = if (searchQuery.isNotEmpty()) {
            val filteredList = ArrayList<SubjectEntity>()
            didiList.value.forEach { subjectEntity ->
                if (subjectEntity.subjectName.trim().lowercase().contains(
                        searchQuery.trim().lowercase()
                    )
                ) {
                    filteredList.add(subjectEntity)
                }
            }
            filteredList.sortedBy { it.subjectName }
        } else {
            didiList.value.sortedBy { it.subjectName }
        }
    }

    override fun refreshData() {
        loadAllDataForDidiTab(true)
    }

}
