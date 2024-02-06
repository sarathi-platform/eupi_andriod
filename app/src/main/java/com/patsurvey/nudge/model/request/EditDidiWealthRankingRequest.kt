package com.patsurvey.nudge.model.request

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.utils.AbleBodiedFlag
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.BPC_SURVEY_CONSTANT
import com.patsurvey.nudge.utils.BPC_USER_TYPE
import com.patsurvey.nudge.utils.COMPLETED_STRING
import com.patsurvey.nudge.utils.DIDI_NOT_AVAILABLE
import com.patsurvey.nudge.utils.DIDI_REJECTED
import com.patsurvey.nudge.utils.DidiEndorsementStatus
import com.patsurvey.nudge.utils.ExclusionType
import com.patsurvey.nudge.utils.LOW_SCORE
import com.patsurvey.nudge.utils.PAT_SURVEY
import com.patsurvey.nudge.utils.PatSurveyStatus
import com.patsurvey.nudge.utils.SHGFlag
import com.patsurvey.nudge.utils.StepType
import com.patsurvey.nudge.utils.TYPE_EXCLUSION
import com.patsurvey.nudge.utils.VERIFIED_STRING
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

        fun getRequestPayloadForPatScoreSave(didi: DidiEntity, passingMark: Int, isBpcUserType: Boolean): EditDidiWealthRankingRequest {
            val comment = if (didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal || didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal) {
                PatSurveyStatus.NOT_AVAILABLE.name
            } else if (didi.patSurveyStatus == PatSurveyStatus.INPROGRESS.ordinal) {
                BLANK_STRING
            } else {
                if ((didi.patSurveyStatus == PatSurveyStatus.COMPLETED.ordinal && didi.section2Status == PatSurveyStatus.NOT_STARTED.ordinal)
                    || (didi.patSurveyStatus == PatSurveyStatus.COMPLETED.ordinal && didi.patExclusionStatus != ExclusionType.NO_EXCLUSION.ordinal)) {
                    TYPE_EXCLUSION
                } else {
                    if (didi.patSurveyStatus == PatSurveyStatus.COMPLETED.ordinal && didi.section2Status == PatSurveyStatus.COMPLETED.ordinal && didi.score!! < passingMark) {
                        LOW_SCORE
                    } else {
                        BLANK_STRING
                    }
                }
            }

            val result = if (didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal || didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal) {
                DIDI_NOT_AVAILABLE
            } else if (didi.patSurveyStatus == PatSurveyStatus.INPROGRESS.ordinal) {
                PatSurveyStatus.INPROGRESS.name
            } else {
                if (didi.forVoEndorsement == 0 || didi.patExclusionStatus != ExclusionType.NO_EXCLUSION.ordinal) DIDI_REJECTED else {
                    if (isBpcUserType)
                        VERIFIED_STRING
                    else
                        COMPLETED_STRING
                }
            }
            return EditDidiWealthRankingRequest(
                id = if (didi.serverId == 0) didi.id else didi.serverId,
                score = didi.score,
                comment = comment,
                type = if (isBpcUserType) BPC_SURVEY_CONSTANT else PAT_SURVEY,
                result = result,
                rankingEdit = didi.patEdit,
                shgFlag = SHGFlag.fromInt(didi.shgFlag).name,
                ableBodiedFlag = AbleBodiedFlag.fromInt(didi.ableBodiedFlag).name
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
