package com.patsurvey.nudge.utils

import com.nrlm.baselinesurvey.NUDGE_BASELINE_DATABASE
import com.nrlm.baselinesurvey.database.NUDGE_BASELINE_DATABASE_VERSION
import com.nudge.core.CORE_DATABASE
import com.nudge.core.CORE_DB_VERSION
import com.nudge.core.NUDGE_DATABASE
import com.nudge.core.NUDGE_DATABASE_VERSION
import com.nudge.core.NUDGE_GRANT_DATABASE
import com.nudge.core.SYNC_MANAGER_DATABASE
import com.nudge.core.SYNC_MANAGER_DB_VERSION
import com.sarathi.dataloadingmangement.data.database.NUDGE_GRANT_DATABASE_VERSION

enum class DatabaseEnum(val databaseName: String, val dbVersion: Int) {

    NudgeDatabase(NUDGE_DATABASE, NUDGE_DATABASE_VERSION),
    NudgeBaselineDatabase(NUDGE_BASELINE_DATABASE, NUDGE_BASELINE_DATABASE_VERSION),
    NudgeGrantDatabase(NUDGE_GRANT_DATABASE, NUDGE_GRANT_DATABASE_VERSION),
    SyncDatabase(SYNC_MANAGER_DATABASE, SYNC_MANAGER_DB_VERSION),
    CoreDatabase(CORE_DATABASE, CORE_DB_VERSION);

    companion object {
        fun getDbVersion(databaseName: String): Int {

            return when (databaseName) {
                NudgeDatabase.databaseName -> NudgeDatabase.dbVersion
                NudgeBaselineDatabase.databaseName -> NudgeBaselineDatabase.dbVersion
                NudgeGrantDatabase.databaseName -> NudgeGrantDatabase.dbVersion
                SyncDatabase.databaseName -> SyncDatabase.dbVersion
                CoreDatabase.databaseName -> CoreDatabase.dbVersion
                else -> -1
            }
        }
    }

}