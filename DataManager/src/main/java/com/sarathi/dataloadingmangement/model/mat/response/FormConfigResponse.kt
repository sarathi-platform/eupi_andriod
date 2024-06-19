package com.sarathi.dataloadingmangement.model.mat.response

import com.google.gson.annotations.SerializedName

data class FormConfigResponse(
    @SerializedName("componentType")
    val componentType: String,
    @SerializedName("formType")
    val formType: String,
    @SerializedName("key")
    val key: String,
    @SerializedName("languageId")
    val languageId: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("value")
    val value: String
)