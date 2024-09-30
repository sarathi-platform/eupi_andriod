package com.nudge.core.datamodel

import com.google.gson.annotations.SerializedName

data class SyncImageMetadataRequest(
    @field:SerializedName("depends_on")
    val dependsOn: List<Any?>? = null,

    @field:SerializedName("data")
    val data: Map<String, Any>? = emptyMap()
)
