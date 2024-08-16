package com.sarathi.dataloadingmangement.domain.use_case.livelihood

import com.sarathi.dataloadingmangement.SUCCESS
import com.sarathi.dataloadingmangement.network.ApiException
import com.sarathi.dataloadingmangement.repository.liveihood.AssetJournalNetworkRepositoryImpl
import javax.inject.Inject


class FetchAssetJournalUseCase @Inject constructor(private val assetJournalNetworkRepositoryImpl: AssetJournalNetworkRepositoryImpl) {

    suspend fun invoke(): Boolean {
        try {
            val apiResponse = assetJournalNetworkRepositoryImpl.getAssetJournalFromNetwork()
            if (apiResponse.status.equals(SUCCESS, true)) {
                apiResponse.data?.let {
                    assetJournalNetworkRepositoryImpl.saveAssetJournalIntoDb(it)

                }
                return true
            } else {
                return false
            }

        } catch (apiException: ApiException) {
            throw apiException
        } catch (ex: Exception) {
            throw ex
        }
    }
}