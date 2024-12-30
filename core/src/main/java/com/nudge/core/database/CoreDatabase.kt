package com.nudge.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nudge.core.CORE_DB_VERSION
import com.nudge.core.database.MigrationQueries.CREATE_CASTE_TABLE
import com.nudge.core.database.MigrationQueries.CREATE_LANGUAGE_TABLE
import com.nudge.core.database.MigrationQueries.CREATE_TRANSLATION_CONFIG_TABLE
import com.nudge.core.database.converters.DateConverter
import com.nudge.core.database.converters.ListConvertor
import com.nudge.core.database.dao.ApiConfigDao
import com.nudge.core.database.dao.CasteListDao
import com.nudge.core.database.dao.language.LanguageListDao
import com.nudge.core.database.dao.translation.TranslationConfigDao
import com.nudge.core.database.entities.AppConfigEntity
import com.nudge.core.database.entities.CasteEntity
import com.nudge.core.database.entities.language.LanguageEntity
import com.nudge.core.database.entities.traslation.TranslationConfigEntity
import com.nudge.core.utils.CoreLogger
import java.sql.SQLException

@Database(
    entities = [
        AppConfigEntity::class,
        TranslationConfigEntity::class,
        LanguageEntity::class,
        CasteEntity::class
    ],
    version = CORE_DB_VERSION,
    exportSchema = false
)
@TypeConverters(DateConverter::class, ListConvertor::class)
abstract class CoreDatabase : RoomDatabase() {

    abstract fun appConfigDao(): ApiConfigDao
    abstract fun casteListDao(): CasteListDao
    abstract fun translationConfigDao(): TranslationConfigDao
    abstract fun languageListDao(): LanguageListDao

    companion object {

        // CREATE MIGRATION OBJECT FOR MIGRATION 1 to 2.
        val CORE_DATABASE_MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                CoreLogger.d(tag = "CoreDatabase", msg = "MIGRATION_1_2")
                migration(db, listOf(CREATE_CASTE_TABLE))
            }
        }
        val CORE_DATABASE_MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                CoreLogger.d(tag = "**CoreDatabase**", msg = "MIGRATION_2_3")
                migration(db, listOf(CREATE_LANGUAGE_TABLE, CREATE_TRANSLATION_CONFIG_TABLE))
            }
        }

        private fun migration(database: SupportSQLiteDatabase, execSqls: List<String>) {
            for (sql in execSqls) {
                try {
                    database.execSQL(sql)
                } catch (e: SQLException) {
                    CoreLogger.e(
                        tag = "CoreDatabase****",
                        msg = "migration \"$sql\" Migration Error",
                        ex = e,
                        stackTrace = true
                    )
                } catch (t: Throwable) {
                    CoreLogger.e(
                        tag = "CoreDatabase**",
                        msg = "migration \"$sql\"",
                        ex = t,
                        stackTrace = true
                    )
                }
            }
        }

    }


}