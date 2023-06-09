package com.patsurvey.nudge.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.patsurvey.nudge.database.converters.BeneficiaryProcessStatusModel
import com.patsurvey.nudge.database.converters.BeneficiaryStepConverter
import com.patsurvey.nudge.utils.BLANK_STRING

import com.patsurvey.nudge.utils.DIDI_TABLE
import com.patsurvey.nudge.utils.DidiStatus
import com.patsurvey.nudge.utils.WealthRank


@Entity(tableName = DIDI_TABLE)
data class DidiEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: Int,

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

    @ColumnInfo(name = "needsToPostRanking")
    var needsToPostRanking: Boolean = true,

    @TypeConverters(BeneficiaryStepConverter::class)
    @ColumnInfo(name = "beneficiaryProcessStatus")
    var beneficiaryProcessStatus: List<BeneficiaryProcessStatusModel>? = emptyList(),

    @ColumnInfo(name = "patSurveyStatus")
    var patSurveyStatus: Int = 0,

    @ColumnInfo(name = "section1Status")
    var section1Status: Int = 0,

    @ColumnInfo(name = "section2Status")
    var section2Status: Int = 0,

    @ColumnInfo(name = "shgFlag")
    var shgFlag: Int,

    @ColumnInfo(name = "voEndorsementStatus")
    var voEndorsementStatus: Int = 0,

    @ColumnInfo(name = "forVoEndorsement")
    var forVoEndorsement: Int = 0,

    @ColumnInfo(name = "needsToPostPAT")
    var needsToPostPAT: Boolean = false,

    @ColumnInfo(name = "needsToPostVo")
    var needsToPostVo: Boolean = false,

    @ColumnInfo(name = "transactionId")
    var transactionId: String? = ""

)
