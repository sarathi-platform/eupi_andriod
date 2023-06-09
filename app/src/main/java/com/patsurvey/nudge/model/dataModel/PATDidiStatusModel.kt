package com.patsurvey.nudge.model.dataModel

import androidx.room.ColumnInfo
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.patsurvey.nudge.utils.BLANK_STRING

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
    var name: String= BLANK_STRING

)
