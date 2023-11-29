package com.patsurvey.nudge.database.service.csv.adapter

import androidx.room.PrimaryKey
import com.opencsv.bean.CsvBindByName
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.service.csv.Exportable
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.DidiStatus
import com.patsurvey.nudge.utils.WealthRank

data class DidiTableCSV(
    @CsvBindByName(column = "id")
    var id: Int,

    @CsvBindByName(column = "localUniqueId")
    var localUniqueId : String = "",

    @CsvBindByName(column = "serverId")
    var serverId: Int = 0,

    @CsvBindByName(column = "name")
    var name: String,

    @CsvBindByName(column = "address")
    var address: String,

    @CsvBindByName(column = "guardianName")
    var guardianName: String,

    @CsvBindByName(column = "relationship")
    var relationship: String,

    @CsvBindByName(column = "castId")
    var castId: Int,

    @CsvBindByName(column = "castName")
    var castName: String,

    @CsvBindByName(column = "cohortId")
    var cohortId: Int,

    @CsvBindByName(column = "cohortName")
    var cohortName: String,

    @CsvBindByName(column = "villageId")
    var villageId: Int,

    @CsvBindByName(column = "wealth_ranking")
    var wealth_ranking: String = WealthRank.NOT_RANKED.rank,

    @CsvBindByName(column = "needsToPost")
    var needsToPost: Boolean = true,

    @CsvBindByName(column = "localPath")
    var localPath: String = BLANK_STRING,

    @CsvBindByName(column = "createdDate")
    var createdDate: Long?=0,

    @CsvBindByName(column = "modifiedDate")
    var modifiedDate: Long?=0,

    @CsvBindByName(column = "localCreatedDate")
    var localCreatedDate: Long?=0,

    @CsvBindByName(column = "localModifiedDate")
    var localModifiedDate: Long?=0,

    @CsvBindByName(column = "activeStatus")
    var activeStatus: Int = DidiStatus.DIDI_ACTIVE.ordinal,

    @CsvBindByName(column = "needsToPostDeleteStatus")
    var needsToPostDeleteStatus: Boolean = false,

    @CsvBindByName(column = "needsToPostRanking")
    var needsToPostRanking: Boolean = false,

    @CsvBindByName(column = "patSurveyStatus")
    var patSurveyStatus: Int = 0,

    @CsvBindByName(column = "section1Status")
    var section1Status: Int = 0,

    @CsvBindByName(column = "section2Status")
    var section2Status: Int = 0,

    @CsvBindByName(column = "shgFlag")
    var shgFlag: Int,

    @CsvBindByName(column = "voEndorsementStatus")
    var voEndorsementStatus: Int = 0,

    @CsvBindByName(column = "forVoEndorsement")
    var forVoEndorsement: Int = 0,

    @CsvBindByName(column = "needsToPostPAT")
    var needsToPostPAT: Boolean = false,

    @CsvBindByName(column = "needsToPostBPCProcessStatus")
    var needsToPostBPCProcessStatus: Boolean = false,

    @CsvBindByName(column = "needsToPostVo")
    var needsToPostVo: Boolean = false,

    @CsvBindByName(column = "transactionId")
    var transactionId: String? = "",

    @CsvBindByName(column = "score")
    var score: Double? = 0.0,

    @CsvBindByName(column = "crpScore")
    var crpScore: Double? = 0.0,

    @CsvBindByName(column = "bpcScore")
    var bpcScore: Double? = 0.0,

    @CsvBindByName(column = "bpcComment")
    var bpcComment: String? = BLANK_STRING,

    @CsvBindByName(column = "crpComment")
    var crpComment: String? = BLANK_STRING,

    @CsvBindByName(column = "comment")
    var comment: String? = BLANK_STRING,

    @CsvBindByName(column = "isDidiAccepted")
    var isDidiAccepted: Boolean = false,

    @CsvBindByName(column = "patExclusionStatus")
    var patExclusionStatus: Int = 0,

    @CsvBindByName(column = "crpUploadedImage")
    var crpUploadedImage: String? = BLANK_STRING,

    @CsvBindByName(column = "needsToPostImage")
    var needsToPostImage: Boolean = false,

    @CsvBindByName(column = "rankingEdit")
    var rankingEdit: Boolean = true,

    @CsvBindByName(column = "patEdit")
    var patEdit: Boolean = true,

    @CsvBindByName(column = "voEndorsementEdit")
    var voEndorsementEdit: Boolean = true,

    @CsvBindByName(column = "ableBodiedFlag")
    var ableBodiedFlag: Int

) : Exportable

fun List<DidiEntity>.toCsv() : List<DidiTableCSV> = map {
    DidiTableCSV(
        id = it.id,
        serverId = it.serverId,
        localUniqueId = it.localUniqueId,
        name = it.name,
        address = it.address,
        guardianName = it.guardianName,
        relationship = it.relationship,
        castId = it.castId,
        castName = it.castName,
        cohortId = it.cohortId,
        cohortName = it.cohortName,
        villageId = it.villageId,
        wealth_ranking = it.wealth_ranking,
        needsToPost = it.needsToPost,
        localPath = it.localPath,
        createdDate = it.createdDate,
        modifiedDate = it.modifiedDate,
        localCreatedDate = it.localCreatedDate,
        localModifiedDate = it.localModifiedDate,
        activeStatus = it.activeStatus,
        needsToPostDeleteStatus = it.needsToPostDeleteStatus,
        needsToPostRanking = it.needsToPostRanking,
        patExclusionStatus = it.patExclusionStatus,
        patSurveyStatus = it.patSurveyStatus,
        section1Status = it.section1Status,
        section2Status = it.section2Status,
        shgFlag = it.shgFlag,
        voEndorsementStatus = it.voEndorsementStatus,
        forVoEndorsement = it.forVoEndorsement,
        needsToPostPAT = it.needsToPostPAT,
        needsToPostBPCProcessStatus = it.needsToPostBPCProcessStatus,
        needsToPostVo = it.needsToPostVo,
        transactionId = it.transactionId,
        score = it.score,
        comment = it.comment,
        crpScore = it.crpScore,
        bpcScore = it.bpcScore,
        crpComment = it.crpComment,
        bpcComment = it.bpcComment,
        isDidiAccepted = it.isDidiAccepted,
        crpUploadedImage = it.crpUploadedImage,
        needsToPostImage = it.needsToPostImage,
        rankingEdit = it.rankingEdit,
        patEdit = it.patEdit,
        voEndorsementEdit = it.voEndorsementEdit,
        ableBodiedFlag = it.ableBodiedFlag
    )
}