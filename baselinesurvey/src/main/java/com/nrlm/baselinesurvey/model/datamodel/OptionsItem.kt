package com.nrlm.baselinesurvey.model.datamodel

import com.google.gson.annotations.SerializedName
import com.nrlm.baselinesurvey.BLANK_STRING

data class OptionsItem(

    @SerializedName("display")
    val display: String? = null,

    @SerializedName("weight")
    val weight: Int? = null,

    @SerializedName("optionId")
    val optionId: Int? = null,

    @SerializedName("optionValue")
    val optionValue: Int? = null,

    @SerializedName("summary")
    val summary: String? = null,

    @SerializedName("isSelected")
    val isSelected: Boolean? = false,

    @SerializedName("count")
    var count: Int? = 0,

    @SerializedName("optionImage")
    var optionImage: String? = BLANK_STRING,

    @SerializedName("optionType")
    var optionType: String? = BLANK_STRING,

    )
