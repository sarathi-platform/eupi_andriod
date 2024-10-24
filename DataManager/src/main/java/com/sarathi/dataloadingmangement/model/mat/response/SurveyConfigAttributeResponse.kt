package com.sarathi.dataloadingmangement.model.mat.response


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SurveyConfigAttributeResponse(
    @SerializedName("componentType")
    @Expose
    val componentType: String,
    @SerializedName("formId")
    @Expose
    val formId: Int,
    @SerializedName("icon")
    @Expose
    val icon: String,
    @SerializedName("key")
    @Expose
    val key: String,
    @SerializedName("label")
    @Expose
    val label: String,
    @SerializedName("languageId")
    @Expose
    val languageId: String,
    @SerializedName("type")
    @Expose
    val type: String,
    @SerializedName("value")
    @Expose
    val value: String,
    @SerializedName("tagId")
    @Expose
    val tagId: Int
)