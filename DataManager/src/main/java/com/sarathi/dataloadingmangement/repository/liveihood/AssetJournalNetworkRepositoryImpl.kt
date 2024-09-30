package com.sarathi.dataloadingmangement.repository.liveihood

import com.nudge.core.model.ApiResponseModel
import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.livelihood.AssetJournalDao
import com.sarathi.dataloadingmangement.data.entities.livelihood.AssetJournalEntity
import com.sarathi.dataloadingmangement.model.response.AssetJournalApiResponse
import com.sarathi.dataloadingmangement.network.DataLoadingApiService
import javax.inject.Inject

class AssetJournalNetworkRepositoryImpl @Inject constructor(
    private val sharedPrefs: CoreSharedPrefs,
    private val apiInterface: DataLoadingApiService,
    private val assetJournalDao: AssetJournalDao,
) : IAssetJournalSaveNetworkRepository {
    override suspend fun getAssetJournalFromNetwork(): ApiResponseModel<List<AssetJournalApiResponse>> {
        return apiInterface.getAssetJournalDetails(sharedPrefs.getUserNameInInt())

    }

    override suspend fun saveAssetJournalIntoDb(assetJournal: List<AssetJournalApiResponse>) {
        val assetJournalEntities = ArrayList<AssetJournalEntity>()
        assetJournal.forEach {
            assetJournalEntities.add(
                AssetJournalEntity.getAssetJournalEntity(
                    it,
                    sharedPrefs.getUniqueUserIdentifier()
                )
            )
        }
        assetJournalDao.insetAssetJournalEntry(assetJournalEntities)
    }

}