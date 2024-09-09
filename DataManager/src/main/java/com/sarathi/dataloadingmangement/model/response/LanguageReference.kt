package com.sarathi.dataloadingmangement.model.response

import com.google.gson.annotations.SerializedName


data class LanguageReference(

    @SerializedName("languageCode") var languageCode: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("id") var id: Int? = null

)