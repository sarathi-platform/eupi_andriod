package com.sarathi.dataloadingmangement.enums

import com.sarathi.dataloadingmangement.enums.EntryFlowTypeEnum.Inflow
import com.sarathi.dataloadingmangement.enums.EntryFlowTypeEnum.OutFlow
import com.sarathi.dataloadingmangement.enums.LivelihoodEventDataCaptureTypeEnum.AMOUNT
import com.sarathi.dataloadingmangement.enums.LivelihoodEventDataCaptureTypeEnum.COUNT_OF_ASSET
import com.sarathi.dataloadingmangement.enums.LivelihoodEventDataCaptureTypeEnum.TYPE_OF_ASSET
import com.sarathi.dataloadingmangement.enums.LivelihoodEventDataCaptureTypeEnum.TYPE_OF_PRODUCT

enum class LivelihoodEventTypeDataCaptureMapping(
    val livelihoodEventDataCaptureTypes: List<LivelihoodEventDataCaptureTypeEnum>,
    val assetJournalEntryFlowType: EntryFlowTypeEnum? = null,
    val moneyJournalEntryFlowType: EntryFlowTypeEnum? = null
) {

    AssetIncrease(
        livelihoodEventDataCaptureTypes = listOf(TYPE_OF_ASSET, COUNT_OF_ASSET),
        assetJournalEntryFlowType = Inflow
    ),
    AssetDecrease(
        livelihoodEventDataCaptureTypes = listOf(TYPE_OF_ASSET, COUNT_OF_ASSET),
        assetJournalEntryFlowType = OutFlow
    ),

    AssetPurchase(
        livelihoodEventDataCaptureTypes = listOf(TYPE_OF_ASSET, COUNT_OF_ASSET, AMOUNT),
        assetJournalEntryFlowType = Inflow,
        moneyJournalEntryFlowType = OutFlow
    ),
    AssetSale(
        livelihoodEventDataCaptureTypes = listOf(TYPE_OF_ASSET, COUNT_OF_ASSET, AMOUNT),
        assetJournalEntryFlowType = OutFlow,
        moneyJournalEntryFlowType = Inflow
    ),

    ProductSale(
        livelihoodEventDataCaptureTypes = listOf(TYPE_OF_PRODUCT, AMOUNT),
        moneyJournalEntryFlowType = Inflow
    ),

    Income(livelihoodEventDataCaptureTypes = listOf(AMOUNT), moneyJournalEntryFlowType = Inflow),
    Expense(livelihoodEventDataCaptureTypes = listOf(AMOUNT), moneyJournalEntryFlowType = OutFlow);

}

