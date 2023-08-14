package com.patsurvey.nudge.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.patsurvey.nudge.database.converters.BeneficiaryProcessStatusModel
import com.patsurvey.nudge.database.converters.BeneficiaryStepConverter
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.DIDI_TABLE
import com.patsurvey.nudge.utils.DidiStatus
import com.patsurvey.nudge.utils.WealthRank


@Entity(tableName = DIDI_TABLE)
data class DidiEntity(
    @PrimaryKey
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: Int,

    @SerializedName("localUniqueId")
    @Expose
    @ColumnInfo(name = "localUniqueId")
    var localUniqueId : String = "",

    @SerializedName("serverId")
    @Expose
    @ColumnInfo(name = "serverId")
    var serverId: Int = 0,

    @SerializedName("name")
    @Expose
    @ColumnInfo(name = "name")
    var name: String,

    @SerializedName("address")
    @Expose
    @ColumnInfo(name = "address")
    var address: String,

    @SerializedName("guardianName")
    @Expose
    @ColumnInfo(name = "guardianName")
    var guardianName: String,

    @SerializedName("relationship")
    @Expose
    @ColumnInfo(name = "relationship")
    var relationship: String,

    @SerializedName("castId")
    @Expose
    @ColumnInfo(name = "castId")
    var castId: Int,

    @SerializedName("castName")
    @Expose
    @ColumnInfo(name = "castName")
    var castName: String,

    @SerializedName("cohortId")
    @Expose
    @ColumnInfo(name = "cohortId")
    var cohortId: Int,

    @SerializedName("cohortName")
    @Expose
    @ColumnInfo(name = "cohortName")
    var cohortName: String,

    @SerializedName("villageId")
    @Expose
    @ColumnInfo(name = "villageId")
    var villageId: Int,

    @SerializedName("wealth_ranking")
    @Expose
    @ColumnInfo(name = "wealth_ranking")
    var wealth_ranking: String = WealthRank.NOT_RANKED.rank,

    @SerializedName("needsToPost")
    @Expose
    @ColumnInfo(name = "needsToPost")
    var needsToPost: Boolean = true,

    @SerializedName("localPath")
    @Expose
    @ColumnInfo(name = "localPath")
    var localPath: String = BLANK_STRING,

    @SerializedName("createdDate")
    @Expose
    @ColumnInfo(name = "createdDate")
    var createdDate: Long?=0,

    @SerializedName("modifiedDate")
    @Expose
    @ColumnInfo(name = "modifiedDate")
    var modifiedDate: Long?=0,

    @SerializedName("localCreatedDate")
    @Expose
    @ColumnInfo(name = "localCreatedDate")
    var localCreatedDate: Long?=0,

    @SerializedName("localModifiedDate")
    @Expose
    @ColumnInfo(name = "localModifiedDate")
    var localModifiedDate: Long?=0,

    @SerializedName("activeStatus")
    @Expose
    @ColumnInfo(name = "activeStatus")
    var activeStatus: Int = DidiStatus.DIDI_ACTIVE.ordinal,

    @SerializedName("needsToPostDeleteStatus")
    @Expose
    @ColumnInfo(name = "needsToPostDeleteStatus")
    var needsToPostDeleteStatus: Boolean = false,

    @SerializedName("needsToPostRanking")
    @Expose
    @ColumnInfo(name = "needsToPostRanking")
    var needsToPostRanking: Boolean = false,

    @SerializedName("beneficiaryProcessStatus")
    @Expose
    @TypeConverters(BeneficiaryStepConverter::class)
    @ColumnInfo(name = "beneficiaryProcessStatus")
    var beneficiaryProcessStatus: List<BeneficiaryProcessStatusModel>? = emptyList(),

    @SerializedName("patSurveyStatus")
    @Expose
    @ColumnInfo(name = "patSurveyStatus")
    var patSurveyStatus: Int = 0,

    @SerializedName("section1Status")
    @Expose
    @ColumnInfo(name = "section1Status")
    var section1Status: Int = 0,

    @SerializedName("section2Status")
    @Expose
    @ColumnInfo(name = "section2Status")
    var section2Status: Int = 0,

    @SerializedName("shgFlag")
    @Expose
    @ColumnInfo(name = "shgFlag")
    var shgFlag: Int,

    @SerializedName("voEndorsementStatus")
    @Expose
    @ColumnInfo(name = "voEndorsementStatus")
    var voEndorsementStatus: Int = 0,

    @SerializedName("forVoEndorsement")
    @Expose
    @ColumnInfo(name = "forVoEndorsement")
    var forVoEndorsement: Int = 0,

    @SerializedName("needsToPostPAT")
    @Expose
    @ColumnInfo(name = "needsToPostPAT")
    var needsToPostPAT: Boolean = false,

    @SerializedName("needsToPostBPCProcessStatus")
    @Expose
    @ColumnInfo(name = "needsToPostBPCProcessStatus")
    var needsToPostBPCProcessStatus: Boolean = false,

    @SerializedName("needsToPostVo")
    @Expose
    @ColumnInfo(name = "needsToPostVo")
    var needsToPostVo: Boolean = false,

    @SerializedName("transactionId")
    @Expose
    @ColumnInfo(name = "transactionId")
    var transactionId: String? = "",

    @SerializedName("score")
    @Expose
    @ColumnInfo(name = "score")
    var score: Double? = 0.0,

    @SerializedName("crpScore")
    @Expose
    @ColumnInfo(name = "crpScore")
    var crpScore: Double? = 0.0,

    @SerializedName("bpcScore")
    @Expose
    @ColumnInfo(name = "bpcScore")
    var bpcScore: Double? = 0.0,

    @SerializedName("bpcComment")
    @Expose
    @ColumnInfo(name = "bpcComment")
    var bpcComment: String? = BLANK_STRING,

    @SerializedName("crpComment")
    @Expose
    @ColumnInfo(name = "crpComment")
    var crpComment: String? = BLANK_STRING,

    @SerializedName("comment")
    @Expose
    @ColumnInfo(name = "comment")
    var comment: String? = BLANK_STRING,

    @SerializedName("isDidiAccepted")
    @Expose
    @ColumnInfo(name = "isDidiAccepted")
    var isDidiAccepted: Boolean = false,

    @SerializedName("isExclusionYesSelected")
    @Expose
    @ColumnInfo(name = "isExclusionYesSelected")
    var isExclusionYesSelected: Int = 0,

    @SerializedName("crpUploadedImage")
    @Expose
    @ColumnInfo(name = "crpUploadedImage")
    var crpUploadedImage: String? = BLANK_STRING,

    @SerializedName("needsToPostImage")
    @Expose
    @ColumnInfo(name = "needsToPostImage")
    var needsToPostImage: Boolean = false,

    @SerializedName("rankingEdit")
    @Expose
    @ColumnInfo(name = "rankingEdit")
    var rankingEdit: Boolean = true,

    @SerializedName("patEdit")
    @Expose
    @ColumnInfo(name = "patEdit")
    var patEdit: Boolean = true

){
    companion object{
        fun getDidiId(didiEntity: DidiEntity)=if(didiEntity.serverId  == 0) didiEntity.id else didiEntity.serverId
        fun getDidiEntityFromSelectedDidiEntityForBpc(selectedDidiEntity: BpcSelectedDidiEntity): DidiEntity {
            return DidiEntity(
                id = selectedDidiEntity.id,
                serverId = selectedDidiEntity.serverId,
                name = selectedDidiEntity.name,
                address = selectedDidiEntity.address,
                guardianName = selectedDidiEntity.guardianName,
                relationship = selectedDidiEntity.relationship,
                castId = selectedDidiEntity.castId,
                castName = selectedDidiEntity.castName,
                cohortId = selectedDidiEntity.cohortId,
                cohortName = selectedDidiEntity.cohortName,
                villageId = selectedDidiEntity.villageId,
                wealth_ranking = selectedDidiEntity.wealth_ranking,
                needsToPost = selectedDidiEntity.needsToPost,
                localPath = selectedDidiEntity.localPath,
                createdDate = selectedDidiEntity.createdDate,
                modifiedDate = selectedDidiEntity.modifiedDate,
                activeStatus = selectedDidiEntity.activeStatus,
                patSurveyStatus = selectedDidiEntity.patSurveyStatus,
                section1Status = selectedDidiEntity.section1Status,
                section2Status = selectedDidiEntity.section2Status,
                beneficiaryProcessStatus = selectedDidiEntity.beneficiaryProcessStatus,
                shgFlag = selectedDidiEntity.shgFlag,
                score = selectedDidiEntity.bpcScore,
                comment = selectedDidiEntity.bpcComment,
                bpcComment = selectedDidiEntity.bpcComment,
                bpcScore = selectedDidiEntity.bpcScore,
                crpScore = selectedDidiEntity.crpScore,
                crpComment = selectedDidiEntity.crpComment,
                crpUploadedImage = selectedDidiEntity.crpUploadedImage,
                localUniqueId = ""
            )
        }
        fun getDidiEntityFromNonSelectedDidiEntityForBpc(nonSelectedDidiEntity: BpcNonSelectedDidiEntity): DidiEntity {
            return DidiEntity(
                id = nonSelectedDidiEntity.id,
                serverId = nonSelectedDidiEntity.serverId,
                name = nonSelectedDidiEntity.name,
                address = nonSelectedDidiEntity.address,
                guardianName = nonSelectedDidiEntity.guardianName,
                relationship = nonSelectedDidiEntity.relationship,
                castId = nonSelectedDidiEntity.castId,
                castName = nonSelectedDidiEntity.castName,
                cohortId = nonSelectedDidiEntity.cohortId,
                cohortName = nonSelectedDidiEntity.cohortName,
                villageId = nonSelectedDidiEntity.villageId,
                wealth_ranking = nonSelectedDidiEntity.wealth_ranking,
                needsToPost = nonSelectedDidiEntity.needsToPost,
                localPath = nonSelectedDidiEntity.localPath,
                createdDate = nonSelectedDidiEntity.createdDate,
                modifiedDate = nonSelectedDidiEntity.modifiedDate,
                activeStatus = nonSelectedDidiEntity.activeStatus,
                patSurveyStatus = nonSelectedDidiEntity.patSurveyStatus,
                section1Status = nonSelectedDidiEntity.section1Status,
                section2Status = nonSelectedDidiEntity.section2Status,
                beneficiaryProcessStatus = nonSelectedDidiEntity.beneficiaryProcessStatus,
                shgFlag = nonSelectedDidiEntity.shgFlag,
                score = nonSelectedDidiEntity.bpcScore,
                comment = nonSelectedDidiEntity.bpcComment,
                bpcComment = nonSelectedDidiEntity.bpcComment,
                bpcScore = nonSelectedDidiEntity.bpcScore,
                crpScore = nonSelectedDidiEntity.crpScore,
                crpComment = nonSelectedDidiEntity.crpComment,
                crpUploadedImage = nonSelectedDidiEntity.crpUploadedImage,
                localUniqueId = ""
            )
        }
        fun getDidiEntityFromSelectedDidiEntityForCrp(selectedDidiEntity: BpcSelectedDidiEntity): DidiEntity {
            return DidiEntity(
                id = selectedDidiEntity.id,
                serverId = selectedDidiEntity.serverId,
                name = selectedDidiEntity.name,
                address = selectedDidiEntity.address,
                guardianName = selectedDidiEntity.guardianName,
                relationship = selectedDidiEntity.relationship,
                castId = selectedDidiEntity.castId,
                castName = selectedDidiEntity.castName,
                cohortId = selectedDidiEntity.cohortId,
                cohortName = selectedDidiEntity.cohortName,
                villageId = selectedDidiEntity.villageId,
                wealth_ranking = selectedDidiEntity.wealth_ranking,
                needsToPost = selectedDidiEntity.needsToPost,
                localPath = selectedDidiEntity.localPath,
                createdDate = selectedDidiEntity.createdDate,
                modifiedDate = selectedDidiEntity.modifiedDate,
                activeStatus = selectedDidiEntity.activeStatus,
                patSurveyStatus = selectedDidiEntity.patSurveyStatus,
                section1Status = selectedDidiEntity.section1Status,
                section2Status = selectedDidiEntity.section2Status,
                beneficiaryProcessStatus = selectedDidiEntity.beneficiaryProcessStatus,
                shgFlag = selectedDidiEntity.shgFlag,
                bpcComment = selectedDidiEntity.bpcComment,
                bpcScore = selectedDidiEntity.bpcScore,
                crpScore = selectedDidiEntity.crpScore,
                crpComment = selectedDidiEntity.crpComment,
                crpUploadedImage = selectedDidiEntity.crpUploadedImage,
                localUniqueId = ""
            )
        }
    }
}
