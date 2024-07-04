package com.sarathi.dataloadingmangement.model.mat.response


import com.google.gson.annotations.SerializedName

data class TaskData(
    @SerializedName("key")
    val key: String,
    @SerializedName("value")
    val value: String?
)