package com.nudge.core.model.response

import com.google.gson.annotations.SerializedName

data class LastSyncResponseModel(
    @field:SerializedName("lastSyncDate")
    val lastSyncDate: Long? = null
)