package com.sarathi.dataloadingmangement.data.database

import com.sarathi.dataloadingmangement.ACTIVITY_CONFIG_TABLE_NAME
import com.sarathi.dataloadingmangement.ANSWER_TABLE
import com.sarathi.dataloadingmangement.ASSETS_TABLE_NAME
import com.sarathi.dataloadingmangement.ASSET_JOURNAL_TABLE_NAME
import com.sarathi.dataloadingmangement.CONDITIONS_TABLE_NAME
import com.sarathi.dataloadingmangement.LIVELIHOOD_EVENT_TABLE_NAME
import com.sarathi.dataloadingmangement.LIVELIHOOD_LANGUAGE_TABLE_NAME
import com.sarathi.dataloadingmangement.LIVELIHOOD_TABLE_NAME
import com.sarathi.dataloadingmangement.MISSION_TABLE_NAME
import com.sarathi.dataloadingmangement.MONEY_JOURNAL_TABLE_NAME
import com.sarathi.dataloadingmangement.PRODUCT_TABLE_NAME
import com.sarathi.dataloadingmangement.QUESTION_TABLE
import com.sarathi.dataloadingmangement.SECTION_STATUS_TABLE_NAME
import com.sarathi.dataloadingmangement.SOURCE_TARGET_QUESTION_MAPPING_TABLE_NAME
import com.sarathi.dataloadingmangement.SUBJECT_LIVELIHOOD_EVENT_MAPPING_TABLE_NAME
import com.sarathi.dataloadingmangement.SUBJECT_LIVELIHOOD_MAPPING_TABLE_NAME
import com.sarathi.dataloadingmangement.SURVEY_CONFIG_TABLE_NAME
import com.sarathi.dataloadingmangement.SURVEY_TABLE

object MigrationQueries {
    val CREATE_MONEY_JOUNRAL_TABLE =
        "CREATE TABLE IF NOT EXISTS $MONEY_JOURNAL_TABLE_NAME (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userId` TEXT NOT NULL, `transactionId` TEXT NOT NULL, `transactionDate` INTEGER NOT NULL, `transactionDetails` TEXT NOT NULL, `transactionFlow` TEXT NOT NULL, `transactionType` TEXT NOT NULL, `transactionAmount` REAL NOT NULL, `referenceId` INTEGER NOT NULL, `referenceType` TEXT NOT NULL, `subjectId` INTEGER NOT NULL, `subjectType` TEXT NOT NULL, `status` INTEGER NOT NULL, `modifiedDate` INTEGER NOT NULL,`createdDate` INTEGER NOT NULL)"
    val CREATE_ASSET_JOURNAL_TABLE = "CREATE TABLE IF NOT EXISTS $ASSET_JOURNAL_TABLE_NAME (\n" +
            "    'id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
            "    userId TEXT NOT NULL,\n" +
            "    transactionId TEXT NOT NULL,\n" +
            "    transactionDate INTEGER NOT NULL,\n" +
            "    transactionDetails TEXT NOT NULL,\n" +
            "    transactionFlow TEXT NOT NULL,\n" +
            "    transactionType TEXT NOT NULL,\n" +
            "    assetId INTEGER NOT NULL,\n" +
            "    assetCount INTEGER NOT NULL,\n" +
            "    referenceId INTEGER NOT NULL,\n" +
            "    referenceType TEXT NOT NULL,\n" +
            "    subjectId INTEGER NOT NULL,\n" +
            "    subjectType TEXT NOT NULL,\n" +
            "    status INTEGER NOT NULL,\n" +
            "    modifiedDate INTEGER NOT NULL,\n" +
            "    createdDate INTEGER NOT NULL\n" +
            ")"
    val CREATE_LIVELIHOOD_EVENT_MAPPING_TABLE =
        "CREATE TABLE IF NOT EXISTS $SUBJECT_LIVELIHOOD_EVENT_MAPPING_TABLE_NAME (\n" +
                "    'id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                "    userId TEXT NOT NULL,\n" +
                "    transactionId TEXT NOT NULL,\n" +
                "    subjectId INTEGER NOT NULL,\n" +
                "    'date' INTEGER NOT NULL,\n" +
                "    livelihoodId INTEGER NOT NULL,\n" +
                "    livelihoodEventId INTEGER NOT NULL,\n" +
                "    livelihoodEventType TEXT NOT NULL,\n" +
                "    surveyResponse TEXT NOT NULL,\n" +
                "    status INTEGER NOT NULL,\n" +
                "    createdDate INTEGER NOT NULL,\n" +
                "    modifiedDate INTEGER NOT NULL\n" +
                ")"

    val CREATE_PRODUCT_CONFIG_TABLE = "CREATE TABLE IF NOT EXISTS $PRODUCT_TABLE_NAME (\n" +
            "    'id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
            "    productId INTEGER NOT NULL,\n" +
            "    livelihoodId INTEGER NOT NULL,\n" +
            "    userId TEXT NOT NULL,\n" +
            "    name TEXT NOT NULL,\n" +
            "    status INTEGER NOT NULL,\n" +
            "    type INTEGER DEFAULT 0\n" +
            ")"

