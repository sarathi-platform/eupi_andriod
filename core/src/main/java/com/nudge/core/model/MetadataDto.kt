package com.nudge.core.model

import com.google.gson.Gson
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken

data class MetadataDto(

    @Expose
    @SerializedName("mission")
    val mission: String,

    @Expose
    @SerializedName("actors")
    val actors: Any? = null,

    @Expose
    @SerializedName("depends_on")
    val depends_on: List<String>?,

    @Expose
    @SerializedName("request_payload_size")
    val request_payload_size: Long,

    @Expose
    @SerializedName("parentEntity")
    val parentEntity: Map<String, String> = emptyMap(),

    @SerializedName("isRegenerateFile")
    val isRegenerateFile: Boolean = false,

    @Expose
    @SerializedName("data")
    var data: Map<String, Any> = emptyMap()
)

fun String.getMetaDataDtoFromString(): MetadataDto? {
    val type = object : TypeToken<MetadataDto?>() {}.type
    return Gson().fromJson(this, type)
}
