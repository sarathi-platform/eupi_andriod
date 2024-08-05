package com.sarathi.dataloadingmangement.repository.liveihood

import com.sarathi.dataloadingmangement.model.uiModel.livelihood.LivelihoodModel


interface GetLivelihoodListFromDbRepository {
    suspend fun getLivelihoodListFromDb(): List<LivelihoodModel>

}