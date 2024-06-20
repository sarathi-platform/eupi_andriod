package com.sarathi.dataloadingmangement.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class SmallGroupMappingResponseModel(
    @SerializedName("beneficiaryCount")
    @Expose
    val beneficiaryCount: Int,
    @SerializedName("beneficiaryIds")
    @Expose
    val beneficiaryIds: List<Int>,
    @SerializedName("id")
    @Expose
    val id: Int,
    @SerializedName("name")
    @Expose
    val name: String,
    @SerializedName("status")
    @Expose
    val status: Int
)