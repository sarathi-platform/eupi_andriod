package com.sarathi.dataloadingmangement.model.mat.response


import com.google.gson.annotations.SerializedName

data class ActivityTitle(
    @SerializedName("language")
    val language: String,
    @SerializedName("description")
    val name: String
)