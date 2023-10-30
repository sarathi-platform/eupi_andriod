package com.nrlm.baselinesurvey.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.DIDI_TABLE
import com.nrlm.baselinesurvey.database.converters.BeneficiaryStepConverter
import com.nrlm.baselinesurvey.model.datamodel.BeneficiaryProcessStatusModel


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
    var wealth_ranking: String = "POOR",

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
    var activeStatus: Int = 2,

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

    @SerializedName("patExclusionStatus")
    @Expose
    @ColumnInfo(name = "patExclusionStatus")
    var patExclusionStatus: Int = 0,

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
    var patEdit: Boolean = true,

    @SerializedName("voEndorsementEdit")
    @Expose
    @ColumnInfo(name = "voEndorsementEdit")
    var voEndorsementEdit: Boolean = true,

    @SerializedName("ableBodiedFlag")
    @Expose
    @ColumnInfo(name = "ableBodiedFlag")
    var ableBodiedFlag: Int

){
    companion object{
        fun getDidiId(didiEntity: DidiEntity)=if(didiEntity.serverId  == 0) didiEntity.id else didiEntity.serverId
    }
}
