package com.patsurvey.nudge.model.dataModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.PAT_SURVEY
import com.patsurvey.nudge.utils.TYPE_EXCLUSION

data class PATDidiStatusModel(
    @SerializedName("id")
    @Expose
    val id:Int,

    @SerializedName("serverId")
    @Expose
    val serverId:Int,

    @SerializedName("patSurveyStatus")
    @Expose
    var patSurveyStatus: Int=0,

    @SerializedName("villageId")
    @Expose
    var villageId: Int=0,

    @SerializedName("section1Status")
    @Expose
    var section1Status: Int=0,

    @SerializedName("section2Status")
    @Expose
    var section2Status: Int=0,

    @SerializedName("name")
    @Expose
    var name: String= BLANK_STRING,

    @SerializedName("type")
    @Expose
    var type: String?= PAT_SURVEY,

    @SerializedName("forVoEndorsement")
    @Expose
    var forVoEndorsement: Int= 0,

    @SerializedName("score")
    @Expose
    var score: Double= 0.0,

    @SerializedName("comment")
    @Expose
    var comment: String ?= TYPE_EXCLUSION,

    @SerializedName("shgFlag")
    @Expose
    var shgFlag: Int,

    @SerializedName("patEdit")
    @Expose
    var patEdit: Boolean,

    @SerializedName("patExclusionStatus")
    @Expose
    var patExclusionStatus: Int=0,

    @SerializedName("ableBodiedFlag")
    @Expose
    var ableBodiedFlag: Int,

)
