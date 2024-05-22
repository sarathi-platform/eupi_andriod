package com.sarathi.dataloadingmangement.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sarathi.dataloadingmangement.data.dao.ActivityTaskDao
import com.sarathi.dataloadingmangement.data.dao.MissionActivityDao
import com.sarathi.dataloadingmangement.data.dao.MissionDao
import com.sarathi.dataloadingmangement.data.entities.ActivityTaskEntity
import com.sarathi.dataloadingmangement.data.entities.MissionActivityEntity
import com.sarathi.dataloadingmangement.data.entities.MissionEntity

const val NUDGE_GRANT_DATABASE_VERSION = 1

@Database(
    entities = [
        MissionEntity::class,
        MissionActivityEntity::class,
        ActivityTaskEntity::class,
        Content::class
    ],
    version = NUDGE_GRANT_DATABASE_VERSION,
    exportSchema = false
)
abstract class NudgeGrantDatabase : RoomDatabase() {

    abstract fun missionDao(): MissionDao
    abstract fun activityDao(): MissionActivityDao
    abstract fun taskDao(): ActivityTaskDao
    abstract fun contentDao(): ContentDao
    class NudgeDatabaseCallback : Callback()

}