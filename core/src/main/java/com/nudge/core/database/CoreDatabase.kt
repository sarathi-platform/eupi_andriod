package com.nudge.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nudge.core.CORE_DB_VERSION
import com.nudge.core.database.converters.DateConverter
import com.nudge.core.database.converters.ListConvertor
import com.nudge.core.database.dao.ApiConfigDao
import com.nudge.core.database.dao.language.LanguageListDao
import com.nudge.core.database.dao.translation.TranslationConfigDao
import com.nudge.core.database.entities.AppConfigEntity
import com.nudge.core.database.entities.language.LanguageEntity
import com.nudge.core.database.entities.traslation.TranslationConfigEntity

@Database(
    entities = [
        AppConfigEntity::class,
        TranslationConfigEntity::class,
        LanguageEntity::class
    ],
    version = CORE_DB_VERSION,
    exportSchema = false
)
@TypeConverters(DateConverter::class, ListConvertor::class)
abstract class CoreDatabase : RoomDatabase() {

    abstract fun appConfigDao(): ApiConfigDao
    abstract fun translationConfigDao(): TranslationConfigDao
    abstract fun languageListDao(): LanguageListDao


}