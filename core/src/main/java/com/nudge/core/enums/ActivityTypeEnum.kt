package com.nudge.core.enums

enum class ActivityTypeEnum {
    GRANT, SURVEY, SELECT, BASIC, LIVELIHOOD, LIVELIHOOD_PoP, TRAINING;

    companion object {
        fun getActivityTypeFromId(activityConfigId: Int?): ActivityTypeEnum {
            return when (activityConfigId) {
                1 -> GRANT
                2 -> SURVEY
                3 -> BASIC
                4 -> SELECT
                5 -> LIVELIHOOD
                6 -> LIVELIHOOD_PoP
                7 -> TRAINING
                else -> SURVEY
            }
        }

        fun showSurveyQuestionOnTaskScreen(activityType: String?): Boolean {

            if (activityType == null)
                return false

            return activityType.equals(SELECT.name, true) || activityType.equals(
                TRAINING.name,
                true
            )


        }

    }


}

enum class SurveyFlow {

    GrantSurveySummaryScreen,
    SurveyScreen,
    SectionScreen,
    LivelihoodPlanningScreen;

    companion object {

        fun getSurveyFlowFromSectionScreenForActivityType(activityType: String): SurveyFlow {
            return when (activityType.toLowerCase()) {

                ActivityTypeEnum.BASIC.name.toLowerCase(),
                ActivityTypeEnum.LIVELIHOOD_PoP.name.toLowerCase() -> {
                    SurveyScreen
                }

                ActivityTypeEnum.GRANT.name.toLowerCase() -> {
                    GrantSurveySummaryScreen
                }

                else -> SurveyScreen
            }

        }

        fun getSurveyFlowFromTaskScreenForActivityType(activityTypeId: Int?): SurveyFlow {
            return when (ActivityTypeEnum.getActivityTypeFromId(activityTypeId)) {

                ActivityTypeEnum.LIVELIHOOD -> {
                    LivelihoodPlanningScreen
                }

                ActivityTypeEnum.GRANT -> {
                    GrantSurveySummaryScreen
                }

                else -> SurveyScreen
            }

        }

    }

}