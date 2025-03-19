package com.sarathi.dataloadingmangement.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nudge.core.BLANK_STRING
import com.nudge.core.SHG_VERIFICATION_STATUS_NOT_VERIFIED
import com.nudge.core.value
import com.sarathi.dataloadingmangement.SUBJECT_TABLE
import com.sarathi.dataloadingmangement.model.response.DidiDetailList

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
    var voName: String = BLANK_STRING,

    @SerializedName("shgVerificationStatus")
    @Expose
    @ColumnInfo("shgVerificationStatus")
    var shgVerificationStatus: String?,

    @SerializedName("shgVerificationDate")
    @Expose
    @ColumnInfo("shgVerificationDate")
    var shgVerificationDate: Long?,

    @SerializedName("shgName")
    @Expose
    @ColumnInfo(name = "shgName")
    var shgName: String?,

    @SerializedName("shgCode")
    @Expose
    @ColumnInfo(name = "shgCode")
    var shgCode: String?,

    @SerializedName("shgMemberId")
    @Expose
    @ColumnInfo(name = "shgMemberId")
    var shgMemberId: Int?

) {

    companion object {

        fun getSubjectEntityFromResponse(
            didiDetailList: DidiDetailList,
            userId: String,
            crpImageLocalPath: String? = BLANK_STRING,
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
                ableBodied = didiDetailList.ableBodied.value(),
                crpImageLocalPath = crpImageLocalPath ?: BLANK_STRING,
                shgVerificationStatus = didiDetailList.shgVerificationStatus
                    ?: SHG_VERIFICATION_STATUS_NOT_VERIFIED,
                shgVerificationDate = didiDetailList.shgVerificationDate,
                shgName = didiDetailList.shgName,
                shgCode = didiDetailList.shgCode,
                shgMemberId = didiDetailList.shgMemberId
            )

        }

    }

}

data class ShgVerificationDataModel(
    val subjectId: Int,
    val shgVerificationStatus: String? = null,
    val shgVerificationDate: Long? = null,
    val shgName: String? = null,
    val shgMemberId: Int? = null,
    val shgCode: String? = null
)

fun SubjectEntity.getSubtitle(): String {
    return "#${this.houseNo}, ${this.dadaName}"
}