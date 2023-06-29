package com.patsurvey.nudge.activities.ui.home

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ProgressScreen
import com.patsurvey.nudge.activities.ui.bpc.progress_screens.BpcProgressScreen
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.customviews.CustomProgressBar
import com.patsurvey.nudge.customviews.VOAndVillageBoxView
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.navigation.navgraph.Graph

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun HomeUserScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    prefRepo: PrefRepo
) {

        if(prefRepo.isUserBPC()){
            BpcProgressScreen(
                bpcProgreesScreenViewModel = hiltViewModel(),
                navController = navController,
                modifier = Modifier.fillMaxWidth(),
                onNavigateToStep = { villageId, stepId ->
                    navController.navigate("bpc_graph/$villageId/$stepId")
                },
                onNavigateToSetting = {
                    navController.navigate(Graph.SETTING_GRAPH)
                }
            )
        }else{
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
                    navController.navigate(Graph.SETTING_GRAPH)
                }
            )
        }


}
