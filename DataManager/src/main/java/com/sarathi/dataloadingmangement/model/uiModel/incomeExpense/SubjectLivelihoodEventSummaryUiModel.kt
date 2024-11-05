package com.sarathi.dataloadingmangement.model.uiModel.incomeExpense

data class SubjectLivelihoodEventSummaryUiModel(
    val transactionId: String?,
    val subjectId: Int?,
    val date: Long?,
    val livelihoodId: Int?,
    val livelihoodEventId: Int?,
    val livelihoodEventType: String?,
    val transactionAmount: Double?,
    val moneyJournalFlow: String?,
    val assetId: Int?,
    val assetCount: Int?,
    val assetJournalFlow: String?,
    val status: Int,
    val createdDate: Long
) {


}