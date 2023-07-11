package com.patsurvey.nudge.model.dataModel

import androidx.room.ColumnInfo
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

    @ColumnInfo(name = "patSurveyStatus")
    var patSurveyStatus: Int=0,

    @ColumnInfo(name = "section1Status")
    var section1Status: Int=0,

    @ColumnInfo(name = "section2Status")
    var section2Status: Int=0,

    @ColumnInfo(name = "name")
    var name: String= BLANK_STRING,

    @ColumnInfo(name = "type")
    var type: String?= PAT_SURVEY,

    @ColumnInfo(name = "forVoEndorsement")
    var forVoEndorsement: Int= 0,

    @ColumnInfo(name = "score")
    var score: Double= 0.0,

    @ColumnInfo(name = "comment")
    var comment: String ?= TYPE_EXCLUSION,

    @ColumnInfo(name = "shgFlag")
    var shgFlag: Int


)
