package com.sarathi.dataloadingmangement.model.uiModel.incomeExpense

data class SubjectLivelihoodEventHistoryUiModel(
    val id: Int = 0,
    val userId: String,
    val transactionId: String,
    val subjectId: Int,
    val date: Long,
    val livelihoodId: Int,
    val livelihoodEventId: Int,
    val livelihoodEventType: String,
    val surveyResponse: String,
    val status: Int,
    val createdDate: Long,
    val modifiedDate: Long,
    val livelihoodImage: String?
)