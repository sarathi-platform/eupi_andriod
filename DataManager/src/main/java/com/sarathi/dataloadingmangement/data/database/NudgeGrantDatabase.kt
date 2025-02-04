package com.sarathi.dataloadingmangement.data.database


import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nudge.core.database.converters.DateConverter
import com.nudge.core.database.converters.SurveyValidationsConverter
import com.nudge.core.database.converters.ValidationConverter
import com.nudge.core.utils.CoreLogger
import com.sarathi.dataloadingmangement.data.converters.ConditionsDtoConvertor
import com.sarathi.dataloadingmangement.data.converters.ContentListConverter
import com.sarathi.dataloadingmangement.data.converters.ContentMapConverter
import com.sarathi.dataloadingmangement.data.converters.MoneyJournalConfigResponseConverter
import com.sarathi.dataloadingmangement.data.converters.OptionQuestionConverter
import com.sarathi.dataloadingmangement.data.converters.QuestionsOptionsConverter
import com.sarathi.dataloadingmangement.data.converters.StringConverter
import com.sarathi.dataloadingmangement.data.converters.TagConverter
import com.sarathi.dataloadingmangement.data.converters.ValuesDtoConverter
import com.sarathi.dataloadingmangement.data.dao.ActivityConfigDao
import com.sarathi.dataloadingmangement.data.dao.ActivityDao
import com.sarathi.dataloadingmangement.data.dao.ActivityLanguageAttributeDao
import com.sarathi.dataloadingmangement.data.dao.ActivityLanguageDao
import com.sarathi.dataloadingmangement.data.dao.AttributeValueReferenceDao
import com.sarathi.dataloadingmangement.data.dao.ConditionsEntityDao
import com.sarathi.dataloadingmangement.data.dao.ContentConfigDao
import com.sarathi.dataloadingmangement.data.dao.ContentDao
import com.sarathi.dataloadingmangement.data.dao.DocumentDao
import com.sarathi.dataloadingmangement.data.dao.FormDao
import com.sarathi.dataloadingmangement.data.dao.FormUiConfigDao
import com.sarathi.dataloadingmangement.data.dao.GrantConfigDao
import com.sarathi.dataloadingmangement.data.dao.MissionDao
import com.sarathi.dataloadingmangement.data.dao.MissionLanguageAttributeDao
import com.sarathi.dataloadingmangement.data.dao.OptionItemDao
import com.sarathi.dataloadingmangement.data.dao.ProgrammeDao
import com.sarathi.dataloadingmangement.data.dao.QuestionEntityDao
import com.sarathi.dataloadingmangement.data.dao.SectionEntityDao
import com.sarathi.dataloadingmangement.data.dao.SectionStatusEntityDao
import com.sarathi.dataloadingmangement.data.dao.SourceTargetQuestionMappingEntityDao
import com.sarathi.dataloadingmangement.data.dao.SubjectAttributeDao
import com.sarathi.dataloadingmangement.data.dao.SubjectEntityDao
import com.sarathi.dataloadingmangement.data.dao.SurveyAnswersDao
import com.sarathi.dataloadingmangement.data.dao.SurveyConfigEntityDao
import com.sarathi.dataloadingmangement.data.dao.SurveyEntityDao
import com.sarathi.dataloadingmangement.data.dao.SurveyLanguageAttributeDao
import com.sarathi.dataloadingmangement.data.dao.TagReferenceEntityDao
import com.sarathi.dataloadingmangement.data.dao.TaskAttributeDao
import com.sarathi.dataloadingmangement.data.dao.TaskDao
import com.sarathi.dataloadingmangement.data.dao.UiConfigDao
import com.sarathi.dataloadingmangement.data.dao.livelihood.AssetDao
import com.sarathi.dataloadingmangement.data.dao.livelihood.AssetJournalDao
import com.sarathi.dataloadingmangement.data.dao.livelihood.LivelihoodDao
import com.sarathi.dataloadingmangement.data.dao.livelihood.LivelihoodEventDao
import com.sarathi.dataloadingmangement.data.dao.livelihood.LivelihoodLanguageDao
import com.sarathi.dataloadingmangement.data.dao.livelihood.MoneyJournalDao
import com.sarathi.dataloadingmangement.data.dao.livelihood.ProductDao
import com.sarathi.dataloadingmangement.data.dao.livelihood.SubjectLivelihoodEventMappingDao
import com.sarathi.dataloadingmangement.data.dao.livelihood.SubjectLivelihoodMappingDao
import com.sarathi.dataloadingmangement.data.dao.revamp.MissionConfigEntityDao
import com.sarathi.dataloadingmangement.data.dao.revamp.MissionLivelihoodConfigEntityDao
import com.sarathi.dataloadingmangement.data.dao.smallGroup.SmallGroupDidiMappingDao
import com.sarathi.dataloadingmangement.data.database.MigrationQueries.ADD_COLUMN_IS_DATA_LOADED_MISSION_TABLE
import com.sarathi.dataloadingmangement.data.database.MigrationQueries.ALTER_ACTIVITY_CONFIG_TABLE_ADD_COLUMN_REFERENCE_ID
import com.sarathi.dataloadingmangement.data.database.MigrationQueries.ALTER_ACTIVITY_CONFIG_TABLE_ADD_COLUMN_REFERENCE_TYPE
import com.sarathi.dataloadingmangement.data.database.MigrationQueries.ALTER_ACTIVITY_CONFIG_TABLE_ADD_MONEY_JOURNAL_CONFIG
import com.sarathi.dataloadingmangement.data.database.MigrationQueries.ALTER_ACTIVITY_TABLE_ADD_ACTIVITY_ORDER
import com.sarathi.dataloadingmangement.data.database.MigrationQueries.ALTER_ASSET_JOURNAL_TABLE_ADD_EVENT_ID
import com.sarathi.dataloadingmangement.data.database.MigrationQueries.ALTER_ASSET_JOURNAL_TABLE_ADD_EVENT_TYPE
import com.sarathi.dataloadingmangement.data.database.MigrationQueries.ALTER_LIVELIHOOD_COLUMN_ADD_VALIDATION
import com.sarathi.dataloadingmangement.data.database.MigrationQueries.ALTER_LIVELIHOOD_TABLE_ADD_PROGRAM_LIVELIHOOD_ID
import com.sarathi.dataloadingmangement.data.database.MigrationQueries.ALTER_MISSION_TABLE_ADD_MISSION_ORDER
import com.sarathi.dataloadingmangement.data.database.MigrationQueries.ALTER_MONEY_JOURNAL_TABLE_ADD_EVENT_ID
import com.sarathi.dataloadingmangement.data.database.MigrationQueries.ALTER_MONEY_JOURNAL_TABLE_ADD_EVENT_TYPE
import com.sarathi.dataloadingmangement.data.database.MigrationQueries.ALTER_QUESTION_ENTITY_ADD_FORM_ORDER
import com.sarathi.dataloadingmangement.data.database.MigrationQueries.ALTER_QUESTION_TABLE_ADD_FORM_CONTENT
import com.sarathi.dataloadingmangement.data.database.MigrationQueries.ALTER_SURVEY_ANSWER_ENTITY_ADD_CREATED_DATE
import com.sarathi.dataloadingmangement.data.database.MigrationQueries.ALTER_SURVEY_ANSWER_ENTITY_ADD_FORM_ID
import com.sarathi.dataloadingmangement.data.database.MigrationQueries.ALTER_SURVEY_ANSWER_ENTITY_ADD_MODIFIED_DATE
import com.sarathi.dataloadingmangement.data.database.MigrationQueries.ALTER_SURVEY_TABLE_COLUMN_ADD_VALIDATION
import com.sarathi.dataloadingmangement.data.database.MigrationQueries.CREATE_CONDITIONS_TABLE
import com.sarathi.dataloadingmangement.data.database.MigrationQueries.CREATE_LIVELIHOOD_ASSET_TABLE
import com.sarathi.dataloadingmangement.data.database.MigrationQueries.CREATE_LIVELIHOOD_CONFIG_ENTITY_TABLE
import com.sarathi.dataloadingmangement.data.database.MigrationQueries.CREATE_LIVELIHOOD_EVENT_MAPPING_TABLE
import com.sarathi.dataloadingmangement.data.database.MigrationQueries.CREATE_LIVELIHOOD_EVENT_TABLE
import com.sarathi.dataloadingmangement.data.database.MigrationQueries.CREATE_LIVELIHOOD_LANGUAGE_REFRENCE_TABLE
import com.sarathi.dataloadingmangement.data.database.MigrationQueries.CREATE_LIVELIHOOD_TABLE
import com.sarathi.dataloadingmangement.data.database.MigrationQueries.CREATE_MISSION_CONFIG_ENTITY_TABLE
import com.sarathi.dataloadingmangement.data.database.MigrationQueries.CREATE_MONEY_JOUNRAL_TABLE
import com.sarathi.dataloadingmangement.data.database.MigrationQueries.CREATE_NEW_LIVELIHOOD_ASSET_TABLE
import com.sarathi.dataloadingmangement.data.database.MigrationQueries.CREATE_NEW_LIVELIHOOD_LANGUAGE_REFERENCE_TABLE
import com.sarathi.dataloadingmangement.data.database.MigrationQueries.CREATE_NEW_LIVELIHOOD_PRODUCT_TABLE
import com.sarathi.dataloadingmangement.data.database.MigrationQueries.CREATE_NEW_LIVELIHOOD_TABLE
import com.sarathi.dataloadingmangement.data.database.MigrationQueries.CREATE_PRODUCT_CONFIG_TABLE
import com.sarathi.dataloadingmangement.data.database.MigrationQueries.CREATE_SECTION_STATUS_TABLE
import com.sarathi.dataloadingmangement.data.database.MigrationQueries.CREATE_SOURCE_TARGET_QUESTION_MAPPING_TABLE
import com.sarathi.dataloadingmangement.data.database.MigrationQueries.CREATE_SUBJECT_LIVELIHOOD_MAPPING_TABLE_
import com.sarathi.dataloadingmangement.data.database.MigrationQueries.CREATE_SURVEY_CONFIG_TABLE
import com.sarathi.dataloadingmangement.data.database.MigrationQueries.DROP_LANGUAGE_TABLE
import com.sarathi.dataloadingmangement.data.database.MigrationQueries.DROP_LIVELIHOOD_ASSET_TABLE
import com.sarathi.dataloadingmangement.data.database.MigrationQueries.DROP_LIVELIHOOD_PRODUCT_TABLE
import com.sarathi.dataloadingmangement.data.database.MigrationQueries.DROP_LIVELIHOOD_TABLE
import com.sarathi.dataloadingmangement.data.database.MigrationQueries.DROP_TABLE_LIVELIHOOD_LANGUAGE_REFERENCE
import com.sarathi.dataloadingmangement.data.entities.ActivityConfigEntity
import com.sarathi.dataloadingmangement.data.entities.ActivityConfigLanguageAttributesEntity
import com.sarathi.dataloadingmangement.data.entities.ActivityEntity
import com.sarathi.dataloadingmangement.data.entities.ActivityLanguageAttributesEntity
import com.sarathi.dataloadingmangement.data.entities.ActivityTaskEntity
import com.sarathi.dataloadingmangement.data.entities.AttributeValueReferenceEntity
import com.sarathi.dataloadingmangement.data.entities.ConditionsEntity
import com.sarathi.dataloadingmangement.data.entities.Content
import com.sarathi.dataloadingmangement.data.entities.ContentConfigEntity
import com.sarathi.dataloadingmangement.data.entities.DocumentEntity
import com.sarathi.dataloadingmangement.data.entities.FormEntity
import com.sarathi.dataloadingmangement.data.entities.FormUiConfigEntity
import com.sarathi.dataloadingmangement.data.entities.GrantConfigEntity
import com.sarathi.dataloadingmangement.data.entities.MissionEntity
import com.sarathi.dataloadingmangement.data.entities.MissionLanguageEntity
import com.sarathi.dataloadingmangement.data.entities.OptionItemEntity
import com.sarathi.dataloadingmangement.data.entities.ProgrammeEntity
import com.sarathi.dataloadingmangement.data.entities.QuestionEntity
import com.sarathi.dataloadingmangement.data.entities.SectionEntity
import com.sarathi.dataloadingmangement.data.entities.SectionStatusEntity
import com.sarathi.dataloadingmangement.data.entities.SourceTargetQuestionMappingEntity
import com.sarathi.dataloadingmangement.data.entities.SubjectAttributeEntity
import com.sarathi.dataloadingmangement.data.entities.SubjectEntity
import com.sarathi.dataloadingmangement.data.entities.SurveyAnswerEntity
import com.sarathi.dataloadingmangement.data.entities.SurveyConfigEntity
import com.sarathi.dataloadingmangement.data.entities.SurveyEntity
import com.sarathi.dataloadingmangement.data.entities.SurveyLanguageAttributeEntity
import com.sarathi.dataloadingmangement.data.entities.TagReferenceEntity
import com.sarathi.dataloadingmangement.data.entities.TaskAttributesEntity
import com.sarathi.dataloadingmangement.data.entities.UiConfigEntity
import com.sarathi.dataloadingmangement.data.entities.livelihood.AssetEntity
import com.sarathi.dataloadingmangement.data.entities.livelihood.AssetJournalEntity
import com.sarathi.dataloadingmangement.data.entities.livelihood.LivelihoodEntity
import com.sarathi.dataloadingmangement.data.entities.livelihood.LivelihoodEventEntity
import com.sarathi.dataloadingmangement.data.entities.livelihood.LivelihoodLanguageReferenceEntity
import com.sarathi.dataloadingmangement.data.entities.livelihood.MoneyJournalEntity
import com.sarathi.dataloadingmangement.data.entities.livelihood.ProductEntity
import com.sarathi.dataloadingmangement.data.entities.livelihood.SubjectLivelihoodEventMappingEntity
import com.sarathi.dataloadingmangement.data.entities.livelihood.SubjectLivelihoodMappingEntity
import com.sarathi.dataloadingmangement.data.entities.revamp.MissionConfigEntity
import com.sarathi.dataloadingmangement.data.entities.revamp.MissionLivelihoodConfigEntity
import com.sarathi.dataloadingmangement.data.entities.smallGroup.SmallGroupDidiMappingEntity
import java.sql.SQLException

