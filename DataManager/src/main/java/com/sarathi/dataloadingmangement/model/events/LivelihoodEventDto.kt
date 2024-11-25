package com.sarathi.dataloadingmangement.model.events

import com.google.gson.annotations.SerializedName

data class LivelihoodPlanActivityEventDto(
    @SerializedName("userId")
    val userId: String,
    @SerializedName("livelihoodType")
    val livelihoodType: ArrayList<LivelihoodTypeEventDto>,
    @SerializedName("activityId")
    val activityId: Int,
    @SerializedName("missionId")
    val missionId: Int,
    @SerializedName("subjectId")
    val subjectId: Int,
    @SerializedName("subjectType")
    val subjectType: String,
    )
data class LivelihoodTypeEventDto(
    @SerializedName("programLivelihoodId")
    val programLivelihoodId: Int,
    @SerializedName("order")
    val order: Int
)

