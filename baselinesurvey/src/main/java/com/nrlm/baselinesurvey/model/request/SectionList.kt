package com.nrlm.baselinesurvey.model.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SectionList(
    @SerializedName("sectionId")
    @Expose
    var sectionId: Int,

    @SerializedName("sectionName")
    @Expose
    var sectionName: String,

    @SerializedName("sectionStatus")
    @Expose
    var sectionStatus: String
)
