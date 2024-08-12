package com.nudge.incomeexpensemodule.events

import com.nudge.core.model.uiModel.LivelihoodModel

sealed class DataTabEvents {
    data class FilterApplied(val livelihoodModel: LivelihoodModel) : DataTabEvents()

}