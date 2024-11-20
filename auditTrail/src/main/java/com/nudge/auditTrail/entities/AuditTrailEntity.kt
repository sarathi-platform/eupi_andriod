package com.nudge.auditTrail.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.nudge.core.AUDIT_TRAIL_TABLE
import com.nudge.core.BLANK_STRING
import com.nudge.core.database.converters.DateConverter
import com.nudge.core.json
import com.nudge.core.toDate
import java.util.Date
import java.util.Objects
import java.util.UUID

@Entity(tableName = AUDIT_TRAIL_TABLE)
data class AuditTrailEntity(

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = false)
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo("createdDate")
    @TypeConverters(DateConverter::class)
    val createdDate: Date = System.currentTimeMillis().toDate(),

    @ColumnInfo("modifiedDate")
    @TypeConverters(DateConverter::class)
    val modifiedDate: Date,

    @ColumnInfo("mobileNumber")
    val mobileNumber: String,

    @ColumnInfo("auditData")
    val auditData: String,
)
{
    companion object {
        fun getAuditDetailEvent(map:Map<String,Any>,mobileNo:String): AuditTrailEntity {
            return AuditTrailEntity(
                id = UUID.randomUUID().toString(),
                mobileNumber =mobileNo ,
                modifiedDate = System.currentTimeMillis().toDate(),
                auditData = map.json(),
            )
        }
    }
}



