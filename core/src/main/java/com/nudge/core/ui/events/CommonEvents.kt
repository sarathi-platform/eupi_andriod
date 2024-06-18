package com.nudge.core.ui.events

sealed class CommonEvents {
    data class UpdateDateRange(val startDate: Long?, val endDate: Long?) : CommonEvents()

    data class SearchValueChangedEvent(val searchQuery: String, val addArgs: Any?) : CommonEvents()
}