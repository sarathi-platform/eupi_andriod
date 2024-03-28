package com.patsurvey.nudge.activities.ui.home

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.data.prefs.SharedPrefs
import com.nudge.core.PREF_KEY_IS_SETTING_SCREEN_OPEN
import com.nudge.core.ui.navigation.CoreGraph
import com.patsurvey.nudge.activities.ProgressScreen
import com.patsurvey.nudge.activities.settings.presentation.SettingBSScreen
import com.patsurvey.nudge.activities.ui.bpc.progress_screens.BpcProgressScreen
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.navigation.selection.HomeScreens
import com.patsurvey.nudge.utils.PREF_KEY_TYPE_NAME
import com.patsurvey.nudge.utils.UPCM_USER

@Composable
fun HomeUserScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    prefRepo: PrefRepo
) {
    if (prefRepo.isUserBPC()) {
        BpcProgressScreen(
            bpcProgreesScreenViewModel = hiltViewModel(),
            navController = navController,
            modifier = Modifier.fillMaxWidth(),
            onNavigateToStep = { villageId, stepId ->
                navController.navigate("bpc_graph/$villageId/$stepId")
            },
            onNavigateToSetting = {
                navController.navigate(CoreGraph.SETTING_GRAPH)
            },
            onBackClick = {
                navController.navigate(HomeScreens.VILLAGE_SELECTION_SCREEN.route)
            }
        )
    } else {
        ProgressScreen(
            stepsNavHostController = navController,
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
                navController.navigate(CoreGraph.SETTING_GRAPH)
            }, onBackClick = {
                navController.navigate(HomeScreens.VILLAGE_SELECTION_SCREEN.route)
            }
        )
    }


}
