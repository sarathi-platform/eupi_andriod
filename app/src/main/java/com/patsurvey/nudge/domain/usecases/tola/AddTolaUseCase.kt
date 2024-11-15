package com.patsurvey.nudge.domain.usecases.tola

import com.patsurvey.nudge.activities.ui.transect_walk.TransectWalkRepository
import com.patsurvey.nudge.utils.Tola
import javax.inject.Inject

class AddTolaUseCase @Inject constructor(
    private val transectWalkRepository: TransectWalkRepository
) {

    suspend fun invoke(tola: Tola, villageId: Int): Boolean {
        return transectWalkRepository.insertNewTola(tola, villageId)
    }
}