const val NUDGE_GRANT_DATABASE_VERSION = 6

@Database(
    entities = [
        MissionEntity::class,
        ActivityEntity::class,
        ActivityTaskEntity::class,
        ActivityConfigEntity::class,
        ActivityLanguageAttributesEntity::class,
        ActivityConfigLanguageAttributesEntity::class,
        AttributeValueReferenceEntity::class,
        MissionLanguageEntity::class,
        SubjectAttributeEntity::class,
        TaskAttributesEntity::class,
        UiConfigEntity::class,
        ContentConfigEntity::class,
        Content::class,
        SurveyEntity::class,
        SectionEntity::class,
        QuestionEntity::class,
        OptionItemEntity::class,
        ProgrammeEntity::class,
        SurveyAnswerEntity::class,
        GrantConfigEntity::class,
        FormEntity::class,
        FormUiConfigEntity::class,
        DocumentEntity::class,
        SurveyLanguageAttributeEntity::class,
        SubjectEntity::class,
        SmallGroupDidiMappingEntity::class,
        TagReferenceEntity::class,
        LivelihoodLanguageReferenceEntity::class,
        LivelihoodEntity::class,
        AssetEntity::class,
        ProductEntity::class,
        LivelihoodEventEntity::class,
        MoneyJournalEntity::class,
        AssetJournalEntity::class,
        SubjectLivelihoodMappingEntity::class,
        SubjectLivelihoodEventMappingEntity::class,
        SectionStatusEntity::class,
        SourceTargetQuestionMappingEntity::class,
        ConditionsEntity::class,
        SurveyConfigEntity::class,
        MissionConfigEntity::class,
        MissionLivelihoodConfigEntity::class
    ],

    version = NUDGE_GRANT_DATABASE_VERSION,
    exportSchema = false
)
@TypeConverters(
    QuestionsOptionsConverter::class,
    OptionQuestionConverter::class,
    StringConverter::class,
    ConditionsDtoConvertor::class,
    ContentListConverter::class,
    ContentMapConverter::class,
    ValuesDtoConverter::class,
    DateConverter::class,
    TagConverter::class,
    ValidationConverter::class,
    SurveyValidationsConverter::class,
    MoneyJournalConfigResponseConverter::class
)
abstract class NudgeGrantDatabase : RoomDatabase() {

