package com.patsurvey.nudge.activities.ui.home

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.nrlm.baselinesurvey.ui.surveyee_screen.presentation.DataLoadingScreenComponent
import com.nudge.navigationmanager.graphs.HomeScreens
import com.nudge.navigationmanager.graphs.NudgeNavigationGraph
import com.patsurvey.nudge.activities.ProgressScreen
import com.patsurvey.nudge.activities.ui.bpc.progress_screens.BpcProgressScreen
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.utils.UPCM_USER

@Composable
fun HomeUserScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    prefRepo: PrefRepo,
    onSettingIconClick: () -> Unit
) {
    if(prefRepo.getLoggedInUserType().equals(UPCM_USER)) {
        DataLoadingScreenComponent(viewModel = hiltViewModel(), navController = navController)
    }else {
        if (prefRepo.isUserBPC()) {
            BpcProgressScreen(
                bpcProgreesScreenViewModel = hiltViewModel(),
                navController = navController,
                modifier = Modifier.fillMaxWidth(),
                onNavigateToStep = { villageId, stepId ->
                    navController.navigate("bpc_graph/$villageId/$stepId")
                },
                onNavigateToSetting = {
                    navController.navigate(NudgeNavigationGraph.SETTING_GRAPH)
                },
                onBackClick = {
                    navController.navigate(HomeScreens.VILLAGE_SELECTION_SCREEN.route)
                }
            )
        } else {
            ProgressScreen(
                viewModel = hiltViewModel(),
                modifier = Modifier.fillMaxWidth(),
                onNavigateToStep = { villageId, stepId, index, isStepComplete ->
                    when (index) {
                        0 -> navController.navigate("details_graph/$villageId/$stepId/$index")
                        1 -> navController.navigate("social_mapping_graph/$villageId/$stepId")
                        2 -> navController.navigate("wealth_ranking/$villageId/$stepId")
                        3 -> navController.navigate("pat_screens/$villageId/$stepId")
                        4 -> navController.navigate("vo_endorsement_graph/$villageId/$stepId/$isStepComplete")
                    }
                },
                onNavigateToSetting = {
                    navController.navigate(NudgeNavigationGraph.SETTING_GRAPH)
                }, onBackClick = {
                    navController.navigate(HomeScreens.VILLAGE_SELECTION_SCREEN.route)
                }
            )
        }
    }



}
