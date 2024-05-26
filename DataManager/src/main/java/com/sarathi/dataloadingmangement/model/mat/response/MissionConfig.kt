package com.sarathi.dataloadingmangement.model.mat.response

import com.google.gson.annotations.SerializedName

data class MissionConfig(
    @SerializedName("content")
    val contents: List<ContentResponse>
)
