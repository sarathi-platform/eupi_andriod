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
    @SerializedName("type") var type: String? = null,
    @Expose
    @SerializedName("languages") var languages: ArrayList<LanguageReference> = arrayListOf(),
    @Expose
    @SerializedName("validations") var validations: List<Validations>?

)