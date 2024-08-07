package com.sarathi.dataloadingmangement.domain.use_case

import com.sarathi.dataloadingmangement.SUCCESS
import com.sarathi.dataloadingmangement.network.ApiException
import com.sarathi.dataloadingmangement.repository.MoneyJournalNetworkRepository
import javax.inject.Inject


class FetchMoneyJournalUseCase @Inject constructor(private val moneyJournalNetworkRepository: MoneyJournalNetworkRepository) {

    suspend fun invoke(): Boolean {
        try {
            val apiResponse = moneyJournalNetworkRepository.getMoneyJournalFromNetwork()
            if (apiResponse.status.equals(SUCCESS, true)) {
                apiResponse.data?.let {
                    moneyJournalNetworkRepository.saveMoneyJournalIntoDb(it)

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