package com.sarathi.dataloadingmangement.model.uiModel

data class ConditionsUiModel(
    val sourceTargetQuestionIdRef: Int,
    val sourceQuestionId: Int,
    val targetQuestionId: Int,
    val conditionOperator: String?,
    val condition: String,
)