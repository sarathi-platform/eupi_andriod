package com.sarathi.dataloadingmangement.model.response


import com.google.gson.annotations.SerializedName

data class Validation(
    @SerializedName("expression")
    val expression: String?,
    @SerializedName("message")
    val message: String?
)