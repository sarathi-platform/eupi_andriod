package com.nudge.incomeexpensemodule.events

sealed class DataSummaryScreenEvents() {

    data class FilterDataForLivelihood(val livelihoodId: Int) : DataSummaryScreenEvents()

}