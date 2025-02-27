package com.patsurvey.nudge.activities.backup.domain.repository

import android.database.sqlite.SQLiteException
import android.text.TextUtils
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteStatement
import com.google.gson.JsonSyntaxException
import com.nrlm.baselinesurvey.database.NudgeBaselineDatabase
import com.nudge.core.CASTE_TABLE
import com.nudge.core.FAILED
import com.nudge.core.INVALID_OPERATION_MESSAGE
import com.nudge.core.LANGUAGE_TABLE_NAME
import com.nudge.core.LOGGING_TYPE_DEBUG
import com.nudge.core.LOGGING_TYPE_EXCEPTION
import com.nudge.core.OPEN
import com.nudge.core.SUCCESS
import com.nudge.core.analytics.AnalyticsManager
import com.nudge.core.analytics.mixpanel.AnalyticsEvents
import com.nudge.core.analytics.mixpanel.AnalyticsEventsParam
import com.nudge.core.database.CoreDatabase
import com.nudge.core.database.dao.ApiConfigDao
import com.nudge.core.database.dao.RemoteQueryAuditTrailEntityDao
import com.nudge.core.database.entities.RemoteQueryAuditTrailEntity
import com.nudge.core.enums.AppConfigKeysEnum
import com.nudge.core.fromJson
import com.nudge.core.model.RemoteQueryDto
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.utils.CoreLogger
import com.nudge.core.value
import com.nudge.syncmanager.database.SyncManagerDatabase
import com.patsurvey.nudge.BuildConfig
import com.patsurvey.nudge.database.NudgeDatabase
import com.patsurvey.nudge.utils.DatabaseEnum
import com.patsurvey.nudge.utils.DatabaseOperationEnum
import com.sarathi.dataloadingmangement.data.database.NudgeGrantDatabase
import javax.inject.Inject