    val CREATE_LIVELIHOOD_ASSET_TABLE = "CREATE TABLE IF NOT EXISTS $ASSETS_TABLE_NAME (\n" +
            "    'id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
            "    assetId INTEGER NOT NULL,\n" +
            "    livelihoodId INTEGER NOT NULL,\n" +
            "    userId TEXT NOT NULL,\n" +
            "    name TEXT NOT NULL,\n" +
            "    status INTEGER NOT NULL,\n" +
            "    type INTEGER DEFAULT 0,\n" +
            "    'value' REAL DEFAULT 0.0,\n" +
            "    'image' TEXT\n" +
            ")"
    val CREATE_LIVELIHOOD_EVENT_TABLE =
        "CREATE TABLE IF NOT EXISTS  $LIVELIHOOD_EVENT_TABLE_NAME (\n" +
                "    'id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                "    eventId INTEGER NOT NULL,\n" +
                "    livelihoodId INTEGER NOT NULL,\n" +
                "    userId TEXT NOT NULL,\n" +
                "    name TEXT NOT NULL,\n" +
                "    status INTEGER NOT NULL,\n" +
                "    type TEXT\n" +
                ")"

    val CREATE_LIVELIHOOD_TABLE = "CREATE TABLE IF NOT EXISTS  $LIVELIHOOD_TABLE_NAME (\n" +
            "    'id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
            "    livelihoodId INTEGER NOT NULL,\n" +
            "    userId TEXT NOT NULL,\n" +
            "    name TEXT NOT NULL,\n" +
            "    status INTEGER NOT NULL,\n" +
            "    type INTEGER DEFAULT 0,\n" +
            "    image TEXT\n" +
            ")"

    val CREATE_LIVELIHOOD_LANGUAGE_REFRENCE_TABLE =
        "CREATE TABLE  IF NOT EXISTS  $LIVELIHOOD_LANGUAGE_TABLE_NAME (\n" +
                "    'id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                "    livelihoodId INTEGER NOT NULL,\n" +
                "    referenceType TEXT NOT NULL,\n" +
                "    languageCode TEXT NOT NULL,\n" +
                "    name TEXT NOT NULL,\n" +
                "    userId TEXT NOT NULL\n" +
                ")"

    val CREATE_SUBJECT_LIVELIHOOD_MAPPING_TABLE_ =
        "CREATE TABLE IF NOT EXISTS  $SUBJECT_LIVELIHOOD_MAPPING_TABLE_NAME (\n" +
                "    'id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                "    userId TEXT NOT NULL,\n" +
                "    subjectId INTEGER NOT NULL,\n" +
                "    livelihoodId INTEGER NOT NULL,\n" +
                "    type INTEGER NOT NULL,\n" +
                "    status INTEGER NOT NULL\n" +
                ")"

    val CREATE_SECTION_STATUS_TABLE = "CREATE TABLE IF NOT EXISTS $SECTION_STATUS_TABLE_NAME (\n" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
            "userId TEXT DEFAULT '',\n" +
            "missionId INTEGER NOT NULL,\n" +
            "surveyId INTEGER NOT NULL,\n" +
            "sectionId INTEGER NOT NULL,\n" +
            " taskId INTEGER NOT NULL,\n" +
            "sectionStatus TEXT\n" +
            ")"

    val ADD_COLUMN_IS_DATA_LOADED_MISSION_TABLE =
        "ALTER TABLE $MISSION_TABLE_NAME ADD COLUMN 'isDataLoaded' INTEGER DEFAULT 1 NOT NULL"


    val ALTER_ACTIVITY_CONFIG_TABLE_ADD_COLUMN_REFERENCE_ID =
        "ALTER TABLE $ACTIVITY_CONFIG_TABLE_NAME ADD COLUMN 'referenceId' INTEGER"

    val ALTER_ACTIVITY_CONFIG_TABLE_ADD_COLUMN_REFERENCE_TYPE =
        "ALTER TABLE $ACTIVITY_CONFIG_TABLE_NAME ADD COLUMN 'referenceType' TEXT"


    val CREATE_SOURCE_TARGET_QUESTION_MAPPING_TABLE =
        "CREATE TABLE IF NOT EXISTS $SOURCE_TARGET_QUESTION_MAPPING_TABLE_NAME (\n" +
                "                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                "                userId TEXT NOT NULL,\n" +
                "                surveyId INTEGER NOT NULL,\n" +
                "                sectionId INTEGER NOT NULL,\n" +
                "                sourceQuestionId INTEGER NOT NULL,\n" +
                "                targetQuestionId INTEGER NOT NULL,\n" +
                "                conditionOperator TEXT \n" +
                "            )"

    val CREATE_CONDITIONS_TABLE =
        "CREATE TABLE IF NOT EXISTS $CONDITIONS_TABLE_NAME (\n" +
                "                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                "                userId TEXT,\n" +
                "                sourceTargetQuestionRefId INTEGER NOT NULL,\n" +
                "                conditions TEXT NOT NULL \n" +
                "            )"


