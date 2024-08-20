package com.sarathi.dataloadingmangement.domain.use_case.livelihood

import com.google.gson.annotations.SerializedName

data class LivelihoodOptionResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("subjectId")
    val subjectId: Int,
    @SerializedName("subjectType")
    val subjectType: String,
    @SerializedName("doerId")
    val doerId: Int,
    @SerializedName("selectedPrimaryLivelihood")
    val selectedPrimaryLivelihood: Int?,
    @SerializedName("selectedSecondaryLivelihood")
    val selectedSecondaryLivelihood: Int,
    @SerializedName("activityId")
    val activityId: Int?,
   @SerializedName("status")
    val status: Int?,

) {
}
