package com.patsurvey.nudge.model.dataModel

import com.google.gson.annotations.SerializedName

data class VillageListModal(
    @SerializedName("village_name")
    val villageName : String,
    @SerializedName("vo_name")
    val voName : String,
    @SerializedName("is_complete")
    val isComplete: Boolean = false
)
