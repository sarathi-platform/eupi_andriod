package com.patsurvey.nudge.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.patsurvey.nudge.database.dao.*

@Database(entities = [VillageEntity::class, UserEntity::class
    , LanguageEntity::class, StepListEntity::class,CasteEntity::class,
    TolaEntity::class,DidiEntity::class], version = 1, exportSchema = false)
abstract class NudgeDatabase: RoomDatabase()  {

    abstract fun villageListDao(): VillageListDao
    abstract fun userDao(): UserDao
    abstract fun languageListDao(): LanguageListDao
    abstract fun stepsListDao(): StepsListDao
    abstract fun tolaDao(): TolaDao
    abstract fun casteListDao(): CasteListDao
    abstract fun didiDao(): DidiDao

}