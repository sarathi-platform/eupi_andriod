package com.nrlm.baselinesurvey.ui.common_components.common_events

sealed class SearchEvent {
    data class PerformSearch(val searchTerm: String, val isFilterApplied: Boolean) : SearchEvent()
    object FilterList: SearchEvent()
}