    abstract fun missionDao(): MissionDao
    abstract fun activityDao(): ActivityDao
    abstract fun taskDao(): TaskDao
    abstract fun contentDao(): ContentDao
    abstract fun activityConfigDao(): ActivityConfigDao
    abstract fun formEDao(): FormDao
    abstract fun documentDao(): DocumentDao
    abstract fun activityLanguageAttributeDao(): ActivityLanguageAttributeDao
    abstract fun activityLanguageDao(): ActivityLanguageDao
    abstract fun attributeValueReferenceDao(): AttributeValueReferenceDao
    abstract fun contentConfigDao(): ContentConfigDao
    abstract fun missionLanguageAttributeDao(): MissionLanguageAttributeDao
    abstract fun subjectAttributeDao(): SubjectAttributeDao
    abstract fun taskAttributeDao(): TaskAttributeDao
    abstract fun uiConfigDao(): UiConfigDao
    abstract fun formUiConfigDao(): FormUiConfigDao
    abstract fun surveyEntityDao(): SurveyEntityDao

    abstract fun sectionEntityDao(): SectionEntityDao

    abstract fun questionEntityDao(): QuestionEntityDao
    abstract fun optionItemDao(): OptionItemDao
    abstract fun programmeDao(): ProgrammeDao
    abstract fun surveyAnswersDao(): SurveyAnswersDao
    abstract fun grantConfigDao(): GrantConfigDao
    abstract fun surveyLanguageAttributeDao(): SurveyLanguageAttributeDao

