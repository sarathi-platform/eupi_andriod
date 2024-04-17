package com.nrlm.baselinesurvey.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.SURVEYEE_TABLE
import com.nrlm.baselinesurvey.utils.states.SurveyState


@Entity(tableName = SURVEYEE_TABLE)
data class SurveyeeEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: Int,

    @SerializedName("userId")
    @Expose
    @ColumnInfo(name = "userId")
    var userId: Int?,

    @SerializedName("didiId")
    @Expose
    @ColumnInfo(name = "didiId")
    var didiId: Int? = null,

    @SerializedName("didiName")
    @Expose
    @ColumnInfo(name = "didiName")
    var didiName: String,

    @SerializedName("dadaName")
    @Expose
    @ColumnInfo(name = "dadaName")
    var dadaName: String,

    @SerializedName("cohortId")
    @Expose
    @ColumnInfo(name = "cohortId")
    var cohortId: Int,

    @SerializedName("cohortName")
    @Expose
    @ColumnInfo(name = "cohortName")
    var cohortName: String,

    @SerializedName("houseNo")
    @Expose
    @ColumnInfo(name = "houseNo")
    var houseNo: String,

    @SerializedName("villageId")
    @Expose
    @ColumnInfo(name = "villageId")
    var villageId: Int,

    @SerializedName("villageName")
    @Expose
    @ColumnInfo(name = "villageName")
    var villageName: String,

    @SerializedName("comment")
    @Expose
    @ColumnInfo(name = "comment")
    var comment: String = BLANK_STRING,

    @SerializedName("score")
    @Expose
    @ColumnInfo(name = "score")
    var score: Double = 0.0,

    @SerializedName("crpImageName")
    @Expose
    @ColumnInfo(name = "crpImageName")
    var crpImageName: String = BLANK_STRING,

    @SerializedName("crpImageLocalPath")
    @Expose
    @ColumnInfo(name = "crpImageLocalPath")
    var crpImageLocalPath: String = BLANK_STRING,

    @SerializedName("ableBodied")
    @Expose
    @ColumnInfo(name = "ableBodied")
    var ableBodied: String,

    @SerializedName("casteId")
    @Expose
    @ColumnInfo(name = "casteId")
    var casteId: Int,

    @SerializedName("relationship")
    @Expose
    @ColumnInfo(name = "relationship")
    var relationship: String = BLANK_STRING,

    @SerializedName("surveyStatus")
    @Expose
    @ColumnInfo(name = "surveyStatus")
    var surveyStatus: Int = SurveyState.NOT_STARTED.ordinal,

    @SerializedName("movedToThisWeek")
    @Expose
    @ColumnInfo(name = "movedToThisWeek")
    var movedToThisWeek: Boolean = false,
    @SerializedName("voName")
    @Expose
    @ColumnInfo(name = "voName")
    var voName: String = BLANK_STRING

) {
    companion object {
        fun getEmptySurveyeeEntity() = SurveyeeEntity(
            id = 0,
            userId = 101,
            didiId = 1011,
            didiName = "",
            dadaName = "",
            cohortId = 1012,
            cohortName = "",
            houseNo = "",
            villageId = 0,
            villageName = "",
            comment = "",
            score = 0.0,
            crpImageName = "",
            crpImageLocalPath = "",
            ableBodied = "",
            casteId = 0,
            relationship = "",
            surveyStatus = 0,
            movedToThisWeek = false,
            voName = ""
        )
    }
}
