package com.patsurvey.nudge.activities.backup.domain.repository

import android.text.TextUtils
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteStatement
import com.nrlm.baselinesurvey.database.NudgeBaselineDatabase
import com.nudge.core.CASTE_TABLE
import com.nudge.core.FAILED
import com.nudge.core.INPROGRESS
import com.nudge.core.INVALID_OPERATION_MESSAGE
import com.nudge.core.LANGUAGE_TABLE_NAME
import com.nudge.core.SUCCESS
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
    private val coreSharedPrefs: CoreSharedPrefs
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

        if (isUserIdCheckRequired && !cleanQuery.contains(USER_ID_FILTER))
            return false

        if (cleanQuery.contains(SUSPICIOUS_PATTERN_REGEX))
            return false

        return true
    }

    override suspend fun getRemoteQuery(): RemoteQueryDto? {
        val config = appConfigDao.getConfig(
            AppConfigKeysEnum.EXCLUDE_IN_INCOME_SUMMARY.name,
            coreSharedPrefs.getUniqueUserIdentifier()
        )?.value

        if (TextUtils.isEmpty(config))
            return null

        return config.fromJson<RemoteQueryDto>()
    }

    override suspend fun executeQuery(remoteQueryDto: RemoteQueryDto) {
        val isAppVersionValid = runAppVersionCheck(remoteQueryDto)

        if (!isAppVersionValid)
            return

        if (!isDatabaseVersionValidForExecution(remoteQueryDto))
            return

        getDatabaseForQueryExecution(remoteQueryDto)?.let { database ->

            val userId = coreSharedPrefs.getUniqueUserIdentifier()

            val supportSQLiteStatement =
                database.compileStatement(getCleanQuery(remoteQueryDto.query))

            val auditTrailRowId = remoteQueryAuditTrailEntityDao.insertRemoteQueryAuditTrailEntity(
                RemoteQueryAuditTrailEntity.getRemoteQueryAuditTrailEntity(remoteQueryDto, userId)
            )

            when (remoteQueryDto.operationType) {
                DatabaseOperationEnum.INSERT.name -> {
                    try {
                        executeInsertState(
                            auditTrailRowId.toInt(),
                            userId,
                            remoteQueryDto,
                            supportSQLiteStatement
                        )
                    } catch (ex: Exception) {
                        remoteQueryAuditTrailEntityDao
                            .updateRemoteQueryAuditTrailEntityStatus(
                                auditTrailRowId.toInt(),
                                status = FAILED,
                                errorMessage = ex.message.value(),
                                userId = userId
                            )
                        CoreLogger.e(
                            tag = TAG,
                            msg = "executeQuery failed -> operation: ${DatabaseOperationEnum.INSERT.name}, database: ${remoteQueryDto.databaseName}, " +
                                    "table: ${remoteQueryDto.tableName}, query: ${remoteQueryDto.query} \nexcetion: ${ex.message}",
                            ex = ex, stackTrace = true
                        )
                    }
                }

                DatabaseOperationEnum.DELETE.name,
                DatabaseOperationEnum.UPDATE.name -> {
                    try {
                        executeUpdateDeleteStatement(
                            auditTrailRowId.toInt(),
                            userId,
                            remoteQueryDto,
                            supportSQLiteStatement
                        )
                    } catch (ex: Exception) {
                        remoteQueryAuditTrailEntityDao.updateRemoteQueryAuditTrailEntityStatus(
                            auditTrailRowId.toInt(),
                            status = FAILED,
                            errorMessage = ex.message.value(),
                            userId = userId
                        )
                        CoreLogger.e(
                            tag = TAG,
                            msg = "executeQuery failed -> operation: ${remoteQueryDto.operationType}, database: ${remoteQueryDto.databaseName}, " +
                                    "table: ${remoteQueryDto.tableName}, query: ${remoteQueryDto.query} \nexcetion: ${ex.message}",
                            ex = ex, stackTrace = true
                        )
                    }
                }

                else -> {
                    remoteQueryAuditTrailEntityDao.updateRemoteQueryAuditTrailEntityStatus(
                        auditTrailRowId.toInt(),
                        status = INPROGRESS,
                        errorMessage = "$INVALID_OPERATION_MESSAGE ${remoteQueryDto.operationType}",
                        userId = userId
                    )
                    return@let
                }
            }
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
            CoreLogger.d(
                tag = TAG,
                msg = "executeInsertState success -> operation: ${DatabaseOperationEnum.INSERT.name}, database: ${remoteQueryDto.databaseName}, " +
                        "table: ${remoteQueryDto.tableName}, query: ${remoteQueryDto.query}, rowId: $rowId"
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
            CoreLogger.d(
                tag = TAG,
                msg = "executeUpdateDeleteStatement success -> operation: ${remoteQueryDto.operationType}, database: ${remoteQueryDto.databaseName}, " +
                        "table: ${remoteQueryDto.tableName}, query: ${remoteQueryDto.query}, affectedRowCount: $affectedRowCount"
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
            DatabaseEnum.NudgeDatabase.databaseName -> {
                remoteQueryDto.dbVersion == DatabaseEnum.NudgeDatabase.dbVersion
            }

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

}