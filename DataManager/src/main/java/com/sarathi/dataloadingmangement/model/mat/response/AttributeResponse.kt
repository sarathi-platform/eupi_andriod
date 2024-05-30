package com.sarathi.dataloadingmangement.model.mat.response


import com.google.gson.annotations.SerializedName

data class AttributeResponse(
    @SerializedName("componentType")
    val componentType: String,
    @SerializedName("key")
    val key: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("value")
    val value: String,
    @SerializedName("languageId")
    val languageCode: String?
)