package com.nrlm.baselinesurvey.model.datamodel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ValuesDto(
    @SerializedName("id")
    @Expose
    val id: Int,

    @SerializedName("value")
    @Expose
    val value: String
)

fun List<ValuesDto>.contains(element: String): Boolean {
    if (this.isEmpty())
        return false

    return this.map { it.value }.contains(element)
}

fun List<ValuesDto>.contains(id: Int): Boolean {
    if (this.isEmpty())
        return false

    return this.map { it.id }.contains(id)
}
