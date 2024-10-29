package com.patsurvey.nudge.activities.ui.progress.domain.useCase

import com.patsurvey.nudge.activities.ui.progress.domain.repository.interfaces.SelectionVillageRepository
import com.patsurvey.nudge.database.VillageEntity
import javax.inject.Inject

class SelectionVillageUseCase @Inject constructor(
    private val selectionVillageRepository: SelectionVillageRepository
) {

    fun setSelectedVillage(villageEntity: VillageEntity) {
        selectionVillageRepository.setSelectedVillage(villageEntity)
    }

    fun getSelectedVillage(): VillageEntity {
        return selectionVillageRepository.getSelectedVillage()
    }

    suspend fun getVillageListFromDb(): List<VillageEntity> {
        return selectionVillageRepository.getVillageListFromDb()
    }

    suspend fun getSelectedVillageFromDb(languageId: Int? = null): VillageEntity? {
        return selectionVillageRepository.getSelectedVillageFromDb(languageId)
    }

}