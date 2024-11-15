package com.patsurvey.nudge.domain.usecases.tola

import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.VillageEntity
import javax.inject.Inject

class GetSelectedVillageUseCase @Inject constructor(val prefRepo: PrefRepo) {
    suspend fun getSelectedVillage(): VillageEntity {
        return prefRepo.getSelectedVillage()
    }
}