package com.sarathi.dataloadingmangement.repository.liveihood

import com.nudge.core.model.uiModel.LivelihoodModel


interface GetLivelihoodListFromDbRepository {
    suspend fun getLivelihoodListFromDb(): List<LivelihoodModel>

}