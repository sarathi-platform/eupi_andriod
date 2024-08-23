package com.patsurvey.nudge.utils

enum class StepType {
    WEALTH_RANKING,
    PAT_SURVEY,
    SOCIAL_MAPPING,
    VO_ENDROSEMENT,
    TRANSECT_WALK,
    BPC_VERIFICATION;

    companion object {

        fun getStepTypeFromId(id: Int): StepType {
            return when (id) {
                40 -> TRANSECT_WALK
                41 -> SOCIAL_MAPPING
                43 -> PAT_SURVEY
                44 -> VO_ENDROSEMENT
                45 -> BPC_VERIFICATION
                46 -> WEALTH_RANKING
                else -> TRANSECT_WALK
            }
        }
    }
}