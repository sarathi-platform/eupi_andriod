package com.patsurvey.nudge.activities.ui.progress

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.patsurvey.nudge.activities.ui.progress.domain.useCase.ChangeUserUseCase
import com.patsurvey.nudge.activities.ui.progress.domain.useCase.FetchCrpDataUseCase
import com.patsurvey.nudge.activities.ui.progress.domain.useCase.PreferenceProviderUseCase
import com.patsurvey.nudge.activities.ui.progress.domain.useCase.SelectionVillageUseCase
import com.patsurvey.nudge.activities.ui.progress.events.SelectionEvents
import com.patsurvey.nudge.base.BaseViewModelV2
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.missionactivitytask.utils.event.LoaderEvent
import com.sarathi.missionactivitytask.utils.event.SearchEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class VillageSelectionViewModelV2 @Inject constructor(
    private val fetchCrpDataUseCase: FetchCrpDataUseCase,
    private val changeUserUseCase: ChangeUserUseCase,
    private val selectionVillageUseCase: SelectionVillageUseCase,
    val preferenceProviderUseCase: PreferenceProviderUseCase
) : BaseViewModelV2(fetchSelectionUserUseCase = fetchCrpDataUseCase) {

    private val _villagList = MutableStateFlow(listOf<VillageEntity>())
    val villageList: StateFlow<List<VillageEntity>> get() = _villagList

    val villageSelected = mutableStateOf(0)
    val stateId = mutableStateOf(1)
    val showUserChangedDialog = mutableStateOf(false)

    var isFromOTPScreen: Boolean = false
    val isVoEndorsementComplete = mutableStateOf(mutableMapOf<Int, Boolean>())
    var _filterVillageList = MutableStateFlow(listOf<VillageEntity>())
    val filterVillageList: StateFlow<List<VillageEntity>> get() = _filterVillageList

    override fun <T> onEvent(event: T) {
        super.onEvent(event)

        when (event) {
            is InitDataEvent.InitDataState -> {
                onEvent(LoaderEvent.UpdateLoaderState(true))
                val isSameUser = fetchCrpDataUseCase.compareWithPreviousUser()
                if (isSameUser)
                    loadAllData(isRefresh = false)
                else
                    showUserChangedDialog.value = true
            }

            is InitDataEvent.InitChangeUserState -> {
                loadDataForNewUser()
            }

            is SelectionEvents.DownloadQuestionImages -> {
                event.questionImageList.forEach { imageLink ->

                // TODO Download question image using content manager
                }
            }

            is SelectionEvents.UpdateSelectedVillage -> {
                villageSelected.value = event.selectedIndex
                selectionVillageUseCase.setSelectedVillage(filterVillageList.value[villageSelected.value])
            }

            is SearchEvent.PerformSearch -> {
                _filterVillageList.value = if (event.searchTerm.isNotEmpty()) {
                    val filteredList = ArrayList<VillageEntity>()
                    villageList.value.forEach { village ->
                        if (village.name.lowercase().contains(event.searchTerm.lowercase())) {
                            filteredList.add(village)
                        }
                    }
                    filteredList
                } else {
                    villageList.value
                }
            }

            is SelectionEvents.Logout -> {
                changeUserUseCase.logout()
            }
        }

    }

    private fun loadDataForNewUser() {
        onEvent(LoaderEvent.UpdateLoaderState(true))
        viewModelScope.launch(ioDispatcher + exceptionHandler) {
            changeUserUseCase.invoke() {
                loadAllData()
            }
        }
    }

    private fun loadAllData(isRefresh: Boolean = false) {
        viewModelScope.launch(ioDispatcher + exceptionHandler) {
            fetchCrpDataUseCase.invoke(isRefresh = isRefresh,
                onComplete = {
                    initVillageSelectionScreen()
                    onEvent(SelectionEvents.GetStateId { result ->
                        stateId.value = result
                    })
                }
            )

        }

    }

    private fun initVillageSelectionScreen() {
        ioViewModelScope {
            _villagList.value = selectionVillageUseCase.getVillageListFromDb()
            _filterVillageList.value = villageList.value
            withContext(mainDispatcher) {
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
    }

    override fun refreshData() {
        super.refreshData()
        loadAllData(true)
    }

    override fun onServerError(error: ErrorModel?) {
        TODO("Not yet implemented")
    }

    override fun onServerError(errorModel: ErrorModelWithApi?) {
        TODO("Not yet implemented")
    }


}