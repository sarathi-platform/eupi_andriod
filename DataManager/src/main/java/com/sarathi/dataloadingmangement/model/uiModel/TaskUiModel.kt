package com.sarathi.dataloadingmangement.model.uiModel

data class TaskUiModelV1(
    val taskId: Int,
    val status: String?,
    val subjectId: Int,
    val isActive: Int? = 1
)
