package com.patsurvey.nudge.activities.ui.progress.domain.repository.interfaces

import com.patsurvey.nudge.database.VillageEntity

interface SelectionVillageRepository {

    fun setSelectedVillage(villageEntity: VillageEntity)
    fun getSelectedVillage(): VillageEntity

    suspend fun getVillageListFromDb(): List<VillageEntity>

}