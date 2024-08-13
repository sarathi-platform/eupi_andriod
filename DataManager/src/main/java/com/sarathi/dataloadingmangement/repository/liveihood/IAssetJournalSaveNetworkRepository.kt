package com.sarathi.dataloadingmangement.repository.liveihood

import com.nudge.core.model.ApiResponseModel
import com.sarathi.dataloadingmangement.model.response.AssetJournalApiResponse

interface IAssetJournalSaveNetworkRepository {
    suspend fun getAssetJournalFromNetwork(): ApiResponseModel<List<AssetJournalApiResponse>>

    suspend fun saveAssetJournalIntoDb(assetJournal: List<AssetJournalApiResponse>)
}