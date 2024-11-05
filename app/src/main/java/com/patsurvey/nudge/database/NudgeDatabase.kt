package com.patsurvey.nudge.database

import android.util.Log
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nudge.core.NUDGE_DATABASE_VERSION
import com.patsurvey.nudge.database.converters.BeneficiaryStepConverter
import com.patsurvey.nudge.database.converters.IntConverter
import com.patsurvey.nudge.database.converters.QuestionsOptionsConverter
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.BpcScorePercentageDao
import com.patsurvey.nudge.database.dao.BpcSummaryDao
import com.patsurvey.nudge.database.dao.CasteListDao
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.LastSelectedTolaDao
import com.patsurvey.nudge.database.dao.NumericAnswerDao
import com.patsurvey.nudge.database.dao.PoorDidiListDao
import com.patsurvey.nudge.database.dao.QuestionListDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.dao.TolaDao
import com.patsurvey.nudge.database.dao.TrainingVideoDao
import com.patsurvey.nudge.database.dao.UserDao
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.utils.NudgeLogger
import java.sql.SQLException


@Database(
    entities = [VillageEntity::class, UserEntity::class, StepListEntity::class, CasteEntity::class,
        TolaEntity::class, DidiEntity::class, LastTolaSelectedEntity::class, QuestionEntity::class, SectionAnswerEntity::class, NumericAnswerEntity::class, TrainingVideoEntity::class,
        BpcSummaryEntity::class, BpcScorePercentageEntity::class, PoorDidiEntity::class],
    version = NUDGE_DATABASE_VERSION,
    exportSchema = false
)
@TypeConverters(
    IntConverter::class,
    BeneficiaryStepConverter::class,
    QuestionsOptionsConverter::class
)
abstract class NudgeDatabase : RoomDatabase() {

    abstract fun villageListDao(): VillageListDao
    abstract fun userDao(): UserDao
    abstract fun stepsListDao(): StepsListDao
    abstract fun tolaDao(): TolaDao
    abstract fun casteListDao(): CasteListDao
    abstract fun didiDao(): DidiDao
    abstract fun lastSelectedTola(): LastSelectedTolaDao
    abstract fun questionListDao(): QuestionListDao
    abstract fun answerDao(): AnswerDao
    abstract fun numericAnswerDao(): NumericAnswerDao
    abstract fun trainingVideoDao(): TrainingVideoDao
    abstract fun bpcSummaryDao(): BpcSummaryDao
    abstract fun bpcScorePercentageDao(): BpcScorePercentageDao
    abstract fun poorDidiListDao(): PoorDidiListDao

    companion object {


        // ADD THIS TYPE OF SQL QUERY FOR TABLE CREATION OR ALTERATION
        private const val ALTER_VILLAGE_TABLE =
            "ALTER TABLE 'village_table' ADD COLUMN 'isDataLoadTriedOnce' INTEGER DEFAULT 0 NOT NULL"
        private const val ALTER_VILLAGE_TABLE_WITH_ACTIVE_COLUMN =
            "ALTER TABLE 'village_table' ADD COLUMN 'isActive' INTEGER DEFAULT 1 NOT NULL"

        // CREATE MIGRATION OBJECT FOR MIGRATION 1 to 2.
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                NudgeLogger.d("NudgeDatabase",  "MIGRATION_1_2")
                migration(db, listOf(ALTER_VILLAGE_TABLE))
            }
        }
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                NudgeLogger.d("NudgeDatabase", "MIGRATION_2_3")
                migration(db, listOf(ALTER_VILLAGE_TABLE_WITH_ACTIVE_COLUMN))
            }
        }

        private fun migration(database: SupportSQLiteDatabase, execSqls: List<String>) {
            for(sql in execSqls) {
                try {
                    database.execSQL(sql)
                } catch (e: SQLException) {
                    Log.d("NudgeDatabase", "migration \"$sql\" Migration Error", e)
                } catch (t: Throwable) {
                    Log.d("NudgeDatabase", "migration \"$sql\"", t)
                }
            }
        }
    }

    class NudgeDatabaseCallback : Callback()

}