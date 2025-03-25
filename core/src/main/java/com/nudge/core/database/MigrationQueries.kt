package com.nudge.core.database

import com.nudge.core.ApiCallConfigTable
import com.nudge.core.ApiCallJournalTable
import com.nudge.core.CASTE_TABLE
import com.nudge.core.LANGUAGE_TABLE_NAME
import com.nudge.core.TRANSLATION_CONFIG_TABLE_NAME


object MigrationQueries {
    const val CREATE_CASTE_TABLE =
        "CREATE TABLE IF NOT EXISTS $CASTE_TABLE (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `casteId` INTEGER, `casteName` TEXT NOT NULL, `languageId` INTEGER NOT NULL, `languageCode` TEXT NOT NULL)"
    const val CREATE_LANGUAGE_TABLE =
        "CREATE TABLE IF NOT EXISTS $LANGUAGE_TABLE_NAME (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `orderNumber` INTEGER NOT NULL, `language` TEXT NOT NULL, `langCode` TEXT, `localName` TEXT)"
    const val CREATE_TRANSLATION_CONFIG_TABLE =
        "CREATE TABLE IF NOT EXISTS $TRANSLATION_CONFIG_TABLE_NAME (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userId` TEXT NOT NULL, `key` TEXT NOT NULL, `value` TEXT NOT NULL, `languageCode` TEXT NOT NULL)"
    const val CREATE_API_CALL_JOURNAL_TABLE =
        "CREATE TABLE IF NOT EXISTS $ApiCallJournalTable (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userId` TEXT NOT NULL, `apiUrl` TEXT NOT NULL, `status` TEXT NOT NULL, `moduleName` TEXT NOT NULL, `screenName` TEXT NOT NULL, `requestBody` TEXT,`triggerPoint` TEXT NOT NULL,`errorMsg` TEXT  ,`retryCount` INTEGER NOT NULL,`createdDate` INTEGER NOT NULL,`modifiedDate` INTEGER NOT NULL)"
    const val CREATE_API_CALL_CONFIG_TABLE =
        "CREATE TABLE IF NOT EXISTS $ApiCallConfigTable (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userId` TEXT NOT NULL, `apiUrls` TEXT NOT NULL, `screenName` TEXT NOT NULL, `triggerPoint` TEXT NOT NULL, `apiType` TEXT NOT NULL, `moduleName` TEXT NOT NULL)"
}

