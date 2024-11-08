package com.patsurvey.nudge.activities.ui.progress

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.viewModelScope
import com.nudge.core.utils.CoreLogger
import com.nudge.core.value
import com.patsurvey.nudge.activities.ui.progress.domain.repository.impls.StepNameEnum
import com.patsurvey.nudge.activities.ui.progress.domain.useCase.FetchCrpDataUseCase
import com.patsurvey.nudge.activities.ui.progress.domain.useCase.FetchProgressScreenDataUseCase
import com.patsurvey.nudge.activities.ui.progress.domain.useCase.FetchVillageDataFromNetworkUseCase
import com.patsurvey.nudge.activities.ui.progress.domain.useCase.PreferenceProviderUseCase
import com.patsurvey.nudge.activities.ui.progress.domain.useCase.SelectionVillageUseCase
import com.patsurvey.nudge.activities.ui.progress.events.SelectionEvents
import com.patsurvey.nudge.base.BaseViewModelV2
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.StepListEntity
import com.patsurvey.nudge.database.TolaEntity
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.sarathi.dataloadingmangement.NUMBER_ZERO
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.missionactivitytask.utils.event.LoaderEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProgressScreenViewModelV2 @Inject constructor(
    private val fetchCrpDataUseCase: FetchCrpDataUseCase,
    private val selectionVillageUseCase: SelectionVillageUseCase,
    private val fetchVillageDataFromNetworkUseCase: FetchVillageDataFromNetworkUseCase,
    private val fetchProgressScreenDataUseCase: FetchProgressScreenDataUseCase,
    val preferenceProviderUseCase: PreferenceProviderUseCase
) : BaseViewModelV2(
    fetchCrpDataUseCase,
) {

    private val LOGGER_TAG = ProgressScreenViewModelV2::class.java.simpleName

    private val _stepsList = mutableStateListOf<StepListEntity>()
    val stepList: SnapshotStateList<StepListEntity> get() = _stepsList

    private val _villagList = mutableStateListOf<VillageEntity>()
    val villageList: SnapshotStateList<VillageEntity> get() = _villagList

    val selectedVillageId = mutableStateOf(0)
    val villageSelectionDropDownTitle = mutableStateOf("Select Village")

    private val _tolaList = MutableStateFlow(listOf<TolaEntity>())
    val tolaList: StateFlow<List<TolaEntity>> get() = _tolaList
    private val _didiList = MutableStateFlow(listOf<DidiEntity>())
    val didiList: StateFlow<List<DidiEntity>> get() = _didiList

    val villageSelected = mutableStateOf(0)
    private val _stepSummaryForVillage = mutableStateMapOf<String, Int>()
    val stepSummaryForVillage get() = _stepSummaryForVillage

    /*val stepSelected = mutableStateOf(0)

    val tolaCount = mutableStateOf(0)
    val didiCount = mutableStateOf(0)
    val poorDidiCount = mutableStateOf(0)
    val ultrPoorDidiCount = mutableStateOf(0)
    val endorsedDidiCount = mutableStateOf(0)*/

    val isVoEndorsementComplete = mutableStateOf(mutableMapOf<Int, Boolean>())

    override fun <T> onEvent(event: T) {
        super.onEvent(event)
        when (event) {
            is InitDataEvent.InitDataState -> {
                onEvent(LoaderEvent.UpdateLoaderState(true))
                loadAllData()
            }

            is SelectionEvents.UpdateSelectedVillage -> {
                onEvent(LoaderEvent.UpdateLoaderState(true))
                villageSelected.value = event.selectedIndex
                with(villageList[villageSelected.value]) {
                    selectedVillageId.value = this.id
                    villageSelectionDropDownTitle.value = this.name
                    selectionVillageUseCase.setSelectedVillage(this)
                }
                loadAllData()
            }
        }
    }

    private fun loadAllData() {
        viewModelScope.launch(ioDispatcher + exceptionHandler) {
            val village = selectionVillageUseCase.getSelectedVillageFromDb()
                ?: selectionVillageUseCase.getSelectedVillage()

            village.let {
                selectedVillageId.value = it.id
                villageSelectionDropDownTitle.value = it.name
            }

            fetchVillageDataFromNetworkUseCase.invoke(
                villageId = selectedVillageId.value,
                onComplete = {
                    initProgressScreen()
                }
            )
        }
    }

    private fun initProgressScreen() {
        ioViewModelScope {

            with(selectionVillageUseCase.getVillageListFromDb()) {
                _villagList.clear()
                _villagList.addAll(this)
            }

            _stepsList.clear()
            _stepsList.addAll(fetchProgressScreenDataUseCase.invoke(villageId = selectedVillageId.value))

            _stepSummaryForVillage.clear()
            _stepSummaryForVillage.putAll(
                fetchProgressScreenDataUseCase.getStepSummaryForVillage(
                    selectedVillageId.value
                )
            )

            withContext(mainDispatcher) {
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
    }


    override fun onServerError(error: ErrorModel?) {
        CoreLogger.e(tag = LOGGER_TAG, msg = "onServerError -> Error: ${error?.message.value()}")
        onEvent(LoaderEvent.UpdateLoaderState(false))
    }

    override fun onServerError(errorModel: ErrorModelWithApi?) {
        CoreLogger.e(
            tag = LOGGER_TAG,
            msg = "onServerError -> Error: ${errorModel?.message.value()}, api: ${errorModel?.apiName}"
        )
        onEvent(LoaderEvent.UpdateLoaderState(false))
    }

    fun getSubTitleCount(orderNumber: Int): Int {
        return stepSummaryForVillage.get(StepNameEnum.getStepNameForOrderNumber(orderNumber)?.name.value())
            .value(NUMBER_ZERO)
    }
}