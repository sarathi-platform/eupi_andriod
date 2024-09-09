package com.sarathi.dataloadingmangement.enums

import com.sarathi.dataloadingmangement.enums.DataEntryJournal.AssetJournalEntry
import com.sarathi.dataloadingmangement.enums.DataEntryJournal.LivelihoodDataEntry
import com.sarathi.dataloadingmangement.enums.DataEntryJournal.MoneyJournalEntry

enum class LivelihoodEventDataCaptureTypeEnum(val dataEntryJournals: DataEntryJournal) {

    TYPE_OF_ASSET(dataEntryJournals = AssetJournalEntry),
    COUNT_OF_ASSET(dataEntryJournals = AssetJournalEntry),
    AMOUNT(dataEntryJournals = MoneyJournalEntry),
    TYPE_OF_PRODUCT(dataEntryJournals = LivelihoodDataEntry);

}