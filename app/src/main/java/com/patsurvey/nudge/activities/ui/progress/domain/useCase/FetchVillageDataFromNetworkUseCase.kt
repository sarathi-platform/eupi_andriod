package com.patsurvey.nudge.activities.ui.progress.domain.useCase

import com.nudge.core.value
import com.patsurvey.nudge.activities.ui.progress.domain.repository.interfaces.FetchVillageDataFromNetworkRepository
import com.patsurvey.nudge.activities.ui.progress.domain.repository.interfaces.SelectionVillageRepository
import com.sarathi.dataloadingmangement.NUMBER_ZERO
import javax.inject.Inject

class FetchVillageDataFromNetworkUseCase @Inject constructor(
    private val fetchVillageDataFromNetworkRepository: FetchVillageDataFromNetworkRepository,
    private val selectionVillageRepository: SelectionVillageRepository
) {

    suspend operator fun invoke(
        villageId: Int,
        onComplete: (isSuccess: Boolean) -> Unit
    ) {
        val villageData = selectionVillageRepository.getSelectedVillageFromDb()
        if (villageData?.isDataLoadTriedOnce.value(NUMBER_ZERO) > NUMBER_ZERO) {
            onComplete(true)
            return
        }

        try {
            fetchVillageDataFromNetworkRepository.fetchStepListForVillageFromNetwork(villageId)
            fetchVillageDataFromNetworkRepository.fetchDidiListForVillageFromNetwork(villageId)
            fetchVillageDataFromNetworkRepository.fetchTolaListForVillageFromNetwork(villageId)
            fetchVillageDataFromNetworkRepository.fetchSavedAnswerForVillageFromNetwork(villageId)
            fetchVillageDataFromNetworkRepository.updateVillageDataLoadingStatus(
                villageId = villageId,
                isDataLoaded = villageData?.isDataLoadTriedOnce.value(NUMBER_ZERO).inc()
            )
            onComplete(true)
        } catch (ex: Exception) {
            onComplete(false)
        }

    }

}