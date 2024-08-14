package com.nudge.incomeexpensemodule.events

sealed class DataSummaryScreenEvents() {

    data class FilterDataForLivelihood(val livelihoodId: Int) : DataSummaryScreenEvents()

    data class EventsSubFilterSelected(val selectedValue: Int) : DataSummaryScreenEvents()

    data class TabFilterSelected(val selectedTabIndex: Int) : DataSummaryScreenEvents()

    data class CustomDateRangeFilterSelected(val selectedTabIndex: Int) : DataSummaryScreenEvents()

}