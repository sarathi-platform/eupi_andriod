package com.nrlm.baselinesurvey.database

import android.util.Log
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nrlm.baselinesurvey.database.converters.BeneficiaryStepConverter
import com.nrlm.baselinesurvey.database.converters.ConditionsDtoConvertor
import com.nrlm.baselinesurvey.database.converters.ContentListConverter
import com.nrlm.baselinesurvey.database.converters.ContentMapConverter
import com.nrlm.baselinesurvey.database.converters.IntConverter
import com.nrlm.baselinesurvey.database.converters.OptionQuestionConverter
import com.nrlm.baselinesurvey.database.converters.QuestionsOptionsConverter
import com.nrlm.baselinesurvey.database.converters.StringConverter
import com.nrlm.baselinesurvey.database.converters.ValuesDtoConverter
import com.nrlm.baselinesurvey.database.dao.ActivityTaskDao
import com.nrlm.baselinesurvey.database.dao.ContentDao
import com.nrlm.baselinesurvey.database.dao.DidiInfoDao
import com.nrlm.baselinesurvey.database.dao.DidiSectionProgressEntityDao
import com.nrlm.baselinesurvey.database.dao.FormQuestionResponseDao
import com.nrlm.baselinesurvey.database.dao.InputTypeQuestionAnswerDao
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
import com.nrlm.baselinesurvey.database.entity.ContentEntity
import com.nrlm.baselinesurvey.database.entity.DidiInfoEntity
import com.nrlm.baselinesurvey.database.entity.DidiSectionProgressEntity
import com.nrlm.baselinesurvey.database.entity.FormQuestionResponseEntity
import com.nrlm.baselinesurvey.database.entity.InputTypeQuestionAnswerEntity
import com.nrlm.baselinesurvey.database.entity.MissionActivityEntity
import com.nrlm.baselinesurvey.database.entity.MissionEntity
import com.nrlm.baselinesurvey.database.entity.OptionItemEntity
import com.nrlm.baselinesurvey.database.entity.QuestionEntity
import com.nrlm.baselinesurvey.database.entity.SectionAnswerEntity
import com.nrlm.baselinesurvey.database.entity.SectionEntity
import com.nrlm.baselinesurvey.database.entity.SurveyEntity
import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import com.nrlm.baselinesurvey.database.entity.VillageEntity
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.nudge.core.LANGUAGE_TABLE_NAME
import java.sql.SQLException

// Increase DB Version everytime any change is made to any table or a new table is added.
const val NUDGE_BASELINE_DATABASE_VERSION = 4

@Database(
    entities = [
        VillageEntity::class,
        SurveyeeEntity::class,
        SurveyEntity::class,
        SectionEntity::class,
        QuestionEntity::class,
        OptionItemEntity::class,
        MissionEntity::class,
        MissionActivityEntity::class,
        ActivityTaskEntity::class,
        DidiInfoEntity::class,
        DidiSectionProgressEntity::class,
        SectionAnswerEntity::class,
        FormQuestionResponseEntity::class,
        InputTypeQuestionAnswerEntity::class,
        ContentEntity::class
    ],
    version = NUDGE_BASELINE_DATABASE_VERSION,
    exportSchema = false
)
@TypeConverters(
    IntConverter::class,
    BeneficiaryStepConverter::class,
    QuestionsOptionsConverter::class,
    OptionQuestionConverter::class,
    StringConverter::class,
    ConditionsDtoConvertor::class,
    ContentListConverter::class,
    ContentMapConverter::class,
    ValuesDtoConverter::class
)
abstract class NudgeBaselineDatabase: RoomDatabase()  {

    abstract fun villageListDao(): VillageListDao


    abstract fun didiDao(): SurveyeeEntityDao

    abstract fun surveyEntityDao(): SurveyEntityDao

    abstract fun sectionEntityDao(): SectionEntityDao

    abstract fun questionEntityDao(): QuestionEntityDao
    abstract fun optionItemDao(): OptionItemDao

    abstract fun missionEntityDao(): MissionEntityDao
    abstract fun didiInfoEntityDao(): DidiInfoDao
    abstract fun missionActivityEntityDao(): MissionActivityDao
    abstract fun activityTaskEntityDao(): ActivityTaskDao
    abstract fun contentEntityDao(): ContentDao

    abstract fun didiSectionProgressEntityDao(): DidiSectionProgressEntityDao

    abstract fun sectionAnswerEntityDao(): SectionAnswerEntityDao

    abstract fun formQuestionResponseDao(): FormQuestionResponseDao

    abstract fun inputTypeQuestionAnswerDao(): InputTypeQuestionAnswerDao

    companion object {

        // ADD THIS TYPE OF SQL QUERY FOR TABLE CREATION OR ALTERATION
        private const val ALTER_FORM_RESPONSE_TABLE =
            "ALTER TABLE form_question_response_table ADD selectedValueId TEXT NOT NULL DEFAULT '';"


        // CREATE MIGRATION OBJECT FOR MIGRATION 1 to 2.
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                Log.d("NudgeDatabase", "MIGRATION_1_2")
                migration(database, listOf(ALTER_FORM_RESPONSE_TABLE))
            }
        }

        //        val DROP_CASTE_TABLE = "DROP TABLE $CASTE_TABLE"
//        val MIGRATION_2_3 = object : Migration(2, 3) {
//            override fun migrate(db: SupportSQLiteDatabase) {
//                Log.d("NudgeDatabase", "MIGRATION_2_3")
//                migration(db, listOf(DROP_CASTE_TABLE))
//            }
//        }
        const val DROP_LANGUAGE_TABLE = "DROP TABLE $LANGUAGE_TABLE_NAME"
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                Log.d("NudgeDatabase", "MIGRATION_3_4")
                migration(db, listOf(DROP_LANGUAGE_TABLE))
            }
        }


        private fun migration(database: SupportSQLiteDatabase, execSqls: List<String>) {
            for(sql in execSqls) {
                try {
                    database.execSQL(sql)
                    BaselineLogger.i(
                        "NudgeBaselineDatabase",
                        "migration \"$sql\" Migration success"
                    )
                } catch (e: SQLException) {
                    BaselineLogger.e(
                        "NudgeBaselineDatabase",
                        "migration \"$sql\" Migration Error",
                        e
                    )
                } catch (t: Throwable) {
                    BaselineLogger.e("NudgeBaselineDatabase", "migration \"$sql\"", t)
                }
            }
        }
    }

    class NudgeDatabaseCallback : Callback() {
    }

}