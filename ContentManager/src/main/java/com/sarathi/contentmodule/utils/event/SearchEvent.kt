package com.sarathi.contentmodule.utils.event

sealed class SearchEvent {
    data class PerformSearch(
        val searchTerm: String,
        val isFilterApplied: Boolean,
        val fromScreen: String
    ) : SearchEvent()

    data class FilterList(val fromScreen: String) : SearchEvent()

}
