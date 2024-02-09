package com.patsurvey.nudge.model.dataModel

data class RankingEditEvent(
    val villageId: Int,
    val type: String,
    val status: Boolean
)
