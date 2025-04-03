package com.nudge.core.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class FetchRemoteQueryResponse : ArrayList<FetchRemoteQueryResponseItem>()

data class FetchRemoteQueryResponseItem(
    @SerializedName("id")
    @Expose
    val id: Int,
    @SerializedName("level")
    @Expose
    val level: String,
    @SerializedName("mobileNo")
    @Expose
    val mobileNo: String,
    @SerializedName("name")
    @Expose
    val name: String,
    @SerializedName("queryStatus")
    @Expose
    val queryStatus: String,
    @SerializedName("value")
    @Expose
    val value: String
)