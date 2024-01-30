package com.patsurvey.nudge.model.request

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.DidiEndorsementStatus
import com.patsurvey.nudge.utils.StepType
import com.patsurvey.nudge.utils.VoEndorsementStatus


data class EditDidiWealthRankingRequest(
    @SerializedName("id") var id: Int,
    @SerializedName("type") var type: String,
    @SerializedName("result") var result: String,
    @SerializedName("score") var score: Double?=0.0,
    @SerializedName("comment") var comment: String?= BLANK_STRING,
    @SerializedName("localModifiedDate") var localModifiedDate: Long?=0,
    @SerializedName("rankingEdit") var rankingEdit: Boolean = true,
    @SerializedName("shgFlag") var shgFlag: String? = BLANK_STRING,
    @SerializedName("ableBodiedFlag") var ableBodiedFlag: String? = BLANK_STRING,

    @SerializedName("name") var name: String = "",
    @SerializedName("guardianName") var guardianName: String = "",
    @SerializedName("address") var address: String = "",
    @SerializedName("cohortId") var cohortId: Int = -1
) {

    companion object {
        fun getRequestPayloadForWealthRanking(didiEntity: DidiEntity): EditDidiWealthRankingRequest {
            return EditDidiWealthRankingRequest(
                id = didiEntity.id,
                name = didiEntity.name,
                guardianName = didiEntity.guardianName,
                address = didiEntity.address,
                cohortId = didiEntity.cohortId,
                type = StepType.WEALTH_RANKING.name,
                result = didiEntity.wealth_ranking,
                rankingEdit = didiEntity.rankingEdit,
                localModifiedDate = System.currentTimeMillis()
            )
        }

        fun getRequestPayloadForVoEndorsement(didiEntity: DidiEntity): EditDidiWealthRankingRequest {
            return EditDidiWealthRankingRequest(
                id = didiEntity.id,
                name = didiEntity.name,
                guardianName = didiEntity.guardianName,
                address = didiEntity.address,
                cohortId = didiEntity.cohortId,
                type = StepType.VO_ENDROSEMENT.name,
                result = DidiEndorsementStatus.fromIntToString(didiEntity.voEndorsementStatus),
                rankingEdit = didiEntity.rankingEdit,
                localModifiedDate = System.currentTimeMillis()
            )
        }
    }

    fun toJson() : JsonObject {
        val jsonObject = JsonObject()
        jsonObject.addProperty("id", id)
        jsonObject.addProperty("type", type)
        jsonObject.addProperty("result", result)
        return jsonObject
    }
}
