package com.patsurvey.nudge.activities.backup.domain.repository

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteStatement
import com.nudge.core.database.entities.RemoteQueryAuditTrailEntity

interface RemoteQueryExecutionRepository {

    fun checkIfQueryIsValid(query: String, isUserIdCheckRequired: Boolean = true): Boolean

    suspend fun getRemoteQuery(): List<RemoteQueryAuditTrailEntity>

    suspend fun executeQuery(remoteQueryAuditTrail: RemoteQueryAuditTrailEntity)

    suspend fun getSQLiteStatement(
        database: RoomDatabase,
        remoteQueryAuditTrail: RemoteQueryAuditTrailEntity
    ): SupportSQLiteStatement?

    suspend fun executeInsertState(
        remoteQueryAuditTrail: RemoteQueryAuditTrailEntity,
        supportSQLiteStatement: SupportSQLiteStatement
    ): Long

    suspend fun executeUpdateDeleteStatement(
        remoteQueryAuditTrail: RemoteQueryAuditTrailEntity,
        supportSQLiteStatement: SupportSQLiteStatement
    ): Int

    fun getCleanQuery(query: String): String

    fun isUserIdCheckNotRequired(remoteQueryAuditTrail: RemoteQueryAuditTrailEntity): Boolean

    fun getDatabaseForQueryExecution(remoteQueryAuditTrail: RemoteQueryAuditTrailEntity): RoomDatabase?

    fun isDatabaseVersionValidForExecution(remoteQueryAuditTrail: RemoteQueryAuditTrailEntity): Boolean

    fun runAppVersionCheck(remoteQueryAuditTrail: RemoteQueryAuditTrailEntity): Boolean

    suspend fun isQueryAlreadyExecuted(remoteQueryAuditTrail: RemoteQueryAuditTrailEntity): Boolean


    fun logEvent(loggingType: String, status: String, msg: String, exception: Exception?)

    fun getUserId(): Int

}