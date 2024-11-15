package com.patsurvey.nudge.domain.usecases.tola

import com.patsurvey.nudge.activities.ui.transect_walk.TransectWalkRepository
import com.patsurvey.nudge.database.TolaEntity
import javax.inject.Inject

class GetTolaUseCase @Inject constructor(
    private val transectWalkRepository: TransectWalkRepository
) {

    suspend fun invoke(villageId: Int): TolaEntity {
        return transectWalkRepository.getTola(villageId)
    }

    suspend fun getAllTolasForVillage(villageId: Int): List<TolaEntity> {
        return transectWalkRepository.getAllTolasForVillage(villageId)
    }
}