package com.sarathi.dataloadingmangement.repository

import com.nudge.core.model.ApiResponseModel
import com.sarathi.dataloadingmangement.model.response.MoneyJournalApiResponse

interface IMoneyJournalSaveNetworkRepository {
    suspend fun getMoneyJournalFromNetwork(): ApiResponseModel<List<MoneyJournalApiResponse>>

    suspend fun saveMoneyJournalIntoDb(moneyJournals: List<MoneyJournalApiResponse>)
}