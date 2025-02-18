package com.patsurvey.nudge.activities.backup.domain.repository

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteStatement
import com.nudge.core.model.RemoteQueryDto

interface RemoteQueryExecutionRepository {

    fun checkIfQueryIsValid(query: String, isUserIdCheckRequired: Boolean = true): Boolean

    suspend fun getRemoteQuery(): RemoteQueryDto?

    suspend fun executeQuery(remoteQueryDto: RemoteQueryDto)

    suspend fun executeInsertState(
        auditTrailRowId: Int,
        userId: String,
        remoteQueryDto: RemoteQueryDto,
        supportSQLiteStatement: SupportSQLiteStatement
    ): Long

    suspend fun executeUpdateDeleteStatement(
        auditTrailRowId: Int,
        userId: String,
        remoteQueryDto: RemoteQueryDto,
        supportSQLiteStatement: SupportSQLiteStatement
    ): Int

    fun getCleanQuery(query: String): String

    fun isUserIdCheckNotRequired(remoteQueryDto: RemoteQueryDto): Boolean

    fun getDatabaseForQueryExecution(remoteQueryDto: RemoteQueryDto): RoomDatabase?

    fun isDatabaseVersionValidForExecution(remoteQueryDto: RemoteQueryDto): Boolean

    fun runAppVersionCheck(remoteQueryDto: RemoteQueryDto): Boolean

    suspend fun isQueryAlreadyExecuted(remoteQueryDto: RemoteQueryDto): Boolean

    suspend fun isTableExists(tableName: String, database: RoomDatabase): Boolean

}