    abstract fun subjectEntityDao(): SubjectEntityDao

    abstract fun smallGroupDidiMappingDao(): SmallGroupDidiMappingDao
    abstract fun tagReferenceEntityDao(): TagReferenceEntityDao
    abstract fun moneyJournalDao(): MoneyJournalDao
    abstract fun assetJournalDao(): AssetJournalDao
    abstract fun livelihoodLanguageDao(): LivelihoodLanguageDao
    abstract fun livelihoodDao(): LivelihoodDao
    abstract fun assetDao(): AssetDao
    abstract fun productDao(): ProductDao
    abstract fun livelihoodEventDao(): LivelihoodEventDao

    abstract fun subjectLivelihoodMappingDao(): SubjectLivelihoodMappingDao

    abstract fun sectionStatusEntityDao(): SectionStatusEntityDao

    abstract fun subjectLivelihoodEventMappingDao(): SubjectLivelihoodEventMappingDao

    abstract fun sourceTargetQuestionMappingEntityDao(): SourceTargetQuestionMappingEntityDao

    abstract fun conditionsEntityDao(): ConditionsEntityDao

    abstract fun surveyConfigEntityDao(): SurveyConfigEntityDao
    abstract fun missionConfigEntityDao(): MissionConfigEntityDao
    abstract fun missionLivelihoodConfigEntityDao(): MissionLivelihoodConfigEntityDao

