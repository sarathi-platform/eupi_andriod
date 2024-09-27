package com.sarathi.dataloadingmangement.network.response

import com.google.gson.annotations.SerializedName

data class ContentResponse(
    @SerializedName("contentId")
    val contentId: String,
    @SerializedName("contentKey")
    val contentKey: String,
    @SerializedName("contentType")

    val contentType: String,
    @SerializedName("contentValue")

    val contentValue: String,
    @SerializedName("contentName")
    val contentName: String?,
    @SerializedName("languageCode")
    val languageCode: String,
    @SerializedName("thumbnail")
    val thumbnail: String?,

) {
}
