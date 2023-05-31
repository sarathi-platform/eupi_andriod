package com.patsurvey.nudge.activities.survey

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.MainActivity
import com.patsurvey.nudge.activities.ui.socialmapping.ShowDialog
import com.patsurvey.nudge.activities.ui.theme.*
import com.patsurvey.nudge.activities.ui.transect_walk.VillageDetailView
import com.patsurvey.nudge.intefaces.NetworkCallbackListener
import com.patsurvey.nudge.navigation.home.HomeScreens
import com.patsurvey.nudge.navigation.navgraph.Graph
import com.patsurvey.nudge.utils.*

@Composable
fun SurveySummary(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    fromScreen: String,
    surveySummaryViewModel: SurveySummaryViewModel,
    stepId: Int,
    isStepComplete: Boolean
) {

    val didids = surveySummaryViewModel.didiList.collectAsState()
    var showDidiListForStatus by remember {
        mutableStateOf(
            Pair(
                false,
                PatSurveyStatus.NOT_STARTED.ordinal
            )
        )
    }


    val context = LocalContext.current

    val localDensity = LocalDensity.current
    var bottomPadding by remember {
        mutableStateOf(0.dp)
    }

    val showDialog = remember { mutableStateOf(false) }

    BackHandler() {
        if (showDidiListForStatus.first) {
            showDidiListForStatus = Pair(!showDidiListForStatus.first, PatSurveyStatus.NOT_STARTED.ordinal)
        } else if (showDialog.value){
            showDialog.value = !showDialog.value
        } else {
            if (isStepComplete) {
                navController.navigate(Graph.HOME) {
                    popUpTo(HomeScreens.PROGRESS_SCREEN.route) {
                        inclusive = true
                    }
                }
            } else {
                navController.popBackStack()
            }
        }
    }



    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .then(modifier)
    ) {
        val (bottomActionBox, mainBox) = createRefs()

        if (showDialog.value) {
            ShowDialog(
                title = "Are you sure?",
                message = "You are sending ${didids.value.filter { it.patSurveyProgress == PatSurveyStatus.COMPLETED.ordinal }.size} PAT completed Didis for VO Endorsement.",
                setShowDialog = {
                    showDialog.value = it
                }) {
                if ((context as MainActivity).isOnline.value ?: false) {
                    surveySummaryViewModel.savePATSummeryToServer(object : NetworkCallbackListener {
                        override fun onSuccess() {

                        }

                        override fun onFailed() {
                            showCustomToast(context, SYNC_FAILED)
                        }

                    })
                    surveySummaryViewModel.callWorkFlowAPI(surveySummaryViewModel.prefRepo.getSelectedVillage().id, stepId, object :
                        NetworkCallbackListener {
                        override fun onSuccess() {
                        }

                        override fun onFailed() {
                            showCustomToast(context, SYNC_FAILED)
                        }
                    })
                }
                surveySummaryViewModel.markPatComplete(surveySummaryViewModel.prefRepo.getSelectedVillage().id, stepId)
                surveySummaryViewModel.savePatCompletionDate()
                navController.navigate("pat_step_completion_screen/${context.getString(R.string.pat_survey_completed_message).replace(
                    "{VILLAGE_NAME}",
                    surveySummaryViewModel.prefRepo.getSelectedVillage().name)}"
                )
            }
        }

        Box(
            modifier = Modifier
                .constrainAs(mainBox) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                }
                .fillMaxWidth()
                .padding(top = 14.dp)
                .then(modifier),
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 16.dp),
            ) {

                VillageDetailView(
                    villageName = surveySummaryViewModel.prefRepo.getSelectedVillage().name ?: "",
                    voName = (surveySummaryViewModel.prefRepo.getSelectedVillage().federationName)
                        ?: "",
                    modifier = Modifier
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.pat_survey),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxWidth(),
                        style = mediumTextStyle,
                        color = textColorDark,
                        textAlign = TextAlign.Start
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                ) {
                    Text(
                        text = stringResource(id = if (showDidiListForStatus.first){
                            if (showDidiListForStatus.second == PatSurveyStatus.COMPLETED.ordinal){
                                R.string.didi_pat_complete_text
                            } else {
                                R.string.didi_not_available_sub_heading_text
                            }
                        } else R.string.summary_text),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxWidth(),
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            fontFamily = NotoSans
                        ),
                        color = textColorDark80,
                        textAlign = TextAlign.Start
                    )
                }

                AnimatedVisibility(visible = showDidiListForStatus.first) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 8.dp)
                    ) {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            contentPadding = PaddingValues(bottom = bottomPadding),
                            modifier = Modifier.padding(bottom = 10.dp)
                        ) {
                            itemsIndexed(didids.value.filter { it.patSurveyProgress == showDidiListForStatus.second }) { index, didi ->
                                DidiItemCardForPat(didi = didi, modifier = modifier, onItemClick = {
                                    if (showDidiListForStatus.second == PatSurveyStatus.COMPLETED.ordinal)
                                        navController.navigate("pat_complete_didi_summary_screen/${didi.id}/${ARG_FROM_PAT_SUMMARY_SCREEN}")
                                })
                            }
                            item { Spacer(modifier = Modifier.height(6.dp)) }
                        }
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    SummaryBox(
                        count = didids.value.filter { it.patSurveyProgress == PatSurveyStatus.COMPLETED.ordinal }.size,
                        boxColor = blueLighter,
                        boxTitle = stringResource(R.string.pat_completed_box_title), //added changes for vo endorsement text
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 20.dp)
                    ) {
                        showDidiListForStatus = Pair(true, PatSurveyStatus.COMPLETED.ordinal)
                    }
                    SummaryBox(
                        count = didids.value.filter { it.patSurveyProgress == PatSurveyStatus.NOT_AVAILABLE.ordinal }.size,
                        boxColor = if (fromScreen == ARG_FROM_PAT_SURVEY) yellowLight else redLight,
                        boxTitle = stringResource(id = R.string.didi_not_available_text), //added changes for vo endorsement text
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 20.dp)
                    ) {
                        showDidiListForStatus = Pair(true, PatSurveyStatus.NOT_AVAILABLE.ordinal)
                    }

                }
            }
        }

        if (!isStepComplete || showDidiListForStatus.first) {
            BottomButtonBox(
                modifier = Modifier
                    .constrainAs(bottomActionBox) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    }
                    .onGloballyPositioned { coordinates ->
                        bottomPadding = with(localDensity) {
                            coordinates.size.height.toDp()
                        }
                    },
                positiveButtonText = if (showDidiListForStatus.first) stringResource(id = R.string.done_text) else stringResource(
                    id = R.string.send_for_vo_text
                ),
                isArrowRequired = !showDidiListForStatus.first,
                positiveButtonOnClick = {
                    if (showDidiListForStatus.first)
                        showDidiListForStatus = Pair(false, PatSurveyStatus.NOT_STARTED.ordinal)
                    else
                        showDialog.value = true
                }
            )
        } else {
            bottomPadding = 0.dp
        }
    }

}