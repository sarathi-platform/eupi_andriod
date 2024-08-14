package com.nudge.incomeexpensemodule.events

sealed class DataTabEvents {
    data class LivelihoodFilterApplied(val livelihoodId: Int) : DataTabEvents()
    object LivelihoodSortApplied : DataTabEvents()

    data class OnSearchQueryChanged(val searchQuery: String) : DataTabEvents()

    data class ShowAssetDialogForSubject(
        val showDialog: Boolean,
        val subjectId: Int,
        val livelihoodIds: List<Int>
    ) : DataTabEvents()

}