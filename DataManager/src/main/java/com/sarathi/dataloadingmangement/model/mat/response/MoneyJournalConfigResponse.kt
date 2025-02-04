package com.sarathi.dataloadingmangement.model.mat.response

import com.google.gson.annotations.SerializedName

data class MoneyJournalConfigResponse(
    @SerializedName("tags")
    val tags: List<Int>? = emptyList()
)