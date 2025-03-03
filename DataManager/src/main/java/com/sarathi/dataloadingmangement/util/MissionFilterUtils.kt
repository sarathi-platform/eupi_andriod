package com.sarathi.dataloadingmangement.util

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.nudge.core.ALL_MISSION_FILTER_VALUE
import com.nudge.core.BLANK_STRING
import com.nudge.core.GENERAL_MISSION_FILTER_VALUE
import com.nudge.core.R
import com.nudge.core.getFileNameFromURL
import com.nudge.core.helper.TranslationHelper
import com.nudge.core.model.FilterType
import com.nudge.core.model.FilterUiModel
import com.nudge.core.model.updateTranslations
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.value
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.GetLivelihoodListFromDbUseCase
import com.sarathi.dataloadingmangement.model.uiModel.InfoUiModel
import com.sarathi.dataloadingmangement.model.uiModel.MissionUiModel
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
                    it.programLivelihoodReferenceId?.contains(livelihood.programLivelihoodId) == true
                }
            }

        filterList.add(
            ALL_MISSION_FILTER.updateTranslations(
                translationHelper,
                R.string.all_missions_filter_label
            )
        )
        filterList.add(
            DEFAULT_MISSION_FILTER.updateTranslations(
                translationHelper,
                R.string.general_missions_filter_label
            )
        )

        with(livelihoods.distinctBy { it.programLivelihoodId }) {
            iterator().forEach {
                filterList.add(
                    FilterUiModel(
                        type = FilterType.OTHER(it.programLivelihoodId),
                        filterValue = it.name,
                        filterLabel = it.name,
                        imageFileName = getFileNameFromURL(it.image.value())
                    )
                )
            }
        }

        missionFilterList.addAll(filterList.distinctBy { it.filterValue })

        updateSelectedLivelihoodFilterLabelOnInit()

    }

    private fun updateSelectedLivelihoodFilterLabelOnInit() {
        val selectedFilter = getSelectedMissionFilterValue()
        setSelectedMissionFilterValue((missionFilterList.find { it.type == selectedFilter.type }
            ?: DEFAULT_MISSION_FILTER))
    }

    fun getMissionFiltersList(): SnapshotStateList<FilterUiModel> {
        return missionFilterList
    }

    fun setSelectedMissionFilterValue(value: FilterUiModel, persistValue: Boolean = false) {

        _selectedMissionFilter.value = value
        if (persistValue)
            coreSharedPrefs.saveMissionFilter(value)

    }

    private fun resetSelectedMissionFilterValueToDefault(persistValue: Boolean) {
        setSelectedMissionFilterValue(DEFAULT_MISSION_FILTER, persistValue)
    }

    fun getSelectedMissionFilterValue(): FilterUiModel {
        var selectedMissionFilterValueFromPref = getSelectedMissionFilterValueFromPref()

        if (selectedMissionFilterValueFromPref?.type is FilterType.OTHER) {
            val selectedFilterValue = (selectedMissionFilterValueFromPref.type as FilterType.OTHER)
                .filterValue

            val filterValues = getMissionFilterListForLivelihood()
                .mapNotNull { (it.type as? FilterType.OTHER)?.filterValue }

            if (selectedFilterValue !in filterValues) {
                selectedMissionFilterValueFromPref = null
                resetSelectedMissionFilterValueToDefault(persistValue = true)
            }
        }

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
            getMissionFilterListForLivelihood()
                .find {
                    infoUIModel.programLivelihoodReferenceId?.contains((it.type as FilterType.OTHER).filterValue) == true
                }
        }

        setSelectedMissionFilterValue(filterValue ?: DEFAULT_MISSION_FILTER, true)
    }

    fun getMissionFilterListForLivelihood(): List<FilterUiModel> =
        missionFilterList.filter { it.type is FilterType.OTHER }

}