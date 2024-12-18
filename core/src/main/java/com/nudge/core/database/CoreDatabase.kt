package com.nudge.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nudge.core.CORE_DB_VERSION
import com.nudge.core.database.converters.DateConverter
import com.nudge.core.database.converters.ListConvertor
import com.nudge.core.database.dao.ApiConfigDao
import com.nudge.core.database.dao.CasteListDao
import com.nudge.core.database.entities.AppConfigEntity
import com.nudge.core.database.entities.CasteEntity

@Database(
    entities = [
        AppConfigEntity::class,
        CasteEntity::class

    ],
    version = CORE_DB_VERSION,
    exportSchema = false
)
@TypeConverters(DateConverter::class, ListConvertor::class)
abstract class CoreDatabase : RoomDatabase() {

    abstract fun appConfigDao(): ApiConfigDao
    abstract fun casteListDao(): CasteListDao


}