    class NudgeGrantDatabaseCallback : Callback()
    companion object {
        // ADD THIS TYPE OF SQL QUERY FOR TABLE CREATION OR ALTERATION

        // CREATE MIGRATION OBJECT FOR MIGRATION 1 to 2.
        val NUDGE_GRANT_DATABASE_MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                CoreLogger.d(tag = "NudgeGrantDatabase", msg = "MIGRATION_1_2")
                migration(db, listOf(CREATE_MONEY_JOUNRAL_TABLE))
            }
        }
        val NUDGE_GRANT_DATABASE_MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                CoreLogger.d(tag = "NudgeGrantDatabase", msg = "MIGRATION_2_3")
                migration(
                    db,
                    listOf(
                        CREATE_MONEY_JOUNRAL_TABLE,
                        MigrationQueries.CREATE_ASSET_JOURNAL_TABLE,
                        CREATE_LIVELIHOOD_EVENT_MAPPING_TABLE,
                        CREATE_LIVELIHOOD_TABLE,
                        CREATE_PRODUCT_CONFIG_TABLE,
                        CREATE_LIVELIHOOD_ASSET_TABLE,
                        CREATE_LIVELIHOOD_EVENT_TABLE,
                        CREATE_SUBJECT_LIVELIHOOD_MAPPING_TABLE_,
                        CREATE_LIVELIHOOD_LANGUAGE_REFRENCE_TABLE,
                        CREATE_SECTION_STATUS_TABLE
                    )
                )
            }
        }

        val NUDGE_GRANT_DATABASE_MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                CoreLogger.d(tag = "NudgeGrantDatabase", msg = "MIGRATION_3_4")
                /**
                 * DROP OLD TABLES AND CREATE NEW ONES with correct data type for column AS SQLite Does not support drop column.
                 * */

                migration(
                    db,
                    listOf(
                        DROP_LIVELIHOOD_TABLE,
                        DROP_LIVELIHOOD_ASSET_TABLE,
                        DROP_LIVELIHOOD_PRODUCT_TABLE,
                        DROP_TABLE_LIVELIHOOD_LANGUAGE_REFERENCE,
                        CREATE_NEW_LIVELIHOOD_TABLE,
                        CREATE_NEW_LIVELIHOOD_ASSET_TABLE,
                        CREATE_NEW_LIVELIHOOD_PRODUCT_TABLE,
                        CREATE_NEW_LIVELIHOOD_LANGUAGE_REFERENCE_TABLE,
                        ALTER_LIVELIHOOD_COLUMN_ADD_VALIDATION,
                    )
                )
                migration(
                    db,
                    listOf(
                        ALTER_ACTIVITY_CONFIG_TABLE_ADD_COLUMN_REFERENCE_ID,
                        ALTER_ACTIVITY_CONFIG_TABLE_ADD_COLUMN_REFERENCE_TYPE,
                        ADD_COLUMN_IS_DATA_LOADED_MISSION_TABLE,
                        CREATE_SOURCE_TARGET_QUESTION_MAPPING_TABLE,
                        CREATE_CONDITIONS_TABLE,
                        CREATE_SURVEY_CONFIG_TABLE,
                        ALTER_SURVEY_TABLE_COLUMN_ADD_VALIDATION,
                        ALTER_SURVEY_ANSWER_ENTITY_ADD_FORM_ID,

                        ALTER_SURVEY_ANSWER_ENTITY_ADD_CREATED_DATE,
                        ALTER_SURVEY_ANSWER_ENTITY_ADD_MODIFIED_DATE
                    )
                )
            }
        }
        val NUDGE_GRANT_DATABASE_MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                CoreLogger.d(tag = "NudgeGrantDatabase", msg = "MIGRATION_4_5")
                migration(
                    db,
                    listOf(
                        ALTER_QUESTION_ENTITY_ADD_FORM_ORDER,
                        ALTER_QUESTION_TABLE_ADD_FORM_CONTENT,
                        ALTER_MISSION_TABLE_ADD_MISSION_ORDER,
                        ALTER_ACTIVITY_TABLE_ADD_ACTIVITY_ORDER
                    )
                )
            }
        }
        val NUDGE_GRANT_DATABASE_MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                CoreLogger.d(tag = "NudgeGrantDatabase", msg = "MIGRATION_5_6")
                migration(
                    db,
                    listOf(
                        DROP_LANGUAGE_TABLE,
                        ALTER_LIVELIHOOD_TABLE_ADD_PROGRAM_LIVELIHOOD_ID,
                        CREATE_MISSION_CONFIG_ENTITY_TABLE,
                        CREATE_LIVELIHOOD_CONFIG_ENTITY_TABLE,
                        ALTER_ACTIVITY_CONFIG_TABLE_ADD_MONEY_JOURNAL_CONFIG,
                        ALTER_ASSET_JOURNAL_TABLE_ADD_EVENT_ID,
                        ALTER_ASSET_JOURNAL_TABLE_ADD_EVENT_TYPE,
                        ALTER_MONEY_JOURNAL_TABLE_ADD_EVENT_ID,
                        ALTER_MONEY_JOURNAL_TABLE_ADD_EVENT_TYPE
                    )
                )
            }
        }

        private fun migration(database: SupportSQLiteDatabase, execSqls: List<String>) {
            for (sql in execSqls) {
                try {
                    database.execSQL(sql)
                } catch (e: SQLException) {
                    CoreLogger.e(
                        tag = "NudgeGrantDatabase",
                        msg = "migration \"$sql\" Migration Error",
                        ex = e,
                        stackTrace = true
                    )
                } catch (t: Throwable) {
                    CoreLogger.e(
                        tag = "NudgeGrantDatabase",
                        msg = "migration \"$sql\"",
                        ex = t,
                        stackTrace = true
                    )
                }
            }
        }

    }
}