package com.sarathi.dataloadingmangement.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sarathi.dataloadingmangement.data.dao.ActivityDao
import com.sarathi.dataloadingmangement.data.dao.ContentDao
import com.sarathi.dataloadingmangement.data.dao.MissionDao
import com.sarathi.dataloadingmangement.data.dao.TaskDao
import com.sarathi.dataloadingmangement.data.entities.Activity
import com.sarathi.dataloadingmangement.data.entities.Content
import com.sarathi.dataloadingmangement.data.entities.Mission
import com.sarathi.dataloadingmangement.data.entities.Task

const val NUDGE_GRANT_DATABASE_VERSION = 1

@Database(
    entities = [
        Mission::class,
        Activity::class,
        Task::class,
        Content::class
    ],
    version = NUDGE_GRANT_DATABASE_VERSION,
    exportSchema = false
)
abstract class NudgeGrantDatabase : RoomDatabase() {

    abstract fun missionDao(): MissionDao
    abstract fun activityDao(): ActivityDao
    abstract fun taskDao(): TaskDao
    abstract fun contentDao(): ContentDao
    class NudgeDatabaseCallback : Callback()

}