package com.patsurvey.nudge.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.patsurvey.nudge.utils.BPC_SUMMARY_TABLE

@Entity(tableName = BPC_SUMMARY_TABLE)
data class BpcSummaryEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "cohortCount")
    var cohortCount: Int?,

    @ColumnInfo(name = "mobilisedCount")
    var mobilisedCount: Int?,

    @ColumnInfo(name = "poorDidiCount")
    var poorDidiCount: Int?,

    @ColumnInfo(name = "sentVoEndorsementCount")
    var sentVoEndorsementCount: Int?,

    @ColumnInfo(name = "voEndorsedCount")
    var voEndorsedCount: Int?,

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
