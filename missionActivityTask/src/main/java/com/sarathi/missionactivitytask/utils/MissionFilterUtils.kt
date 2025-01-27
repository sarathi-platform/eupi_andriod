package com.sarathi.missionactivitytask.utils

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.nudge.core.ALL_MISSION_FILTER_VALUE
import com.nudge.core.BLANK_STRING
import com.nudge.core.GENERAL_MISSION_FILTER_VALUE
import com.nudge.core.getFileNameFromURL
import com.nudge.core.helper.TranslationHelper
import com.nudge.core.model.FilterType
import com.nudge.core.model.FilterUiModel
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.value
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.GetLivelihoodListFromDbUseCase
import com.sarathi.dataloadingmangement.model.uiModel.InfoUiModel
import com.sarathi.dataloadingmangement.model.uiModel.MissionUiModel
import com.sarathi.missionactivitytask.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MissionFilterUtils @Inject constructor(
    private val coreSharedPrefs: CoreSharedPrefs,
    private val getLivelihoodListFromDbUseCase: GetLivelihoodListFromDbUseCase,
    private val translationHelper: TranslationHelper
) {

    private val DEFAULT_MISSION_FILTER = FilterUiModel.getGeneralFilter(
        filterValue = GENERAL_MISSION_FILTER_VALUE,
        filterLabel = translationHelper.stringResource(R.string.general_missions_filter_label),
        imageFileName = null
    )

    private val ALL_MISSION_FILTER = FilterUiModel.getAllFilter(
        filterValue = ALL_MISSION_FILTER_VALUE,
        filterLabel = translationHelper.stringResource(R.string.all_missions_filter_label),
        imageFileName = null
    )

    private val missionFilterList: SnapshotStateList<FilterUiModel> = mutableStateListOf()

    private val _selectedMissionFilter: MutableState<FilterUiModel?> = mutableStateOf(null)
    val selectedMissionFilter: State<FilterUiModel?> get() = _selectedMissionFilter

    suspend fun createMissionFilters(missionList: List<MissionUiModel>) {
        val filterList = ArrayList<FilterUiModel>()
        missionFilterList.clear()
        val livelihoods = getLivelihoodListFromDbUseCase.getLivelihoodListForFilterUi()
            .filter { livelihood -> // filtering livelihood that user's have mapped mission
                missionList.any() {
                    it.livelihoodType?.lowercase() == livelihood.type.lowercase()
                }
            }

        filterList.add(
            ALL_MISSION_FILTER
        )
        filterList.add(
            DEFAULT_MISSION_FILTER
        )

        with(livelihoods.distinctBy { it.programLivelihoodId }) {
            iterator().forEach {
                filterList.add(
                    FilterUiModel(
                        type = FilterType.OTHER(it.type),
                        filterValue = it.name,
                        filterLabel = it.name,
                        imageFileName = getFileNameFromURL(it.image.value())
                    )
                )
            }
        }
        withContext(Dispatchers.IO) {
            missionFilterList.addAll(filterList.distinctBy { it.filterValue })
        }
    }

    fun getMissionFiltersList(): SnapshotStateList<FilterUiModel> {
        return missionFilterList
    }

    fun setSelectedMissionFilterValue(value: FilterUiModel, persistValue: Boolean = false) {

        _selectedMissionFilter.value = value
        if (persistValue)
            coreSharedPrefs.saveMissionFilter(value)

    }

    fun getSelectedMissionFilterValue(): FilterUiModel {
        val selectedMissionFilterValueFromPref = getSelectedMissionFilterValueFromPref()
        return selectedMissionFilter.value ?: selectedMissionFilterValueFromPref
        ?: DEFAULT_MISSION_FILTER
    }

    private fun getSelectedMissionFilterValueFromPref(): FilterUiModel? {
        return coreSharedPrefs.getMissionFilter()
    }

    fun updateMissionFilterOnUserAction(infoUIModel: InfoUiModel) {
        val filterValue = if (infoUIModel.livelihoodType == BLANK_STRING)
            DEFAULT_MISSION_FILTER
        else {
            missionFilterList
                .filter { it.type is FilterType.OTHER }
                .find {
                    (it.type as FilterType.OTHER).filterValue.toString()
                        .equals(infoUIModel.livelihoodType, true)
                }
        }

        setSelectedMissionFilterValue(filterValue ?: DEFAULT_MISSION_FILTER, true)
    }

}