class RemoteQueryExecutionRepositoryImpl @Inject constructor(
    private val nudgeDatabase: NudgeDatabase,
    private val nudgeBaselineDatabase: NudgeBaselineDatabase,
    private val nudgeGrantDatabase: NudgeGrantDatabase,
    private val syncDatabase: SyncManagerDatabase,
    private val coreDatabase: CoreDatabase,
    private val appConfigDao: ApiConfigDao,
    private val remoteQueryAuditTrailEntityDao: RemoteQueryAuditTrailEntityDao,
    private val coreSharedPrefs: CoreSharedPrefs,
    private val analyticsManager: AnalyticsManager
) : RemoteQueryExecutionRepository {

    companion object {
        private val TAG = RemoteQueryExecutionRepositoryImpl::class.java.simpleName
        private const val USER_ID_FILTER = "userId"
        private val TABLE_NOT_REQUIRE_USER_ID = listOf(CASTE_TABLE, LANGUAGE_TABLE_NAME)
        private val SUSPICIOUS_PATTERN_REGEX = Regex(
            "(\\b(UNION|DROP|ALTER|TRUNCATE|REPLACE|GRANT|REVOKE|EXEC|DECLARE|CAST)\\b\\s*)|(--|#|\\/\\*|\\*\\/|;)|(\\bOR\\b\\s+\\d+=\\d+\\b)|(\\bAND\\b\\s+\\d+=\\d+\\b)",
            RegexOption.IGNORE_CASE
        )
    }

    override fun checkIfQueryIsValid(query: String, isUserIdCheckRequired: Boolean): Boolean {
        val cleanQuery = getCleanQuery(query).lowercase()

        if (isUserIdCheckRequired && !cleanQuery.contains(USER_ID_FILTER.lowercase())) {
            logEvent(
                LOGGING_TYPE_DEBUG,
                FAILED,
                "checkIfQueryIsValid failed due to userId Filter -> query: ${query}",
                null
            )
            return false
        }

//        TODO Check this regex and handle appropriately before merge.
//        if (cleanQuery.contains(SUSPICIOUS_PATTERN_REGEX))
//            return false

        return true
    }

    override suspend fun getRemoteQuery(): List<RemoteQueryDto?> {
        try {
            val config = appConfigDao.getConfig(
                AppConfigKeysEnum.SQL_QUERY_EXECUTOR.name,
                coreSharedPrefs.getUniqueUserIdentifier()
            )?.value

            CoreLogger.d(tag = TAG, msg = "getRemoteQuery -> config: $config")
//            Test Query for execution
//            TODO Delete before merge after testing with BE integration.
//            val config =
//                "{\"databaseName\": \"NudgeGrantDatabase\",\"dbVersion\": 7,\"tableName\": \"mission_configs_table\",\"query\": \"UPDATE mission_configs_table SET missionName = 'Livelihood Planning New' where userId = 'Ultra Poor change maker (UPCM)_9862345078' and missionId = 6;\",\n" +
//                        "  \"operationType\": \"UPDATE\",\"appVersion\": \"134\"}"

            if (TextUtils.isEmpty(config))
                return emptyList()

            return config.fromJson<List<RemoteQueryDto>>() ?: emptyList()
        } catch (ex: JsonSyntaxException) {
            logEvent(
                LOGGING_TYPE_EXCEPTION,
                FAILED,
                "getRemoteQuery -> JsonSyntaxException ex: ${ex.message}",
                ex
            )
            return emptyList()
        } catch (ex: Exception) {
            logEvent(
                LOGGING_TYPE_EXCEPTION,
                FAILED,
                "getRemoteQuery -> Exception ex: ${ex.message}",
                ex
            )
            return emptyList()
        }

    }

    override suspend fun executeQuery(remoteQueryDto: RemoteQueryDto) {
        if (remoteQueryDto.status != OPEN) {
            logEvent(
                LOGGING_TYPE_DEBUG,
                remoteQueryDto.status,
                msg = "executeQuery: failed as query was already executed",
                exception = null
            )
            return
        }

        val isAppVersionValid = runAppVersionCheck(remoteQueryDto)

        if (!isAppVersionValid) {
            logEvent(
                LOGGING_TYPE_DEBUG,
                FAILED,
                "executeQuery failed due to invalid AppVersion required: ${BuildConfig.VERSION_CODE}, found: ${remoteQueryDto.appVersion} -> operation: ${remoteQueryDto.operationType}, database: ${remoteQueryDto.databaseName}, " +
                        "table: ${remoteQueryDto.tableName}, query: ${remoteQueryDto.query}",
                null
            )
            return
        }

        if (!isDatabaseVersionValidForExecution(remoteQueryDto)) {
            logEvent(
                LOGGING_TYPE_DEBUG,
                FAILED,
                "executeQuery failed due to invalid Database version, required: ${
                    DatabaseEnum.getDbVersion(remoteQueryDto.databaseName)
                }, found: ${remoteQueryDto.dbVersion} -> operation: ${remoteQueryDto.operationType}, database: ${remoteQueryDto.databaseName}," +
                        "table: ${remoteQueryDto.tableName}, query: ${remoteQueryDto.query}",
                null
            )
            return
        }

        if (isQueryAlreadyExecuted(remoteQueryDto)) {
            logEvent(
                LOGGING_TYPE_DEBUG,
                FAILED,
                "executeQuery failed due to query already executed -> operation: ${DatabaseOperationEnum.INSERT.name}, database: ${remoteQueryDto.databaseName}," +
                        "table: ${remoteQueryDto.tableName}, query: ${remoteQueryDto.query}",
                null
            )
            return
        }

        getDatabaseForQueryExecution(remoteQueryDto)?.let { database ->

            val userId = coreSharedPrefs.getUniqueUserIdentifier()

            val auditTrailRowId = remoteQueryAuditTrailEntityDao.insertRemoteQueryAuditTrailEntity(
                RemoteQueryAuditTrailEntity.getRemoteQueryAuditTrailEntity(remoteQueryDto, userId)
            )

            val supportSQLiteStatement: SupportSQLiteStatement? =
                getSQLiteStatement(database, remoteQueryDto, auditTrailRowId, userId)

            when (remoteQueryDto.operationType) {
                DatabaseOperationEnum.INSERT.name -> {
                    try {
                        supportSQLiteStatement?.let {
                            executeInsertState(
                                auditTrailRowId.toInt(),
                                userId,
                                remoteQueryDto,
                                it
                            )
                        }
                    } catch (ex: Exception) {
                        remoteQueryAuditTrailEntityDao
                            .updateRemoteQueryAuditTrailEntityStatus(
                                auditTrailRowId.toInt(),
                                status = FAILED,
                                errorMessage = ex.message.value(),
                                userId = userId
                            )
                        logEvent(
                            LOGGING_TYPE_EXCEPTION,
                            FAILED,
                            "executeQuery failed -> operation: ${DatabaseOperationEnum.INSERT.name}, database: ${remoteQueryDto.databaseName}, " +
                                    "table: ${remoteQueryDto.tableName}, query: ${remoteQueryDto.query} \nexception: ${ex.message}",
                            ex
                        )
                    }
                }

                DatabaseOperationEnum.DELETE.name,
                DatabaseOperationEnum.UPDATE.name -> {
                    try {
                        supportSQLiteStatement?.let {
                            executeUpdateDeleteStatement(
                                auditTrailRowId.toInt(),
                                userId,
                                remoteQueryDto,
                                it
                            )
                        }
                    } catch (ex: Exception) {
                        remoteQueryAuditTrailEntityDao.updateRemoteQueryAuditTrailEntityStatus(
                            auditTrailRowId.toInt(),
                            status = FAILED,
                            errorMessage = ex.message.value(),
                            userId = userId
                        )
                        logEvent(
                            LOGGING_TYPE_EXCEPTION,
                            FAILED,
                            "executeQuery failed due to exception -> operation: ${remoteQueryDto.operationType}, database: ${remoteQueryDto.databaseName}, " +
                                    "table: ${remoteQueryDto.tableName}, query: ${remoteQueryDto.query} \nexception: ${ex.message}",
                            ex
                        )
                    }
                }

                else -> {
                    remoteQueryAuditTrailEntityDao.updateRemoteQueryAuditTrailEntityStatus(
                        auditTrailRowId.toInt(),
                        status = OPEN,
                        errorMessage = "$INVALID_OPERATION_MESSAGE ${remoteQueryDto.operationType}",
                        userId = userId
                    )
                    return@let
                }
            }
        }
    }

    override suspend fun getSQLiteStatement(
        database: RoomDatabase,
        remoteQueryDto: RemoteQueryDto,
        auditTrailRowId: Long,
        userId: String
    ): SupportSQLiteStatement? {
        return try {
            database.compileStatement(getCleanQuery(remoteQueryDto.query))
        } catch (ex: SQLiteException) {
            remoteQueryAuditTrailEntityDao
                .updateRemoteQueryAuditTrailEntityStatus(
                    auditTrailRowId.toInt(),
                    status = FAILED,
                    errorMessage = ex.message.value(),
                    userId = userId
                )
            logEvent(
                LOGGING_TYPE_EXCEPTION,
                FAILED,
                msg = "executeQuery failed due to SQLiteException -> operation: ${DatabaseOperationEnum.INSERT.name}, database: ${remoteQueryDto.databaseName}, " +
                        "table: ${remoteQueryDto.tableName}, query: ${remoteQueryDto.query} \nexception: ${ex.message}",
                ex
            )

            null
        } catch (ex: Exception) {
            remoteQueryAuditTrailEntityDao
                .updateRemoteQueryAuditTrailEntityStatus(
                    auditTrailRowId.toInt(),
                    status = FAILED,
                    errorMessage = ex.message.value(),
                    userId = userId
                )
            logEvent(
                LOGGING_TYPE_EXCEPTION,
                FAILED,
                msg = "executeQuery failed due to Exception -> operation: ${DatabaseOperationEnum.INSERT.name}, database: ${remoteQueryDto.databaseName}, " +
                        "table: ${remoteQueryDto.tableName}, query: ${remoteQueryDto.query} \nexception: ${ex.message}",
                ex
            )

            null
        }
    }

    override suspend fun executeInsertState(
        auditTrailRowId: Int,
        userId: String,
        remoteQueryDto: RemoteQueryDto,
        supportSQLiteStatement: SupportSQLiteStatement
    ): Long {
        try {
            val rowId = supportSQLiteStatement.executeInsert()
            remoteQueryAuditTrailEntityDao.updateRemoteQueryAuditTrailEntityStatus(
                auditTrailRowId,
                status = SUCCESS,
                userId = userId
            )
            //TODO add code to update query status in locally and remotely here
            logEvent(
                LOGGING_TYPE_DEBUG,
                SUCCESS,
                msg = "executeInsertState success -> operation: ${DatabaseOperationEnum.INSERT.name}, database: ${remoteQueryDto.databaseName}, " +
                        "table: ${remoteQueryDto.tableName}, query: ${remoteQueryDto.query}, rowId: $rowId",
                null
            )
            return rowId
        } catch (ex: Exception) {
            throw ex
        }
    }

    override suspend fun executeUpdateDeleteStatement(
        auditTrailRowId: Int,
        userId: String,
        remoteQueryDto: RemoteQueryDto,
        supportSQLiteStatement: SupportSQLiteStatement
    ): Int {
        try {
            val affectedRowCount = supportSQLiteStatement.executeUpdateDelete()
            remoteQueryAuditTrailEntityDao.updateRemoteQueryAuditTrailEntityStatus(
                auditTrailRowId,
                status = SUCCESS,
                userId = userId
            )
            //TODO add code to update query status in locally and remotely here
            logEvent(
                LOGGING_TYPE_DEBUG,
                SUCCESS,
                msg = "executeUpdateDeleteStatement success -> operation: ${remoteQueryDto.operationType}, database: ${remoteQueryDto.databaseName}, " +
                        "table: ${remoteQueryDto.tableName}, query: ${remoteQueryDto.query}, affectedRowCount: $affectedRowCount",
                null
            )
            return affectedRowCount
        } catch (ex: Exception) {
            throw ex
        }
    }

    override fun getCleanQuery(query: String): String {
        return query.trim()
    }

    override fun isUserIdCheckNotRequired(remoteQueryDto: RemoteQueryDto): Boolean {
        return !TABLE_NOT_REQUIRE_USER_ID.contains(remoteQueryDto.tableName)
    }

    override fun getDatabaseForQueryExecution(remoteQueryDto: RemoteQueryDto): RoomDatabase? {
        return when (remoteQueryDto.databaseName) {
            DatabaseEnum.NudgeDatabase.databaseName -> nudgeDatabase
            DatabaseEnum.NudgeBaselineDatabase.databaseName -> nudgeBaselineDatabase
            DatabaseEnum.NudgeGrantDatabase.databaseName -> nudgeGrantDatabase
            DatabaseEnum.SyncDatabase.databaseName -> syncDatabase
            DatabaseEnum.CoreDatabase.databaseName -> coreDatabase
            else -> null
        }
    }

    override fun isDatabaseVersionValidForExecution(remoteQueryDto: RemoteQueryDto): Boolean {
        return when (remoteQueryDto.databaseName) {
            DatabaseEnum.NudgeDatabase.databaseName -> remoteQueryDto.dbVersion == DatabaseEnum.NudgeDatabase.dbVersion
            DatabaseEnum.NudgeBaselineDatabase.databaseName -> remoteQueryDto.dbVersion == DatabaseEnum.NudgeBaselineDatabase.dbVersion
            DatabaseEnum.NudgeGrantDatabase.databaseName -> remoteQueryDto.dbVersion == DatabaseEnum.NudgeGrantDatabase.dbVersion
            DatabaseEnum.SyncDatabase.databaseName -> remoteQueryDto.dbVersion == DatabaseEnum.SyncDatabase.dbVersion
            DatabaseEnum.CoreDatabase.databaseName -> remoteQueryDto.dbVersion == DatabaseEnum.CoreDatabase.dbVersion
            else -> false
        }
    }

    override fun runAppVersionCheck(remoteQueryDto: RemoteQueryDto): Boolean {
        return remoteQueryDto.appVersion == BuildConfig.VERSION_CODE.toString()
    }

    override suspend fun isQueryAlreadyExecuted(remoteQueryDto: RemoteQueryDto): Boolean {
        remoteQueryAuditTrailEntityDao.isQueryAlreadyExecuted(
            coreSharedPrefs.getUniqueUserIdentifier(), databaseName = remoteQueryDto.databaseName,
            dbVersion = remoteQueryDto.dbVersion,
            tableName = remoteQueryDto.tableName,
            operationType = remoteQueryDto.operationType,
            appVersion = remoteQueryDto.appVersion,
            query = remoteQueryDto.query
        )?.let {
            return it.status == SUCCESS
        } ?: return false
    }

    override fun logEvent(
        loggingType: String,
        status: String,
        msg: String,
        exception: Exception?
    ) {
        analyticsManager.trackEvent(
            AnalyticsEvents.SQL_INJECTION.eventName, properties = mapOf(
                AnalyticsEventsParam.SQL_INJECTION_STATUS.eventParam to status,
                AnalyticsEventsParam.SQL_INJECTION_MESSAGE.eventParam to msg
            )
        )
        when (loggingType) {
            LOGGING_TYPE_DEBUG -> {
                CoreLogger.d(
                    tag = TAG,
                    msg = msg
                )
            }

            LOGGING_TYPE_EXCEPTION -> {
                CoreLogger.e(
                    tag = TAG,
                    msg = msg,
                    ex = exception, stackTrace = true
                )
            }
        }
    }

}