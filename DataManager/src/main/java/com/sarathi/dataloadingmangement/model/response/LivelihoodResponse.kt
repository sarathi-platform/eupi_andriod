package com.sarathi.dataloadingmangement.model.response

import com.google.gson.annotations.SerializedName


data class LivelihoodResponse(

    @SerializedName("livelihood") var livelihood: Livelihood?,
    @SerializedName("assets") var assets: List<Asset>?,
    @SerializedName("products") var products: List<Product>?,
    @SerializedName("events") var events: List<LivelihoodEvent>?

)