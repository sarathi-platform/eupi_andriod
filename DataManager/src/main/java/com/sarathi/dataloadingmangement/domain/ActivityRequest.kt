package com.sarathi.dataloadingmangement.domain

import com.google.gson.annotations.SerializedName

data class ActivityRequest(
    @SerializedName("programId") val programId: Int,
    @SerializedName("missionId") val missionId: Int
)