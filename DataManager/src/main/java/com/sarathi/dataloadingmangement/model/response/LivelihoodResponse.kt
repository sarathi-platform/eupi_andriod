package com.sarathi.dataloadingmangement.model.response

import com.google.gson.annotations.SerializedName


data class LivelihoodResponse(

    @SerializedName("livelihood") var livelihood: Livelihood?,
    @SerializedName("assets") var assets: Asset?,
    @SerializedName("products") var products: Product?,
    @SerializedName("events") var events: LivelihoodEvent?

)