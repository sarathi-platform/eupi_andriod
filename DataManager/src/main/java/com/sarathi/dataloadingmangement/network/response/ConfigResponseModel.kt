package com.sarathi.dataloadingmangement.network.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sarathi.dataloadingmangement.data.entities.LanguageEntity

data class ConfigResponseModel(
    @SerializedName("languageList")
    @Expose
    val languageList: List<LanguageEntity>,

    @SerializedName("questionImageUrlList")
    @Expose
    val image_profile_link: List<String>,

    )
