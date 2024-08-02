package com.sarathi.dataloadingmangement.network.response


import com.google.gson.annotations.SerializedName

data class LivelihoodResponse(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String,
    @SerializedName("assets")
    val assets: List<AssetsResponse>,
    @SerializedName("products")
    val products: List<ProductResponse>,
    @SerializedName("events")
    val events: List<EventResponse>
)