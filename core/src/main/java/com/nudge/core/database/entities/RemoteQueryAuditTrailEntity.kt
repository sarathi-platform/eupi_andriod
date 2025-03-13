package com.nudge.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nudge.core.BLANK_STRING
import com.nudge.core.REMOTE_QUERY_AUDIT_TRAIL_TABLE_NAME
import com.nudge.core.model.RemoteQueryDto

@Entity(REMOTE_QUERY_AUDIT_TRAIL_TABLE_NAME)
data class RemoteQueryAuditTrailEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    val userId: String,
    val databaseName: String,
    val dbVersion: Int,
    val tableName: String,
    val operationType: String,
    val appVersion: String,
    val query: String,
    val executionOrder: Int,
    val level: String,
    val status: String,
    val propertyValueId: Int,
    val errorMessage: String = BLANK_STRING,
    val createdDate: Long = System.currentTimeMillis(),
    val modifiedDate: Long
) {

    companion object {
        fun getRemoteQueryAuditTrailEntity(
            remoteQueryDto: RemoteQueryDto,
            userId: String,
            level: String,
            propertyValueId: Int
        ): RemoteQueryAuditTrailEntity {
            return RemoteQueryAuditTrailEntity(
                userId = userId,
                databaseName = remoteQueryDto.databaseName,
                dbVersion = remoteQueryDto.dbVersion,
                tableName = remoteQueryDto.tableName,
                operationType = remoteQueryDto.operationType,
                appVersion = remoteQueryDto.appVersion,
                query = remoteQueryDto.query,
                status = remoteQueryDto.queryStatus ?: BLANK_STRING,
                level = level,
                executionOrder = remoteQueryDto.executionOrder,
                modifiedDate = System.currentTimeMillis(),
                propertyValueId = propertyValueId
            )
        }
    }

}
