package com.sarathi.missionactivitytask.utils.event

sealed class SearchEvent {
    data class PerformSearch(
        val searchTerm: String,
        val isSearchApplied: Boolean,
    ) : SearchEvent()
}
