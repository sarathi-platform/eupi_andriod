package com.patsurvey.nudge.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sarathi.missionactivitytask.data.dao.ActivityTaskDao
import com.sarathi.missionactivitytask.data.dao.ContentDao
import com.sarathi.missionactivitytask.data.dao.MissionActivityDao
import com.sarathi.missionactivitytask.data.dao.MissionDao
import com.sarathi.missionactivitytask.data.entities.ActivityTaskEntity
import com.sarathi.missionactivitytask.data.entities.Content
import com.sarathi.missionactivitytask.data.entities.MissionActivityEntity
import com.sarathi.missionactivitytask.data.entities.MissionEntity

// Increase DB Version everytime any change is made to any table or a new table is added.
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