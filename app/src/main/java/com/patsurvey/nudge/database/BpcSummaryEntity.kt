package com.patsurvey.nudge.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.patsurvey.nudge.utils.BPC_SUMMARY_TABLE

@Entity(tableName = BPC_SUMMARY_TABLE)
data class BpcSummaryEntity(

    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @SerializedName("cohortCount")
    @Expose
    @ColumnInfo(name = "cohortCount")
    var cohortCount: Int?,

    @SerializedName("mobilisedCount")
    @Expose
    @ColumnInfo(name = "mobilisedCount")
    var mobilisedCount: Int?,

    @SerializedName("poorDidiCount")
    @Expose
    @ColumnInfo(name = "poorDidiCount")
    var poorDidiCount: Int?,

    @SerializedName("sentVoEndorsementCount")
    @Expose
    @ColumnInfo(name = "sentVoEndorsementCount")
    var sentVoEndorsementCount: Int?,

    @SerializedName("voEndorsedCount")
    @Expose
    @ColumnInfo(name = "voEndorsedCount")
    var voEndorsedCount: Int?,

    @SerializedName("villageId")
    @Expose
    @ColumnInfo(name = "villageId")
    var villageId: Int?

) {
    companion object {
        fun getEmptySummary(): BpcSummaryEntity {
            return BpcSummaryEntity(
                id = 0,
                cohortCount = 0,
                mobilisedCount = 0,
                poorDidiCount = 0,
                sentVoEndorsementCount = 0,
                voEndorsedCount = 0,
                villageId = 0
            )
        }
    }
}
