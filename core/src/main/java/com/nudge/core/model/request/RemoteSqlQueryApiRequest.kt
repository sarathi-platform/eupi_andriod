package com.nudge.core.model.request


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nudge.core.model.RemoteQueryDto

data class RemoteSqlQueryApiRequest(

    @SerializedName("propertyValueId")
    @Expose
    val propertyValueId: Int,
    @SerializedName("userId")
    @Expose
    val userId: Int,
    @SerializedName("value")
    @Expose
    val value: List<RemoteQueryDto>
)