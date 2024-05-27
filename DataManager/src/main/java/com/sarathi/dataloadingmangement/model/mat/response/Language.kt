package com.sarathi.dataloadingmangement.model.mat.response


import com.google.gson.annotations.SerializedName

data class Language(
    @SerializedName("description")
    val description: String,
    @SerializedName("language")
    val language: String
)