package com.sarathi.dataloadingmangement.data.database

import com.sarathi.dataloadingmangement.ACTIVITY_CONFIG_TABLE_NAME
import com.sarathi.dataloadingmangement.ASSETS_TABLE_NAME
import com.sarathi.dataloadingmangement.ASSET_JOURNAL_TABLE_NAME
import com.sarathi.dataloadingmangement.LIVELIHOOD_EVENT_TABLE_NAME
import com.sarathi.dataloadingmangement.LIVELIHOOD_LANGUAGE_TABLE_NAME
import com.sarathi.dataloadingmangement.LIVELIHOOD_TABLE_NAME
import com.sarathi.dataloadingmangement.MONEY_JOURNAL_TABLE_NAME
import com.sarathi.dataloadingmangement.PRODUCT_TABLE_NAME
import com.sarathi.dataloadingmangement.SECTION_STATUS_TABLE_NAME
import com.sarathi.dataloadingmangement.SUBJECT_LIVELIHOOD_EVENT_MAPPING_TABLE_NAME
import com.sarathi.dataloadingmangement.SUBJECT_LIVELIHOOD_MAPPING_TABLE_NAME

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

    val ALTER_LIVELIHOOD_LANGUAGE_REFERENCE_COLUMN_NAME =
        "ALTER TABLE $LIVELIHOOD_LANGUAGE_TABLE_NAME RENAME COLUMN livelihoodId TO referenceId\n"


    val ALTER_ACTIVITY_CONFIG_TABLE_ADD_COLUMN_REFERENCE_ID =
        "ALTER TABLE $ACTIVITY_CONFIG_TABLE_NAME ADD COLUMN 'referenceId' INTEGER"

    val ALTER_ACTIVITY_CONFIG_TABLE_ADD_COLUMN_REFERENCE_TYPE =
        "ALTER TABLE $ACTIVITY_CONFIG_TABLE_NAME ADD COLUMN 'referenceType' TEXT"
}

