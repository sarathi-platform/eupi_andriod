package com.patsurvey.nudge.model.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.StepListEntity
import com.patsurvey.nudge.utils.calculateMatchPercentage
import com.patsurvey.nudge.utils.getNotAvailableDidiCount

class SaveMatchSummaryRequest(
    @SerializedName("programId")
    @Expose
    val programId: Int,
    @SerializedName("score")
    @Expose
    val score: Int,
    @SerializedName("villageId")
    @Expose
    val villageId: Int,
    @SerializedName("didiNotAvailableCountBPC")
    @Expose
    val didiNotAvailableCountBPC: Int
) {
    companion object {
        fun getSaveMatchSummaryRequestForBpc(villageId: Int, stepListEntity: StepListEntity, didiList: List<DidiEntity>, questionPassionScore: Int) : SaveMatchSummaryRequest {
            return SaveMatchSummaryRequest(
                programId = stepListEntity.programId,
                score = calculateMatchPercentage(didiList, questionPassionScore),
                villageId = villageId,
                didiNotAvailableCountBPC = didiList.getNotAvailableDidiCount()
            )
        }
    }
}