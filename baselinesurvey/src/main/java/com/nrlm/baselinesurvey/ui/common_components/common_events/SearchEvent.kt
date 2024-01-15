package com.nrlm.baselinesurvey.ui.common_components.common_events

sealed class SearchEvent {
    data class PerformSearch(val searchTerm: String, val isFilterApplied: Boolean, val fromScreen: String) : SearchEvent()
    data class FilterList(val fromScreen: String): SearchEvent()

    object SearchTabChanged : SearchEvent()

    data class PerformComplexSearch(val searchTerm: String): SearchEvent()
}
