package com.sarathi.dataloadingmangement.repository

import com.sarathi.dataloadingmangement.data.entities.livelihood.MoneyJournalEntity
import com.sarathi.dataloadingmangement.model.events.incomeExpense.SaveMoneyJournalEventDto

interface MoneyJournalForPopEventWriterRepository {

    suspend fun getMoneyJournalEventDto(
        moneyJournalEntity: MoneyJournalEntity
    ): SaveMoneyJournalEventDto
}