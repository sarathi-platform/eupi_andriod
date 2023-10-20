package com.nrlm.baselinesurvey.database

import android.util.Log
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nrlm.baselinesurvey.database.converters.IntConverter
import com.nrlm.baselinesurvey.database.converters.QuestionsOptionsConverter
import com.nrlm.baselinesurvey.database.dao.LanguageListDao
import com.nrlm.baselinesurvey.database.dao.VillageListDao
import com.nrlm.baselinesurvey.database.entity.LanguageEntity
import com.nrlm.baselinesurvey.database.entity.VillageEntity
import java.sql.SQLException

// Increase DB Version everytime any change is made to any table or a new table is added.
const val NUDGE_BASELINE_DATABASE_VERSION = 1

@Database(entities = [VillageEntity::class, LanguageEntity::class], version = NUDGE_BASELINE_DATABASE_VERSION, exportSchema = false)
@TypeConverters(IntConverter::class)
abstract class NudgeBaselineDatabase: RoomDatabase()  {

    abstract fun villageListDao(): VillageListDao
    abstract fun languageListDao(): LanguageListDao


    companion object {

        // ADD THIS TYPE OF SQL QUERY FOR TABLE CREATION OR ALTERATION
        /*private const val CREATE_BPC_SELECTED_DID_TABLE = "CREATE TABLE `$BPC_SELECTED_DIDI_TABLE` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `serverId` INTEGER NOT NULL, `name` TEXT NOT NULL, `needsToPost` INTEGER NOT NULL, `address` TEXT NOT NULL," +
                " `guardianName` TEXT NOT NULL, `relationship` TEXT NOT NULL, `castId` INTEGER NOT NULL, `castName` TEXT NOT NULL, `cohortId` INTEGER NOT NULL, `cohortName` TEXT NOT NULL, `villageId` INTEGER NOT NULL, `wealth_ranking` TEXT NOT NULL, `needsToPost` INTEGER NOT NULL DEFAULT 1)"*/

        // CREATE MIGRATION OBJECT FOR MIGRATION 1 to 2.
        /*val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                Log.d("NudgeDatabase",  "MIGRATION_42_43")
                migration(database, listOf(CREATE_BPC_SELECTED_DID_TABLE))
            }
        }*/

        private fun migration(database: SupportSQLiteDatabase, execSqls: List<String>) {
            for(sql in execSqls) {
                try {
                    database.execSQL(sql)
                } catch (e: SQLException) {
                    Log.d("NudgeBaselineDatabase", "migration \"$sql\" Migration Error", e)
                } catch (t: Throwable) {
                    Log.d("NudgeBaselineDatabase", "migration \"$sql\"", t)
                }
            }
        }
    }

    class NudgeDatabaseCallback : Callback()

}