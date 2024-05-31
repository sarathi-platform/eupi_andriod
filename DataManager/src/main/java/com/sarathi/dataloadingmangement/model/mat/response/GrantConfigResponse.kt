package com.sarathi.dataloadingmangement.model.mat.response


import com.google.gson.annotations.SerializedName

data class GrantConfigResponse(
    @SerializedName("grantComponent")
    val grantComponent: String,
    @SerializedName("grantId")
    val grantId: Int,
    @SerializedName("grantMode")
    val grantMode: List<String>,
    @SerializedName("grantName")
    val grantName: String,
    @SerializedName("grantNature")
    val grantNature: List<String>,
    @SerializedName("grantType")
    val grantType: String
)