package com.patsurvey.nudge.model.dataModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DidiRankedModel(
    @SerializedName("id")
    @Expose
    val id:Int,
    @SerializedName("name")
    @Expose
    val name:String,

    @SerializedName("rank")
    @Expose
    val rank:String,

    @SerializedName("image")
    @Expose
    val pic:String,

    @SerializedName("isOpen")
    @Expose
    var isOpen:Boolean=false,

)
