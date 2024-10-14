package com.sarathi.dataloadingmangement.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class LivelihoodResponse(
    @Expose
    @SerializedName("livelihood")
    var livelihood: Livelihood?,
    @Expose
    @SerializedName("assets")
    var assets: List<Asset>?,
    @Expose
    @SerializedName("products") var products: List<Product>?,
    @SerializedName("events") var events: List<LivelihoodEvent>?

)