package com.nudge.auditTrail.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.nudge.auditTrail.AuditTrailEventSyncStatus
import com.nudge.core.AUDIT_TRAIL_TABLE
import com.nudge.core.database.converters.DateConverter
import com.nudge.core.json
import com.nudge.core.toDate
import java.util.Date
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
    val auditData: String?,

    @ColumnInfo("actionStatus")
    val actionStatus: String?,

    @ColumnInfo("actionType")
    val actionType: String?,

    @ColumnInfo("message")
    val message: String?,

    @ColumnInfo("syncStatus")
    val syncStatus: String = AuditTrailEventSyncStatus.NOT_STARTED.name
) {
    companion object {
        fun getAuditDetailEvent(
            map: Map<String, Any>,
            mobileNo: String,
            actionType: String,
            actionStatusType: String,
            message:String
        ): AuditTrailEntity {
            return AuditTrailEntity(
                id = UUID.randomUUID().toString(),
                mobileNumber = mobileNo,
                modifiedDate = System.currentTimeMillis().toDate(),
                auditData = map.json(),
                actionType = actionType,
                actionStatus = actionStatusType,
                message = message
            )
        }
    }
}



