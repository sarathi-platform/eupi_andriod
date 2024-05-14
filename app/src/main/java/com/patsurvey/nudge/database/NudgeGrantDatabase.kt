package com.patsurvey.nudge.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sarathi.missionactivitytask.data.entities.MissionEntity

// Increase DB Version everytime any change is made to any table or a new table is added.
const val NUDGE_GRANT_DATABASE_VERSION = 1

@Database(
    entities = [
        MissionEntity::class
    ],
    version = NUDGE_GRANT_DATABASE_VERSION,
    exportSchema = false
)
@TypeConverters()
abstract class NudgeGrantDatabase : RoomDatabase() {

    abstract fun missionDao(): MissionEntity
    class NudgeDatabaseCallback : Callback()

}