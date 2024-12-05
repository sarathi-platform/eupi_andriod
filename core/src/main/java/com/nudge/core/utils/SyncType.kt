package com.nudge.core.utils

enum class SyncType {
    SYNC_ALL,
    SYNC_ONLY_DATA,
    SYNC_ONLY_IMAGES;

    companion object {

        fun getSyncTypeFromInt(syncType: Int): String {
            return when (syncType) {
                SYNC_ALL.ordinal -> SYNC_ALL.name
                SYNC_ONLY_DATA.ordinal -> SYNC_ONLY_DATA.name
                SYNC_ONLY_IMAGES.ordinal -> SYNC_ONLY_IMAGES.name
                else -> SYNC_ALL.name
            }
        }

    }
}