package com.sarathi.dataloadingmangement.domain.use_case.income_expense

import com.sarathi.dataloadingmangement.SUCCESS
import com.sarathi.dataloadingmangement.network.ApiException
import com.sarathi.dataloadingmangement.repository.liveihood.GetLivelihoodSaveEventRepositoryImpl
import javax.inject.Inject

class FetchLivelihoodSaveEventUseCase @Inject constructor(
    private val getLivelihoodSaveEventRepositoryImpl: GetLivelihoodSaveEventRepositoryImpl
) {
    suspend fun invoke(): Boolean {
        try {
            val apiResponse =
                getLivelihoodSaveEventRepositoryImpl.getLivelihoodSaveEventFromNetwork()
            if (apiResponse.status.equals(SUCCESS, true)) {
                apiResponse.data?.let {
                    getLivelihoodSaveEventRepositoryImpl.saveLivelihoodSaveEventIntoDb(it)
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