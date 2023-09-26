package com.patsurvey.nudge.model.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SaveMatchSummaryRequest(
    @SerializedName("programId")
    @Expose
    val programId: Int,
    @SerializedName("score")
    @Expose
    val score: Int,
    @SerializedName("villageId")
    @Expose
    val villageId: Int,
    @SerializedName("didiNotAvailableCountBPC")
    @Expose
    val didiNotAvailableCountBPC: Int
)