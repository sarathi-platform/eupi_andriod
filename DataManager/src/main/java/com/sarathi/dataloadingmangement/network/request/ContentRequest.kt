package com.sarathi.dataloadingmangement.network.request

import com.google.gson.annotations.SerializedName

data class ContentRequest(
    @SerializedName("languageCode")
    val languageCode: String,
    @SerializedName("contentKey")
    val contentKey: String
)