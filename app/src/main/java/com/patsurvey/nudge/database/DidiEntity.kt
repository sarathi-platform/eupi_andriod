package com.patsurvey.nudge.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.patsurvey.nudge.database.converters.BeneficiaryProcessStatusModel
import com.patsurvey.nudge.database.converters.BeneficiaryStepConverter
import com.patsurvey.nudge.utils.BLANK_STRING

import com.patsurvey.nudge.utils.DIDI_TABLE
import com.patsurvey.nudge.utils.WealthRank


@Entity(tableName = DIDI_TABLE)
data class DidiEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: Int,

    @ColumnInfo(name = "name")
    var name : String,

    @ColumnInfo(name = "address")
    var address : String,

    @ColumnInfo(name = "guardianName")
    var guardianName : String,

    @ColumnInfo(name = "relationship")
    var relationship : String,

    @ColumnInfo(name = "castId")
    var castId : Int,

    @ColumnInfo(name = "castName")
    var castName : String,

    @ColumnInfo(name = "cohortId")
    var cohortId : Int,

    @ColumnInfo(name = "cohortName")
    var cohortName : String,

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

    @ColumnInfo(name = "needsToPostRanking")
    var needsToPostRanking: Boolean = true,

    @TypeConverters(BeneficiaryStepConverter::class)
    @ColumnInfo(name = "beneficiaryProcessStatus")
    var beneficiaryProcessStatus: List<BeneficiaryProcessStatusModel>?= emptyList(),

    @ColumnInfo(name = "patSurveyProgress")
    var patSurveyProgress: Int=0,

    @ColumnInfo(name = "section1")
    var section1: Int=0,

    @ColumnInfo(name = "section2")
    var section2: Int=0,

    @ColumnInfo(name = "shgFlag")
    var shgFlag: Int

    )
