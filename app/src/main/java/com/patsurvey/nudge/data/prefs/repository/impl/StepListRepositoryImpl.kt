package com.patsurvey.nudge.data.prefs.repository.impl

import com.patsurvey.nudge.data.prefs.repository.interfaces.StepListRepository
import com.patsurvey.nudge.database.StepListEntity
import com.patsurvey.nudge.database.dao.StepsListDao
import javax.inject.Inject

class StepListRepositoryImpl @Inject constructor(private val stepListDao: StepsListDao) :
    StepListRepository {
    override suspend fun getStepList(villageId: Int, stepId: Int): StepListEntity {
        return stepListDao.getStepForVillage(villageId = villageId, stepId = stepId)
    }

    override suspend fun getStepListForVillage(villageId: Int): List<StepListEntity> {
        return stepListDao.getAllStepsForVillage(villageId = villageId)
    }

    override suspend fun updateStepStatus(villageId: Int, stepId: Int, stepStatus: Int) {
        stepListDao.markStepAsCompleteOrInProgress(stepId, stepStatus, villageId)

    }
}