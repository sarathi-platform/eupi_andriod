package com.sarathi.dataloadingmangement.model.events

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ShgVerificationEventPayloadModel(
    @SerializedName("subjectId")
    @Expose
    val subjectId: Int,
    @SerializedName("shgVerificationStatus")
    @Expose
    val shgVerificationStatus: String? = null,
    @SerializedName("shgVerificationDate")
    @Expose
    val shgVerificationDate: Long? = null,
    @SerializedName("shgName")
    @Expose
    val shgName: String? = null,
    @SerializedName("shgMemberId")
    @Expose
    val shgMemberId: String? = null,
    @SerializedName("shgCode")
    @Expose
    val shgCode: String? = null
)
