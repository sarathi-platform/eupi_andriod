package com.sarathi.dataloadingmangement.model.response

import com.google.gson.annotations.SerializedName

class LivelihoodEvent(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("status") var status: Int? = null,
    @SerializedName("image") var image: String? = null,
    @SerializedName("type") var type: String? = null,
    @SerializedName("validation") var validation: Validation?,
    @SerializedName("languages") var languages: ArrayList<LanguageReference> = arrayListOf()
)
