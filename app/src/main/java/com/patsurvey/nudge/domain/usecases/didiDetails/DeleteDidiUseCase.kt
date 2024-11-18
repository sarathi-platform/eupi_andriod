package com.patsurvey.nudge.domain.usecases.didiDetails

import com.patsurvey.nudge.activities.AddDidiRepository
import javax.inject.Inject

class DeleteDidiUseCase @Inject constructor(
    private val addDidiRepository: AddDidiRepository
) {

    suspend operator fun invoke(tolaId: Int) {
        addDidiRepository.deleteDidisForTola(tolaId)
    }
}