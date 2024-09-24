package com.nudge.core.datamodel

import com.google.gson.annotations.SerializedName
import com.nudge.core.BLANK_STRING

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
    val contentType: String? = null,

    @field:SerializedName("blobUrl")
    val blobUrl: String? = BLANK_STRING
)
