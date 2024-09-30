package com.sarathi.dataloadingmangement.network.response


import com.google.gson.annotations.SerializedName

data class AssetsResponse(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String,
    @SerializedName("status")
    val status: Int,

)