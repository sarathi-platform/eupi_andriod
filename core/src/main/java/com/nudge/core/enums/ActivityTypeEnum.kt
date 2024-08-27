package com.nudge.core.enums

enum class ActivityTypeEnum {
    GRANT, SURVEY, SELECT, BASIC, LIVELIHOOD, LIVELIHOOD_PoP;

    companion object {
        fun getActivityTypeFromId(activityConfigId: Int?): ActivityTypeEnum {
            return when (activityConfigId) {
                1 -> GRANT
                2 -> SURVEY
                3 -> BASIC
                4 -> SELECT
                5 -> LIVELIHOOD
                6 -> LIVELIHOOD_PoP
                else -> SURVEY
            }
        }

    }


}