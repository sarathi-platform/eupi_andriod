package com.sarathi.dataloadingmangement.network.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ContentResponse(
    @SerializedName("contentId")
    @Expose
    val contentId: String,
    @Expose
    @SerializedName("contentKey")
    val contentKey: String,
    @Expose
    @SerializedName("contentType")
    val contentType: String,
    @SerializedName("contentValue")
    @Expose
    val contentValue: String,
    @SerializedName("contentName")
    @Expose
    val contentName: String?,
    @SerializedName("languageCode")
    @Expose
    val languageCode: String,
    @SerializedName("thumbnail")
    @Expose
    val thumbnail: String?,
    @SerializedName("description")
    @Expose
    val description: String?,
    @SerializedName("title")
    @Expose
    val title: String?,
    @SerializedName("thumbUrl")
    @Expose
    val thumbUrl: String?
) {
}
