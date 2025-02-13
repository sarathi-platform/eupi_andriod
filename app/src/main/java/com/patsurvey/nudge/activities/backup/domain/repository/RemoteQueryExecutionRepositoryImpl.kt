package com.patsurvey.nudge.activities.backup.domain.repository

import android.text.TextUtils
import com.nrlm.baselinesurvey.database.NudgeBaselineDatabase
import com.nudge.core.database.CoreDatabase
import com.nudge.core.database.dao.ApiConfigDao
import com.nudge.core.enums.AppConfigKeysEnum
import com.nudge.core.fromJson
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.syncmanager.database.SyncManagerDatabase
import com.patsurvey.nudge.database.NudgeDatabase
import com.patsurvey.nudge.model.dataModel.RemoteQueryDto
import com.sarathi.dataloadingmangement.data.database.NudgeGrantDatabase
import javax.inject.Inject

class RemoteQueryExecutionRepositoryImpl @Inject constructor(
    private val nudgeDatabase: NudgeDatabase,
    private val nudgeBaselineDatabase: NudgeBaselineDatabase,
    private val nudgeGrantDatabase: NudgeGrantDatabase,
    private val syncDatabase: SyncManagerDatabase,
    private val coreDatabase: CoreDatabase,
    private val appConfigDao: ApiConfigDao,
    private val coreSharedPrefs: CoreSharedPrefs
) : RemoteQueryExecutionRepository {

    override fun checkIfQueryIsValid(query: String): Boolean {
        TODO("Not yet implemented")
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

}