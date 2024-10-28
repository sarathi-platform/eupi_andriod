package com.nudge.core.model.response


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Validations(
    @SerializedName("assetType")
    @Expose
    val assetType: String,
    @SerializedName("eventName")
    @Expose
    val eventName: String,
    @SerializedName("productType")
    @Expose
    val productType: String,
    @SerializedName("livelihoodType")
    @Expose
    val livelihoodType: String,
    @SerializedName("validation")
    @Expose
    val validation: List<Validation>
)

data class SurveyValidations(
    @SerializedName("sectionId")
    @Expose
    val sectionId: Int,
    @SerializedName("questionId")
    @Expose
    val questionId: Int,
    @SerializedName("optionId")
    @Expose
    val optionId: Int,
    @SerializedName("validation")
    @Expose
    val validation: List<Validation>
)