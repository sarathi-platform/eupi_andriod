package com.sarathi.dataloadingmangement.repository

import com.nudge.core.model.ApiResponseModel
import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.livelihood.MoneyJournalDao
import com.sarathi.dataloadingmangement.data.entities.livelihood.MoneyJournalEntity
import com.sarathi.dataloadingmangement.model.response.MoneyJournalApiResponse
import com.sarathi.dataloadingmangement.network.DataLoadingApiService
import javax.inject.Inject

class MoneyJournalNetworkRepository @Inject constructor(
    val sharedPrefs: CoreSharedPrefs,
    val apiInterface: DataLoadingApiService,
    val moneyJournalDao: MoneyJournalDao,
) : IMoneyJournalSaveNetworkRepository {

    override suspend fun getMoneyJournalFromNetwork(): ApiResponseModel<List<MoneyJournalApiResponse>> {
        return apiInterface.getMoneyJournalDetails(sharedPrefs.getUserNameInInt())
    }

    override suspend fun saveMoneyJournalIntoDb(moneyJournals: List<MoneyJournalApiResponse>) {
        if (moneyJournalDao.isMoneyJournalEntryExistForUser(sharedPrefs.getUniqueUserIdentifier()) == 0) {
            val moneyJournalEntities = ArrayList<MoneyJournalEntity>()
            moneyJournals.forEach {
                moneyJournalEntities.add(
                    MoneyJournalEntity.getMoneyJournalEntity(
                        it,
                        sharedPrefs.getUniqueUserIdentifier()
                    )
                )
            }
            moneyJournalDao.insertMoneyJournalEntry(moneyJournalEntities)
    }
    }
}