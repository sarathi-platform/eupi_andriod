package com.nudge.core.enums

enum class ActivityTypeEnum {
    GRANT, SURVEY, SELECT, BASIC, LIVELIHOOD;

    companion object {
        fun getActivityTypeFromId(activityConfigId: Int?): ActivityTypeEnum {
            return when (activityConfigId) {
                1 -> GRANT
                2 -> SURVEY
                3 -> BASIC
                4 -> SELECT
                5 -> LIVELIHOOD
                else -> SURVEY
            }
        }

        fun getActivityTypeIdFromName(activityType: String): Int {

            return when (activityType.toLowerCase()) {
                GRANT.name.toLowerCase() -> 1
                SURVEY.name.toLowerCase() -> 2
                BASIC.name.toLowerCase() -> 3
                SELECT.name.toLowerCase() -> 4
                LIVELIHOOD.name.toLowerCase() -> 5
                else -> 2

            }

        }

    }


}