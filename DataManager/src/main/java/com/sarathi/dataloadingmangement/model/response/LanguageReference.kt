package com.sarathi.dataloadingmangement.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class LanguageReference(

    @Expose
    @SerializedName("languageCode") var languageCode: String? = null,
    @Expose
    @SerializedName("name") var name: String? = null,
    @Expose
    @SerializedName("id") var id: Int? = null

)