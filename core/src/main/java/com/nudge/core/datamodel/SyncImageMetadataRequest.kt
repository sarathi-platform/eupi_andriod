package com.nudge.core.datamodel

import com.google.gson.annotations.SerializedName

data class SyncImageMetadataRequest(
    @field:SerializedName("depends_on")
    val dependsOn: List<Any?>? = null,

    @field:SerializedName("data")
    val data: Data? = null
)

data class Data(

    @field:SerializedName("file_path")
    val filePath: String? = null,

    @field:SerializedName("content_type")
    val contentType: String? = null
)
