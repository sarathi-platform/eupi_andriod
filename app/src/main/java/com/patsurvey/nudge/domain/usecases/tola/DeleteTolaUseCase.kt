package com.patsurvey.nudge.domain.usecases.tola

import com.patsurvey.nudge.activities.ui.transect_walk.TransectWalkRepository
import javax.inject.Inject

class DeleteTolaUseCase @Inject constructor(
    private val transectWalkRepository: TransectWalkRepository
) {

    suspend operator fun invoke(tolaId: Int) {
        transectWalkRepository.deleteTola(id = tolaId)
    }
}