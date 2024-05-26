package com.sarathi.dataloadingmangement.model.mat.response

import com.google.gson.annotations.SerializedName

data class LanguageAttributes(
    @SerializedName("language_id")
    val languageId: String,
    @SerializedName("attributes")
    val attributes: List<AttributeResponse>,

    )
