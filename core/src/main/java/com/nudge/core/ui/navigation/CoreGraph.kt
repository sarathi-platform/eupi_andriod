package com.nudge.core.ui.navigation

object CoreGraph {
    const val ROOT = "root_graph"
    const val AUTHENTICATION = "auth_graph"
    val HOME = "home_graph"
    val DETAILS = "details_graph/{${CoreNavigationParams.ARG_VILLAGE_ID.value}}/{${CoreNavigationParams.ARG_STEP_ID.value}}/{${CoreNavigationParams.ARG_STEP_INDEX.value}}"
    val ADD_DIDI = "add_didi_graph/{${CoreNavigationParams.ARG_DIDI_DETAILS_ID.value}"
    val SOCIAL_MAPPING = "social_mapping_graph/{${CoreNavigationParams.ARG_VILLAGE_ID.value}}/{${CoreNavigationParams.ARG_STEP_ID.value}}"
    val WEALTH_RANKING = "wealth_ranking/{${CoreNavigationParams.ARG_VILLAGE_ID.value}}/{${CoreNavigationParams.ARG_STEP_ID.value}}"
    val PAT_SCREENS = "pat_screens/{${CoreNavigationParams.ARG_VILLAGE_ID.value}}/{${CoreNavigationParams.ARG_STEP_ID.value}}"
    val SETTING_GRAPH = "setting_graph"
    val LOGOUT_GRAPH = "logout_graph"
    val VO_ENDORSEMENT_GRAPH = "vo_endorsement_graph/{${CoreNavigationParams.ARG_VILLAGE_ID.value}}/{${CoreNavigationParams.ARG_STEP_ID.value}}/{${CoreNavigationParams.ARG_IS_STEP_COMPLETE.value}}"
    val BPC_GRAPH = "bpc_graph/{${CoreNavigationParams.ARG_VILLAGE_ID.value}}/{${CoreNavigationParams.ARG_STEP_ID.value}}"
}

