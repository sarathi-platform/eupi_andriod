package com.sarathi.dataloadingmangement.model.mat.response


import com.google.gson.annotations.SerializedName

data class ContentResponse(
    @SerializedName("contentKey")
    val contentKey: String,
    @SerializedName("contentType")
    val contentType: String,
    @SerializedName("languageCode")
    val languageCode: String?
)