package com.sarathi.dataloadingmangement.model.uiModel

data class TaskUiModel(
    val taskId: Int,
    val status: String?,
    val subjectId: Int,
    val isTaskSecondaryStatusEnable: Boolean,
    val isNotAvailableButton: Boolean,
    val isActiveStatus: Int
)
