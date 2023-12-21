package com.nrlm.baselinesurvey.model.datamodel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.NO_SECTION
import com.nrlm.baselinesurvey.model.response.ContentList
import com.nrlm.baselinesurvey.model.response.QuestionList

data class Sections(
    @SerializedName("sectionId")
    @Expose
    val sectionId: Int = 0,

    @SerializedName("sectionName")
    @Expose
    val sectionName: String = NO_SECTION,

    @SerializedName("sectionOrder")
    @Expose
    val sectionOrder: Int = 1,

    @SerializedName("sectionDetails")
    @Expose
    val sectionDetails: String = BLANK_STRING,

    @SerializedName("sectionIcon")
    @Expose
    val sectionIcon: String = BLANK_STRING,
//    val sectionIcon: Int = 0,

    @SerializedName("contentList")
    @Expose
    val contentList: List<ContentList>,

    @SerializedName("questionList")
    @Expose
    val questionList: List<QuestionList?> = listOf()
)