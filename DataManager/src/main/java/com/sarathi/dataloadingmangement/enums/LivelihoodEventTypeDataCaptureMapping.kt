package com.sarathi.dataloadingmangement.enums

import com.sarathi.dataloadingmangement.enums.EntryFlowTypeEnum.INFLOW
import com.sarathi.dataloadingmangement.enums.EntryFlowTypeEnum.OUTFLOW
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
        assetJournalEntryFlowType = INFLOW
    ),
    AssetDecrease(
        livelihoodEventDataCaptureTypes = listOf(TYPE_OF_ASSET, COUNT_OF_ASSET),
        assetJournalEntryFlowType = OUTFLOW
    ),

    AssetPurchase(
        livelihoodEventDataCaptureTypes = listOf(TYPE_OF_ASSET, COUNT_OF_ASSET, AMOUNT),
        assetJournalEntryFlowType = INFLOW,
        moneyJournalEntryFlowType = OUTFLOW
    ),
    AssetSale(
        livelihoodEventDataCaptureTypes = listOf(TYPE_OF_ASSET, COUNT_OF_ASSET, AMOUNT),
        assetJournalEntryFlowType = OUTFLOW,
        moneyJournalEntryFlowType = INFLOW
    ),

    ProductSale(
        livelihoodEventDataCaptureTypes = listOf(TYPE_OF_PRODUCT, AMOUNT),
        moneyJournalEntryFlowType = INFLOW
    ),

    Income(livelihoodEventDataCaptureTypes = listOf(AMOUNT), moneyJournalEntryFlowType = INFLOW),
    Expense(livelihoodEventDataCaptureTypes = listOf(AMOUNT), moneyJournalEntryFlowType = OUTFLOW);

    companion object {

        fun getLivelihoodEventFromName(eventName: String): LivelihoodEventTypeDataCaptureMapping {

            when (eventName) {
                AssetIncrease.name -> return AssetIncrease
                AssetDecrease.name -> return AssetDecrease
                AssetPurchase.name -> return AssetPurchase
                AssetSale.name -> return AssetSale
                ProductSale.name -> return ProductSale
                Income.name -> return Income
                Expense.name -> return Expense

            }
            return Expense
        }
    }

}

