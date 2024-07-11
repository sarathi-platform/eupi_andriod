package com.sarathi.dataloadingmangement.model.uiModel

import android.net.Uri

data class TaskCardModel(
    val label: String,
    var value: String,
    val icon: Uri?
)