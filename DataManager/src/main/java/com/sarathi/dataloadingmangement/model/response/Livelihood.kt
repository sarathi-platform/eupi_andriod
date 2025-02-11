package com.sarathi.dataloadingmangement.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nudge.core.model.response.Validations


data class Livelihood(

    @Expose
    @SerializedName("id")
    var id: Int? = null,
    @Expose
    @SerializedName("name")
    var name: String? = null,
    @Expose
    @SerializedName("status")
    var status: Int? = null,
    @Expose
    @SerializedName("image") var image: String? = null,
    @Expose
    @SerializedName("livelihoodType") var type: LivelihoodType? = null,
    @Expose
    @SerializedName("languages") var languages: ArrayList<LanguageReference> = arrayListOf(),
    @Expose
    @SerializedName("validations") var validations: List<Validations>?,
    @Expose
    @SerializedName("programLivelihoodId") var programLivelihoodId: Int
)

data class LivelihoodType(
    @Expose
    @SerializedName("originalName")
    val originalName: String,
    @Expose
    @SerializedName("languages")
    val languages: List<LanguageReference>
)

data class LivelihoodLanguageAttributes(
    @Expose
    @SerializedName("languageCode")
    val languageCode: String,
    @Expose
    @SerializedName("value")
    val value: String
)
