package com.sarathi.dataloadingmangement.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nudge.core.BLANK_STRING
import com.nudge.core.value
import com.sarathi.dataloadingmangement.model.response.DidiDetailList
import com.sarathi.dataloadingmangement.util.SUBJECT_TABLE

@Entity(tableName = SUBJECT_TABLE)
data class SubjectEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: Int,

    @SerializedName("userId")
    @Expose
    @ColumnInfo(name = "userId")
    var userId: String?,

    @SerializedName("subjectId")
    @Expose
    @ColumnInfo(name = "subjectId")
    var subjectId: Int? = null,

    @SerializedName("subjectName")
    @Expose
    @ColumnInfo(name = "subjectName")
    var subjectName: String,

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

    @SerializedName("voName")
    @Expose
    @ColumnInfo(name = "voName")
    var voName: String = BLANK_STRING

) {

    companion object {

        fun getSubjectEntityFromResponse(
            didiDetailList: DidiDetailList,
            userId: String
        ): SubjectEntity {

            return SubjectEntity(
                id = 0,
                userId = userId,
                subjectId = didiDetailList.didiId,
                subjectName = didiDetailList.didiName.value(),
                dadaName = didiDetailList.dadaName.value(),
                cohortId = didiDetailList.cohortId.value(),
                cohortName = didiDetailList.cohortName.value(),
                houseNo = didiDetailList.houseNo.value(),
                villageId = didiDetailList.villageId.value(),
                villageName = didiDetailList.villageName.value(),
                crpImageName = didiDetailList.crpImageName.value(),
                relationship = didiDetailList.relationship.value(),
                voName = didiDetailList.voName.value(),
                casteId = didiDetailList.casteId.value(),
                ableBodied = didiDetailList.ableBodied.value()
            )

        }

    }

}

fun SubjectEntity.getSubtitle(): String {
    return "#${this.houseNo}, ${this.dadaName}"
}