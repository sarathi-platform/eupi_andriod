package com.patsurvey.nudge.activities.backup.domain.repository

import android.database.sqlite.SQLiteException
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteStatement
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

    override suspend fun getRemoteQuery(): List<RemoteQueryAuditTrailEntity> {
        try {
            val config =
                remoteQueryAuditTrailEntityDao.getRemoteQueries(userId = coreSharedPrefs.getUserId())
            CoreLogger.d(tag = TAG, msg = "getRemoteQuery -> config: $config")
            return config
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

    override suspend fun executeQuery(remoteQueryAuditTrail: RemoteQueryAuditTrailEntity) {
        if (remoteQueryAuditTrail.status != OPEN) {
            logEvent(
                LOGGING_TYPE_DEBUG,
                remoteQueryAuditTrail.status,
                msg = "executeQuery: failed as query was already executed",
                exception = null
            )
            return
        }

        val isAppVersionValid = runAppVersionCheck(remoteQueryAuditTrail)

        if (!isAppVersionValid) {
            logEvent(
                LOGGING_TYPE_DEBUG,
                FAILED,
                "executeQuery failed due to invalid AppVersion required: ${BuildConfig.VERSION_CODE}, found: ${remoteQueryAuditTrail.appVersion} -> operation: ${remoteQueryAuditTrail.operationType}, database: ${remoteQueryAuditTrail.databaseName}, " +
                        "table: ${remoteQueryAuditTrail.tableName}, query: ${remoteQueryAuditTrail.query}",
                null
            )
            return
        }

        if (!isDatabaseVersionValidForExecution(remoteQueryAuditTrail)) {
            logEvent(
                LOGGING_TYPE_DEBUG,
                FAILED,
                "executeQuery failed due to invalid Database version, required: ${
                    DatabaseEnum.getDbVersion(remoteQueryAuditTrail.databaseName)
                }, found: ${remoteQueryAuditTrail.dbVersion} -> operation: ${remoteQueryAuditTrail.operationType}, database: ${remoteQueryAuditTrail.databaseName}," +
                        "table: ${remoteQueryAuditTrail.tableName}, query: ${remoteQueryAuditTrail.query}",
                null
            )
            return
        }

        if (isQueryAlreadyExecuted(remoteQueryAuditTrail)) {
            logEvent(
                LOGGING_TYPE_DEBUG,
                FAILED,
                "executeQuery failed due to query already executed -> operation: ${DatabaseOperationEnum.INSERT.name}, database: ${remoteQueryAuditTrail.databaseName}," +
                        "table: ${remoteQueryAuditTrail.tableName}, query: ${remoteQueryAuditTrail.query}",
                null
            )
            return
        }

        getDatabaseForQueryExecution(remoteQueryAuditTrail)?.let { database ->

            val userId = coreSharedPrefs.getUniqueUserIdentifier()
            val supportSQLiteStatement: SupportSQLiteStatement? =
                getSQLiteStatement(database, remoteQueryAuditTrail)

            when (remoteQueryAuditTrail.operationType) {
                DatabaseOperationEnum.INSERT.name -> {
                    try {
                        supportSQLiteStatement?.let {
                            executeInsertState(
                                remoteQueryAuditTrail,
                                it
                            )
                        }
                    } catch (ex: Exception) {
                        remoteQueryAuditTrailEntityDao
                            .updateRemoteQueryAuditTrailEntityStatus(
                                id = remoteQueryAuditTrail.id,
                                propertyValueId = remoteQueryAuditTrail.propertyValueId,
                                status = FAILED,
                                errorMessage = ex.message.value(),
                                userId = userId
                            )
                        logEvent(
                            LOGGING_TYPE_EXCEPTION,
                            FAILED,
                            "executeQuery failed -> operation: ${DatabaseOperationEnum.INSERT.name}, database: ${remoteQueryAuditTrail.databaseName}, " +
                                    "table: ${remoteQueryAuditTrail.tableName}, query: ${remoteQueryAuditTrail.query} \nexception: ${ex.message}",
                            ex
                        )
                    }
                }

                DatabaseOperationEnum.DELETE.name,
                DatabaseOperationEnum.UPDATE.name -> {
                    try {
                        supportSQLiteStatement?.let {
                            executeUpdateDeleteStatement(
                                remoteQueryAuditTrail,
                                it
                            )
                        }
                    } catch (ex: Exception) {
                        remoteQueryAuditTrailEntityDao.updateRemoteQueryAuditTrailEntityStatus(
                            propertyValueId = remoteQueryAuditTrail.propertyValueId,
                            id = remoteQueryAuditTrail.id,
                            status = FAILED,
                            errorMessage = ex.message.value(),
                            userId = userId
                        )
                        logEvent(
                            LOGGING_TYPE_EXCEPTION,
                            FAILED,
                            "executeQuery failed due to exception -> operation: ${remoteQueryAuditTrail.operationType}, database: ${remoteQueryAuditTrail.databaseName}, " +
                                    "table: ${remoteQueryAuditTrail.tableName}, query: ${remoteQueryAuditTrail.query} \nexception: ${ex.message}",
                            ex
                        )
                    }
                }

                else -> {
                    remoteQueryAuditTrailEntityDao.updateRemoteQueryAuditTrailEntityStatus(
                        propertyValueId = remoteQueryAuditTrail.propertyValueId,
                        id = remoteQueryAuditTrail.id,
                        status = OPEN,
                        errorMessage = "$INVALID_OPERATION_MESSAGE ${remoteQueryAuditTrail.operationType}",
                        userId = userId
                    )
                    return@let
                }
            }
        }
    }

    override suspend fun getSQLiteStatement(
        database: RoomDatabase,
        remoteQueryAuditTrail: RemoteQueryAuditTrailEntity,
    ): SupportSQLiteStatement? {
        return try {
            database.compileStatement(getCleanQuery(remoteQueryAuditTrail.query))
        } catch (ex: SQLiteException) {
            remoteQueryAuditTrailEntityDao
                .updateRemoteQueryAuditTrailEntityStatus(
                    id = remoteQueryAuditTrail.id,
                    propertyValueId = remoteQueryAuditTrail.propertyValueId,
                    status = FAILED,
                    errorMessage = ex.message.value(),
                    userId = remoteQueryAuditTrail.userId
                )
            logEvent(
                LOGGING_TYPE_EXCEPTION,
                FAILED,
                msg = "executeQuery failed due to SQLiteException -> operation: ${DatabaseOperationEnum.INSERT.name}, database: ${remoteQueryAuditTrail.databaseName}, " +
                        "table: ${remoteQueryAuditTrail.tableName}, query: ${remoteQueryAuditTrail.query} \nexception: ${ex.message}",
                ex
            )

            null
        } catch (ex: Exception) {
            remoteQueryAuditTrailEntityDao
                .updateRemoteQueryAuditTrailEntityStatus(
                    propertyValueId = remoteQueryAuditTrail.propertyValueId,
                    id = remoteQueryAuditTrail.id,
                    status = FAILED,
                    errorMessage = ex.message.value(),
                    userId = remoteQueryAuditTrail.userId
                )
            logEvent(
                LOGGING_TYPE_EXCEPTION,
                FAILED,
                msg = "executeQuery failed due to Exception -> operation: ${DatabaseOperationEnum.INSERT.name}, database: ${remoteQueryAuditTrail.databaseName}, " +
                        "table: ${remoteQueryAuditTrail.tableName}, query: ${remoteQueryAuditTrail.query} \nexception: ${ex.message}",
                ex
            )

            null
        }
    }

    override suspend fun executeInsertState(
        remoteQueryAuditTrail: RemoteQueryAuditTrailEntity,
        supportSQLiteStatement: SupportSQLiteStatement
    ): Long {
        try {
            val rowId = supportSQLiteStatement.executeInsert()
            remoteQueryAuditTrailEntityDao.updateRemoteQueryAuditTrailEntityStatus(
                id = remoteQueryAuditTrail.id,
                propertyValueId = remoteQueryAuditTrail.propertyValueId,
                status = SUCCESS,
                userId = remoteQueryAuditTrail.userId
            )
            //TODO add code to update query status in locally and remotely here
            logEvent(
                LOGGING_TYPE_DEBUG,
                SUCCESS,
                msg = "executeInsertState success -> operation: ${DatabaseOperationEnum.INSERT.name}, database: ${remoteQueryAuditTrail.databaseName}, " +
                        "table: ${remoteQueryAuditTrail.tableName}, query: ${remoteQueryAuditTrail.query}, rowId: $rowId",
                null
            )
            return rowId
        } catch (ex: Exception) {
            throw ex
        }
    }

    override suspend fun executeUpdateDeleteStatement(
        remoteQueryAuditTrail: RemoteQueryAuditTrailEntity,
        supportSQLiteStatement: SupportSQLiteStatement
    ): Int {
        try {
            val affectedRowCount = supportSQLiteStatement.executeUpdateDelete()
            remoteQueryAuditTrailEntityDao.updateRemoteQueryAuditTrailEntityStatus(
                id = remoteQueryAuditTrail.id,
                propertyValueId = remoteQueryAuditTrail.propertyValueId,
                status = SUCCESS,
                userId = remoteQueryAuditTrail.userId
            )
            //TODO add code to update query status in locally and remotely here
            logEvent(
                LOGGING_TYPE_DEBUG,
                SUCCESS,
                msg = "executeUpdateDeleteStatement success -> operation: ${remoteQueryAuditTrail.operationType}, database: ${remoteQueryAuditTrail.databaseName}, " +
                        "table: ${remoteQueryAuditTrail.tableName}, query: ${remoteQueryAuditTrail.query}, affectedRowCount: $affectedRowCount",
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

    override fun isUserIdCheckNotRequired(remoteQueryAuditTrail: RemoteQueryAuditTrailEntity): Boolean {
        return !TABLE_NOT_REQUIRE_USER_ID.contains(remoteQueryAuditTrail.tableName)
    }

    override fun getDatabaseForQueryExecution(remoteQueryAuditTrail: RemoteQueryAuditTrailEntity): RoomDatabase? {
        return when (remoteQueryAuditTrail.databaseName) {
            DatabaseEnum.NudgeDatabase.databaseName -> nudgeDatabase
            DatabaseEnum.NudgeBaselineDatabase.databaseName -> nudgeBaselineDatabase
            DatabaseEnum.NudgeGrantDatabase.databaseName -> nudgeGrantDatabase
            DatabaseEnum.SyncDatabase.databaseName -> syncDatabase
            DatabaseEnum.CoreDatabase.databaseName -> coreDatabase
            else -> null
        }
    }

    override fun isDatabaseVersionValidForExecution(remoteQueryDto: RemoteQueryAuditTrailEntity): Boolean {
        return when (remoteQueryDto.databaseName) {
            DatabaseEnum.NudgeDatabase.databaseName -> remoteQueryDto.dbVersion == DatabaseEnum.NudgeDatabase.dbVersion
            DatabaseEnum.NudgeBaselineDatabase.databaseName -> remoteQueryDto.dbVersion == DatabaseEnum.NudgeBaselineDatabase.dbVersion
            DatabaseEnum.NudgeGrantDatabase.databaseName -> remoteQueryDto.dbVersion == DatabaseEnum.NudgeGrantDatabase.dbVersion
            DatabaseEnum.SyncDatabase.databaseName -> remoteQueryDto.dbVersion == DatabaseEnum.SyncDatabase.dbVersion
            DatabaseEnum.CoreDatabase.databaseName -> remoteQueryDto.dbVersion == DatabaseEnum.CoreDatabase.dbVersion
            else -> false
        }
    }

    override fun runAppVersionCheck(remoteQueryAuditTrail: RemoteQueryAuditTrailEntity): Boolean {
        return remoteQueryAuditTrail.appVersion == BuildConfig.VERSION_CODE.toString()
    }

    override suspend fun isQueryAlreadyExecuted(remoteQueryAuditTrail: RemoteQueryAuditTrailEntity): Boolean {
        remoteQueryAuditTrailEntityDao.isQueryAlreadyExecuted(
            coreSharedPrefs.getUniqueUserIdentifier(),
            databaseName = remoteQueryAuditTrail.databaseName,
            dbVersion = remoteQueryAuditTrail.dbVersion,
            tableName = remoteQueryAuditTrail.tableName,
            operationType = remoteQueryAuditTrail.operationType,
            appVersion = remoteQueryAuditTrail.appVersion,
            query = remoteQueryAuditTrail.query
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