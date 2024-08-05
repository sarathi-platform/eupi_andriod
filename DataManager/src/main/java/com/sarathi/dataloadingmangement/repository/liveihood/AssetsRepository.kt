package com.sarathi.dataloadingmangement.repository.liveihood

import com.sarathi.dataloadingmangement.model.uiModel.livelihood.LivelihoodDropDownUiModel


interface AssetsRepository {
//    suspend fun getActivity(missionId: Int): List<ActivityUiModel>
    suspend fun getAssets(userId: String, languageCode: String): List<LivelihoodDropDownUiModel>

}