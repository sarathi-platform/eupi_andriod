package com.sarathi.dataloadingmangement.model.events

import com.google.gson.annotations.SerializedName

data class LivelihoodPlanActivityEventDto(
    @SerializedName("userId")
    val userId: String,
    @SerializedName("primaryLivelihoodId")
    val primaryLivelihoodId: Int,
    @SerializedName("secondaryLivelihoodId")
    val secondaryLivelihoodId: Int,
    @SerializedName("activityId")
    val activityId: Int,
    @SerializedName("missionId")
    val missionId: Int,
    @SerializedName("subjectId")
    val subjectId: Int,
    @SerializedName("subjectType")
    val subjectType: String,
    )
