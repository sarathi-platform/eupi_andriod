package com.patsurvey.nudge.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.patsurvey.nudge.database.converters.BeneficiaryProcessStatusModel
import com.patsurvey.nudge.database.converters.BeneficiaryStepConverter
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.BPC_SELECTED_DIDI_TABLE
import com.patsurvey.nudge.utils.DidiStatus
import com.patsurvey.nudge.utils.WealthRank

@Entity(tableName = BPC_SELECTED_DIDI_TABLE)
data class BpcSelectedDidiEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: Int,

    @ColumnInfo(name = "serverId")
    var serverId: Int = 0,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "address")
    var address: String,

    @ColumnInfo(name = "guardianName")
    var guardianName: String,

    @ColumnInfo(name = "relationship")
    var relationship: String,

    @ColumnInfo(name = "castId")
    var castId: Int,

    @ColumnInfo(name = "castName")
    var castName: String,

    @ColumnInfo(name = "cohortId")
    var cohortId: Int,

    @ColumnInfo(name = "cohortName")
    var cohortName: String,

    @ColumnInfo(name = "villageId")
    var villageId: Int,

    @ColumnInfo(name = "wealth_ranking")
    var wealth_ranking: String = WealthRank.NOT_RANKED.rank,

    @ColumnInfo(name = "needsToPost")
    var needsToPost: Boolean = true,

    @ColumnInfo(name = "localPath")
    var localPath: String = BLANK_STRING,

    @ColumnInfo(name = "createdDate")
    var createdDate: Long,

    @ColumnInfo(name = "modifiedDate")
    var modifiedDate: Long,

    @ColumnInfo(name = "activeStatus")
    var activeStatus: Int = DidiStatus.DIDI_ACTIVE.ordinal,

    @ColumnInfo(name = "needsToPostDeleteStatus")
    var needsToPostDeleteStatus: Boolean = false,

    @TypeConverters(BeneficiaryStepConverter::class)
    @ColumnInfo(name = "beneficiaryProcessStatus")
    var beneficiaryProcessStatus: List<BeneficiaryProcessStatusModel>? = emptyList(),

    @ColumnInfo(name = "patSurveyStatus")
    var patSurveyStatus: Int = 0,

    @ColumnInfo(name = "section1Status")
    var section1Status: Int = 0,

    @ColumnInfo(name = "section2Status")
    var section2Status: Int = 0,

    @ColumnInfo(name = "bpcScore")
    var bpcScore: Double? = 0.0,

    @ColumnInfo(name = "bpcComment")
    var bpcComment: String,

    @ColumnInfo(name = "crpScore")
    var crpScore: Double? = 0.0,

    @ColumnInfo(name = "crpComment")
    var crpComment: String,

    @ColumnInfo(name = "shgFlag")
    var shgFlag: Int,

    @ColumnInfo(name = "isAlsoSelected")
    var isAlsoSelected: Boolean = false,

    @ColumnInfo(name = "voEndorsementStatus")
    var voEndorsementStatus: Int = 0,

    @ColumnInfo(name = "forVoEndorsement")
    var forVoEndorsement: Int = 0,

    @ColumnInfo(name = "needsToPostPAT")
    var needsToPostPAT: Boolean = false,

    @ColumnInfo(name = "transactionId")
    var transactionId: String? = ""
) {
    companion object {
        fun getSelectedDidiEntityFromNonSelectedEntity(nonSelectedDidiEntity: BpcNonSelectedDidiEntity): BpcSelectedDidiEntity {
            return BpcSelectedDidiEntity(
                id = nonSelectedDidiEntity.id,
                serverId = nonSelectedDidiEntity.serverId,
                name = nonSelectedDidiEntity.name,
                castName = nonSelectedDidiEntity.castName,
                castId = nonSelectedDidiEntity.castId,
                address = nonSelectedDidiEntity.address,
                guardianName = nonSelectedDidiEntity.guardianName,
                relationship = nonSelectedDidiEntity.relationship,
                cohortId = nonSelectedDidiEntity.cohortId,
                cohortName = nonSelectedDidiEntity.cohortName,
                villageId = nonSelectedDidiEntity.villageId,
                wealth_ranking = nonSelectedDidiEntity.wealth_ranking,
                needsToPost = nonSelectedDidiEntity.needsToPost,
                localPath = nonSelectedDidiEntity.localPath,
                createdDate = nonSelectedDidiEntity.createdDate,
                modifiedDate = nonSelectedDidiEntity.modifiedDate,
                activeStatus = nonSelectedDidiEntity.activeStatus,
                needsToPostDeleteStatus = nonSelectedDidiEntity.needsToPostDeleteStatus,
                beneficiaryProcessStatus = nonSelectedDidiEntity.beneficiaryProcessStatus,
                patSurveyStatus = nonSelectedDidiEntity.patSurveyStatus,
                section1Status = nonSelectedDidiEntity.section1Status,
                section2Status = nonSelectedDidiEntity.section2Status,
                shgFlag = nonSelectedDidiEntity.shgFlag,
                voEndorsementStatus = nonSelectedDidiEntity.voEndorsementStatus,
                forVoEndorsement = nonSelectedDidiEntity.forVoEndorsement,
                needsToPostPAT = nonSelectedDidiEntity.needsToPostPAT,
                transactionId = nonSelectedDidiEntity.transactionId,
                bpcScore = nonSelectedDidiEntity.bpcScore,
                bpcComment = nonSelectedDidiEntity.bpcComment,
                crpComment = nonSelectedDidiEntity.crpComment,
                crpScore = nonSelectedDidiEntity.crpScore
            )
        }
    }
}
