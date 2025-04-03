package com.nudge.core.model.response


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nudge.core.model.RemoteQueryDto

data class RemoteSqlQueryApiResponseItem(
    @SerializedName("level")
    @Expose
    val level: String,
    @SerializedName("propertyValueId")
    @Expose
    val propertyValueId: Int,
    @SerializedName("value")
    @Expose
    val value: List<RemoteQueryDto>
)