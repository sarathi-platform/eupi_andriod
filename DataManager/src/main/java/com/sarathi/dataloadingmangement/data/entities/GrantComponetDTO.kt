package com.sarathi.dataloadingmangement.data.entities

import com.google.gson.annotations.SerializedName

data class GrantComponentDTO(
    @SerializedName("grantComponentName")
    val grantComponentName: String,
    @SerializedName("grantComponentType")
    val grantComponentType: String,
    @SerializedName("languageId")
    val languageId: String
)