    val ALTER_LIVELIHOOD_COLUMN_ADD_VALIDATION =
        "ALTER TABLE $LIVELIHOOD_TABLE_NAME ADD COLUMN validations TEXT\n"


    val DROP_LIVELIHOOD_ASSET_TABLE = "DROP TABLE $ASSETS_TABLE_NAME"
    val DROP_LIVELIHOOD_PRODUCT_TABLE = "DROP TABLE $PRODUCT_TABLE_NAME"
    val DROP_LIVELIHOOD_TABLE = "DROP TABLE $LIVELIHOOD_TABLE_NAME"

    val CREATE_NEW_LIVELIHOOD_ASSET_TABLE = "CREATE TABLE IF NOT EXISTS $ASSETS_TABLE_NAME (\n" +
            "    'id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
            "    assetId INTEGER NOT NULL,\n" +
            "    livelihoodId INTEGER NOT NULL,\n" +
            "    userId TEXT NOT NULL,\n" +
            "    name TEXT NOT NULL,\n" +
            "    status INTEGER NOT NULL,\n" +
            "    type TEXT,\n" +
            "    'value' REAL DEFAULT 0.0,\n" +
            "    'image' TEXT\n" +
            ")"

    val CREATE_NEW_LIVELIHOOD_PRODUCT_TABLE = "CREATE TABLE IF NOT EXISTS $PRODUCT_TABLE_NAME (\n" +
            "    'id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
            "    productId INTEGER NOT NULL,\n" +
            "    livelihoodId INTEGER NOT NULL,\n" +
            "    userId TEXT NOT NULL,\n" +
            "    name TEXT NOT NULL,\n" +
            "    status INTEGER NOT NULL,\n" +
            "    type TEXT\n" +
            ")"

    val CREATE_NEW_LIVELIHOOD_TABLE = "CREATE TABLE IF NOT EXISTS  $LIVELIHOOD_TABLE_NAME (\n" +
            "    'id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
            "    livelihoodId INTEGER NOT NULL,\n" +
            "    userId TEXT NOT NULL,\n" +
            "    name TEXT NOT NULL,\n" +
            "    status INTEGER NOT NULL,\n" +
            "    type TEXT,\n" +
            "    image TEXT\n" +
            ")"

    val CREATE_SURVEY_CONFIG_TABLE =
        "CREATE TABLE IF NOT EXISTS $SURVEY_CONFIG_TABLE_NAME (\n" +
                "                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                "                key TEXT NOT NULL,\n" +
                "                type TEXT NOT NULL,\n" +
                "                tagId INTEGER NOT NULL,\n" +
                "                value TEXT NOT NULL,\n" +
                "                icon TEXT NOT NULL,\n" +
                "                label TEXT NOT NULL,\n" +
                "                componentType TEXT NOT NULL,\n" +
                "                language TEXT NOT NULL,\n" +
                "                activityId INTEGER NOT NULL,\n" +
                "                missionId INTEGER NOT NULL,\n" +
                "                formId INTEGER NOT NULL,\n" +
                "                surveyId INTEGER NOT NULL,\n" +
                "                userId TEXT NOT NULL\n" +
                "            )"

    val ALTER_SURVEY_TABLE_COLUMN_ADD_VALIDATION =
        "ALTER TABLE $SURVEY_TABLE ADD COLUMN validations TEXT"

    val DROP_TABLE_LIVELIHOOD_LANGUAGE_REFERENCE =
        "DROP TABLE $LIVELIHOOD_LANGUAGE_TABLE_NAME"

    val CREATE_NEW_LIVELIHOOD_LANGUAGE_REFERENCE_TABLE =
        "CREATE TABLE $LIVELIHOOD_LANGUAGE_TABLE_NAME (\n" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                "    referenceId INTEGER NOT NULL,\n" +
                "    referenceType TEXT NOT NULL,\n" +
                "    languageCode TEXT NOT NULL,\n" +
                "    name TEXT NOT NULL,\n" +
                "    userId TEXT NOT NULL\n" +
                ");"

    val ALTER_SURVEY_ANSWER_ENTITY_ADD_FORM_ID =
        "ALTER TABLE $ANSWER_TABLE ADD COLUMN formId INTEGER DEFAULT 0  NOT NULL"

    val ALTER_SURVEY_ANSWER_ENTITY_ADD_CREATED_DATE =
        "ALTER TABLE $ANSWER_TABLE ADD COLUMN createdDate INTEGER DEFAULT 0  NOT NULL"

    val ALTER_SURVEY_ANSWER_ENTITY_ADD_MODIFIED_DATE =
        "ALTER TABLE $ANSWER_TABLE ADD COLUMN modifiedDate INTEGER DEFAULT 0  NOT NULL"
    val ALTER_QUESTION_ENTITY_ADD_FORM_ORDER =
        "ALTER TABLE $QUESTION_TABLE ADD COLUMN formOrder INTEGER DEFAULT 0  NOT NULL"
}

