package com.nudge.core.enums

enum class ActivityTypeEnum {
    GRANT, SURVEY, SELECT, BASIC, LIVELIHOOD;

    companion object {
        fun getActivityTypeFromId(activityConfigId: Int): ActivityTypeEnum {
            return when (activityConfigId) {
                1 -> GRANT
                2 -> SURVEY
                3 -> BASIC
                4 -> SELECT
                5 -> LIVELIHOOD
                else -> SURVEY
            }
        }

    }


}