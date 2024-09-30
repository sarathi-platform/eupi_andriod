package com.sarathi.dataloadingmangement.model.survey.response

import com.google.gson.annotations.SerializedName

data class ContentList(

    @SerializedName("contentKey") var contentKey: String? = null,
    @SerializedName("contentType") var contentType: String? = null,
    @SerializedName("contentValue") var contentValue: String? = null,
)