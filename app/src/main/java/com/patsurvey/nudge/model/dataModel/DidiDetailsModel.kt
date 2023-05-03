package com.patsurvey.nudge.model.dataModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DidiDetailsModel(
    @SerializedName("id")
    @Expose
    val id:Int,

    @SerializedName("name")
    @Expose
    val name:String,

    @SerializedName("village")
    @Expose
    val village:String,

    @SerializedName("tola")
    @Expose
    val tola:String = "",

    @SerializedName("caste")
    @Expose
    val caste:String = "",

    @SerializedName("house_number")
    @Expose
    val houseNumber:String = "",

    @SerializedName("dada_name")
    @Expose
    val dadaName:String = ""
)
