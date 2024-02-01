package com.nrlm.baselinesurvey.model.datamodel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.model.response.QuestionList

data class OptionsItem(

    @SerializedName("description")
    val display: String? = null,

    @SerializedName("weight")
    val weight: Int? = null,

    @SerializedName("optionId")
    val optionId: Int? = null,

    @SerializedName("optionValue")
    val optionValue: Int? = null,

    @SerializedName("paraphrase")
    val summary: String? = null,

    /*    @SerializedName("isSelected")
        val isSelected: Boolean? = false,*/

    @SerializedName("count")
    var count: Int? = 0,

    //TODO Uncomment when actual data is fetched
    /*@SerializedName("optionImage")
    var optionImage: String? = BLANK_STRING,*/

    @SerializedName("optionImage")
    var optionImage: Int? = 0,

    @SerializedName("optionType")
    var optionType: String? = BLANK_STRING,


    @SerializedName("conditional")
    @Expose
    val conditional: Boolean,
    @SerializedName("languageCode")
    @Expose
    val languageCode: String = BLANK_STRING,
    @SerializedName("order")
    @Expose
    val order: Int,
    @SerializedName("questions")
    @Expose
    val questionList: List<QuestionList?> = listOf(),
    @SerializedName("type")
    @Expose
    val type: String = BLANK_STRING,
    @SerializedName("values")
    @Expose
    val values: List<String> = listOf(),
)
