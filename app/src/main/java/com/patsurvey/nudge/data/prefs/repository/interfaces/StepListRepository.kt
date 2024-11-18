package com.patsurvey.nudge.data.prefs.repository.interfaces

import com.patsurvey.nudge.database.StepListEntity

interface StepListRepository {
    suspend fun getStepList(villageId: Int, stepId: Int): StepListEntity
    suspend fun getStepListForVillage(villageId: Int): List<StepListEntity>
    suspend fun updateStepStatus(villageId: Int, stepId: Int, stepStatus: Int)
}