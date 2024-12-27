package com.nudge.core.database

import com.nudge.core.CASTE_TABLE


object MigrationQueries {
    val CREATE_CASTE_TABLE =
        "CREATE TABLE IF NOT EXISTS $CASTE_TABLE (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `casteId` INTEGER, `casteName` TEXT NOT NULL, `languageId` INTEGER NOT NULL, `languageCode` TEXT NOT NULL)"


}

