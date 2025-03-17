package com.sarathi.dataloadingmangement.domain.use_case

import com.nudge.core.utils.CoreLogger
import com.sarathi.dataloadingmangement.SUCCESS
import com.sarathi.dataloadingmangement.network.ApiException
import com.sarathi.dataloadingmangement.network.SUBPATH_GET_MONEY_JOURNAL_DETAILS
import com.sarathi.dataloadingmangement.repository.MoneyJournalNetworkRepository
import javax.inject.Inject


class FetchMoneyJournalUseCase @Inject constructor(private val moneyJournalNetworkRepository: MoneyJournalNetworkRepository) {

    suspend fun invoke(): Boolean {
        try {
            val startTime = System.currentTimeMillis()

            val apiResponse = moneyJournalNetworkRepository.getMoneyJournalFromNetwork()
            CoreLogger.d(
                tag = "LazyLoadAnalysis",
                msg = "FetchMoneyJournalUseCase :$SUBPATH_GET_MONEY_JOURNAL_DETAILS  : ${System.currentTimeMillis() - startTime}"
            )

            if (apiResponse.status.equals(SUCCESS, true)) {
                apiResponse.data?.let {
                    moneyJournalNetworkRepository.saveMoneyJournalIntoDb(it)

                }
                CoreLogger.d(
                    tag = "LazyLoadAnalysis",
                    msg = "SaveFetchMoneyJournalUseCase :$SUBPATH_GET_MONEY_JOURNAL_DETAILS  : ${System.currentTimeMillis() - startTime}"
                )

                return true
            } else {
                CoreLogger.d(
                    tag = "LazyLoadAnalysis",
                    msg = "SaveFetchMoneyJournalUseCase :$SUBPATH_GET_MONEY_JOURNAL_DETAILS  : ${System.currentTimeMillis() - startTime}"
                )

                return false
            }

        } catch (apiException: ApiException) {
            throw apiException
        } catch (ex: Exception) {
            throw ex
        }
    }
}