package com.nudge.core.ui.events

import com.nudge.core.SearchTab

sealed class SearchEvent {
    data class PerformSearch(
        val searchTerm: String,
        val isFilterApplied: Boolean,
        val fromScreen: String
    ) : SearchEvent()

    data class FilterList(val fromScreen: String) : SearchEvent()

    data class SearchTabChanged(val searchTab: SearchTab) : SearchEvent()

    data class PerformComplexSearch(val searchTerm: String, val tabFilter: SearchTab) :
        SearchEvent()
}