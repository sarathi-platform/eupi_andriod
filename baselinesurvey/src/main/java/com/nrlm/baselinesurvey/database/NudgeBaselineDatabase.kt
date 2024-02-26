package com.nrlm.baselinesurvey.database

import android.util.Log
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nrlm.baselinesurvey.database.converters.BeneficiaryStepConverter
import com.nrlm.baselinesurvey.database.converters.ConditionsDtoConvertor
import com.nrlm.baselinesurvey.database.converters.IntConverter
import com.nrlm.baselinesurvey.database.converters.OptionQuestionConverter
import com.nrlm.baselinesurvey.database.converters.QuestionsOptionsConverter
import com.nrlm.baselinesurvey.database.converters.StringConverter
import com.nrlm.baselinesurvey.database.dao.ActivityTaskDao
import com.nrlm.baselinesurvey.database.dao.DidiInfoDao
import com.nrlm.baselinesurvey.database.dao.DidiSectionProgressEntityDao
import com.nrlm.baselinesurvey.database.dao.FormQuestionResponseDao
import com.nrlm.baselinesurvey.database.dao.InputTypeQuestionAnswerDao
import com.nrlm.baselinesurvey.database.dao.LanguageListDao
import com.nrlm.baselinesurvey.database.dao.MissionActivityDao
import com.nrlm.baselinesurvey.database.dao.MissionEntityDao
import com.nrlm.baselinesurvey.database.dao.OptionItemDao
import com.nrlm.baselinesurvey.database.dao.QuestionEntityDao
import com.nrlm.baselinesurvey.database.dao.SectionAnswerEntityDao
import com.nrlm.baselinesurvey.database.dao.SectionEntityDao
import com.nrlm.baselinesurvey.database.dao.SurveyEntityDao
import com.nrlm.baselinesurvey.database.dao.SurveyeeEntityDao
import com.nrlm.baselinesurvey.database.dao.VillageListDao
import com.nrlm.baselinesurvey.database.entity.ActivityTaskEntity
import com.nrlm.baselinesurvey.database.entity.DidiIntoEntity
import com.nrlm.baselinesurvey.database.entity.DidiSectionProgressEntity
import com.nrlm.baselinesurvey.database.entity.FormQuestionResponseEntity
import com.nrlm.baselinesurvey.database.entity.InputTypeQuestionAnswerEntity
import com.nrlm.baselinesurvey.database.entity.LanguageEntity
import com.nrlm.baselinesurvey.database.entity.MissionActivityEntity
import com.nrlm.baselinesurvey.database.entity.MissionEntity
import com.nrlm.baselinesurvey.database.entity.OptionItemEntity
import com.nrlm.baselinesurvey.database.entity.QuestionEntity
import com.nrlm.baselinesurvey.database.entity.SectionAnswerEntity
import com.nrlm.baselinesurvey.database.entity.SectionEntity
import com.nrlm.baselinesurvey.database.entity.SurveyEntity
import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import com.nrlm.baselinesurvey.database.entity.VillageEntity
import java.sql.SQLException

// Increase DB Version everytime any change is made to any table or a new table is added.
const val NUDGE_BASELINE_DATABASE_VERSION = 1

@Database(
    entities = [
        VillageEntity::class,
        LanguageEntity::class,
        SurveyeeEntity::class,
        SurveyEntity::class,
        SectionEntity::class,
        QuestionEntity::class,
        OptionItemEntity::class,
        MissionEntity::class,
        MissionActivityEntity::class,
        ActivityTaskEntity::class,
        DidiIntoEntity::class,
        DidiSectionProgressEntity::class,
        SectionAnswerEntity::class,
        FormQuestionResponseEntity::class,
        InputTypeQuestionAnswerEntity::class
    ],
    version = NUDGE_BASELINE_DATABASE_VERSION,
    exportSchema = false
)
@TypeConverters(
    IntConverter::class, BeneficiaryStepConverter::class, QuestionsOptionsConverter::class,
    OptionQuestionConverter::class, StringConverter::class, ConditionsDtoConvertor::class
)
abstract class NudgeBaselineDatabase: RoomDatabase()  {

    abstract fun villageListDao(): VillageListDao

    abstract fun languageListDao(): LanguageListDao

    abstract fun didiDao(): SurveyeeEntityDao

    abstract fun surveyEntityDao(): SurveyEntityDao

    abstract fun sectionEntityDao(): SectionEntityDao

    abstract fun questionEntityDao(): QuestionEntityDao
    abstract fun optionItemDao(): OptionItemDao

    abstract fun missionEntityDao(): MissionEntityDao
    abstract fun didiInfoEntityDao(): DidiInfoDao
    abstract fun missionActivityEntityDao(): MissionActivityDao
    abstract fun activityTaskEntityDao(): ActivityTaskDao

    abstract fun didiSectionProgressEntityDao(): DidiSectionProgressEntityDao

    abstract fun sectionAnswerEntityDao(): SectionAnswerEntityDao

    abstract fun formQuestionResponseDao(): FormQuestionResponseDao

    abstract fun inputTypeQuestionAnswerDao(): InputTypeQuestionAnswerDao


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