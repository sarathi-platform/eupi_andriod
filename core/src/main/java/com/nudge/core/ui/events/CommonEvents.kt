package com.nudge.core.ui.events

import com.nudge.core.enums.SyncAlertType

sealed class CommonEvents {
    data class UpdateDateRange(val startDate: Long?, val endDate: Long?) : CommonEvents()

    data class SearchValueChangedEvent(val searchQuery: String, val addArgs: Any?) : CommonEvents()

    data class CheckEventLimitThreshold(val result: (alertEventType: SyncAlertType) -> Unit) :
        CommonEvents()
}