package com.sarathi.missionactivitytask.utils.event

sealed class SearchEvent {
    data class PerformSearch(
        val searchTerm: String,
        val isGroupingApplied: Boolean,
        val isFilterApplied: Boolean = false
    ) : SearchEvent()
}
