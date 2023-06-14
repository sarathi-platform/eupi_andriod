package com.patsurvey.nudge.model.response

import com.google.gson.annotations.SerializedName

data class BpcSummaryResponse(
    @SerializedName("cohortCount")
    var cohortCount: Int,

    @SerializedName("mobilisedCount")
    var mobilisedCount: Int,

    @SerializedName("poorDidiCount")
    var poorDidiCount: Int,

    @SerializedName("sentVoEndorsementCount")
    var sentVoEndorsementCount: Int,

    @SerializedName("voEndorsedCount")
    var voEndorsedCount: Int,
)
