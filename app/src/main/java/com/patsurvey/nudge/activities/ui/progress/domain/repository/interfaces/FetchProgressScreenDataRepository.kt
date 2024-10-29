package com.patsurvey.nudge.activities.ui.progress.domain.repository.interfaces

import com.patsurvey.nudge.database.StepListEntity

interface FetchProgressScreenDataRepository {

    suspend fun getStepListForVillage(villageId: Int): List<StepListEntity>
    suspend fun getStepSummaryForVillage(villageId: Int): Map<String, Int>

}