package com.patsurvey.nudge.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.patsurvey.nudge.database.dao.VillageListDao

@Database(entities = [VillageEntity::class], version = 1, exportSchema = false)
abstract class NudgeDatabase: RoomDatabase()  {

    abstract fun villageListDao(): VillageListDao

}