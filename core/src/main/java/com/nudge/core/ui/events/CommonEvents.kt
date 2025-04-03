package com.nudge.core.ui.events

import com.nudge.core.enums.SyncAlertType
import com.nudge.core.model.FilterUiModel
import com.nudge.core.model.uiModel.ValuesDto

sealed class CommonEvents {
    data class UpdateDateRange(val startDate: Long?, val endDate: Long?) : CommonEvents()

    data class SearchValueChangedEvent(val searchQuery: String, val addArgs: Any?) : CommonEvents()

    data class CheckEventLimitThreshold(val result: (alertEventType: SyncAlertType) -> Unit) :
        CommonEvents()

    object OnSubTabChanged : CommonEvents()

    data class OnFilterUiModelSelected(val filterUiModel: FilterUiModel) : CommonEvents()

    data class OnVerificationStatusFilterSelected(val selectedFilter: ValuesDto?) : CommonEvents()

    data class OnVerificationFilterApplied(val selectedFilters: List<ValuesDto>) : CommonEvents()
}

