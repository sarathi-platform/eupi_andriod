package com.nrlm.baselinesurvey.model.datamodel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CasteModel(
    @SerializedName("casteId")
    val casteId: Int?=1,

    @SerializedName("id")
    @Expose
    var id: Int,

    @SerializedName("casteName")
    var casteName : String,

    @SerializedName("languageId")
    var languageId : Int
)
