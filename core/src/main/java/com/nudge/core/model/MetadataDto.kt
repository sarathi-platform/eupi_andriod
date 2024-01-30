package com.nudge.core.model

import com.google.gson.Gson
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.nudge.core.KEY_PARENT_ENTITY_TOLA_NAME
import com.nudge.core.KEY_PARENT_ENTITY_VILLAGE_ID
import com.nudge.core.enums.EventName

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
    val parentEntity: Map<String, Any> = emptyMap()

)

fun String.getMetaDataDtoFromString(): MetadataDto? {
    val type = object : TypeToken<MetadataDto?>() {}.type
    return Gson().fromJson(this, type)
}
