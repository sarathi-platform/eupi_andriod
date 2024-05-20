package com.nrlm.baselinesurvey.model.datamodel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ConditionsDto(

    @SerializedName("value")
    @Expose
    val value: String,

    @SerializedName("operator")
    @Expose
    val operator: String,

    @SerializedName("resultType")
    @Expose
    val resultType: String,

    @SerializedName("resultList")
    @Expose
    val resultList: List<QuestionList>

)

data class ConditionDtoWithParentId(
    val resultList: QuestionList,
    val parentId: Int
)