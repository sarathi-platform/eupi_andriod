package com.patsurvey.nudge.domain.usecases.didiDetails

import com.patsurvey.nudge.activities.AddDidiRepository
import com.patsurvey.nudge.database.DidiEntity
import javax.inject.Inject

class GetDidiUseCase @Inject constructor(
    private val addDidiRepository: AddDidiRepository
) {

    suspend operator fun invoke(tolaId: Int): List<DidiEntity> {
        return addDidiRepository.getDidisForTola(tolaId)
    }
}