package com.sarathi.dataloadingmangement.util.event

sealed class LivelihoodPlanningEvent {

    data class PrimaryLivelihoodPlanningEvent(val livelihoodId: Int) : LivelihoodPlanningEvent()
    data class SecondaryLivelihoodPlanningEvent(val livelihoodId: Int) : LivelihoodPlanningEvent()

}