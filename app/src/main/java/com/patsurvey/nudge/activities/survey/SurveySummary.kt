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
import com.patsurvey.nudge.activities.ui.vo_endorsement.DidiItemCardForVo
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
            showDidiListForStatus =
                Pair(!showDidiListForStatus.first, PatSurveyStatus.NOT_STARTED.ordinal)
        } else if (showDialog.value) {
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
            val count = if (fromScreen == ARG_FROM_PAT_SURVEY) didids.value.filter { it.patSurveyStatus == PatSurveyStatus.COMPLETED.ordinal }.size else didids.value.filter { it.voEndorsementStatus == DidiEndorsementStatus.ENDORSED.ordinal }.size
            ShowDialog(
                title = "Are you sure?",
                message = if (fromScreen == ARG_FROM_PAT_SURVEY) {
                    if (count > 1)
                        stringResource(R.string.pat_completion_dialog_message_plural).replace("{COUNT}", count.toString())
                    else
                        stringResource(R.string.pat_completion_dialog_message_singular).replace("{COUNT}", count.toString())
                }
                 else {
                     if (count > 1)
                         stringResource(id = R.string.vo_endorsement_completion_dialog_message_plural).replace("{COUNT}", count.toString())
                    else
                         stringResource(id = R.string.vo_endorsement_completion_dialog_message_singular).replace("{COUNT}", count.toString())
                },
                setShowDialog = {
                    showDialog.value = it
                }) {
                surveySummaryViewModel.checkIfLastStepIsComplete(stepId) { isPreviousStepComplete ->
                    if (isPreviousStepComplete) {
                        if ((context as MainActivity).isOnline.value ?: false) {
                            surveySummaryViewModel.updatePatStatusToNetwork(object : NetworkCallbackListener {
                                override fun onSuccess() {

                                }

                                override fun onFailed() {
                                    showCustomToast(context, SYNC_FAILED)
                                }

                            })
                            surveySummaryViewModel.callWorkFlowAPI(
                                surveySummaryViewModel.prefRepo.getSelectedVillage().id,
                                stepId,
                                object :
                                    NetworkCallbackListener {
                                    override fun onSuccess() {
                                    }

                                    override fun onFailed() {
                                        showCustomToast(context, SYNC_FAILED)
                                    }
                                })
                            if (fromScreen == ARG_FROM_PAT_SURVEY) {
                                surveySummaryViewModel.savePATSummeryToServer(object : NetworkCallbackListener {
                                    override fun onSuccess() {

                                    }

                                    override fun onFailed() {
                                        showCustomToast(context, SYNC_FAILED)
                                    }

                                })
                            }
                        }
                        if (fromScreen == ARG_FROM_PAT_SURVEY) {
                            surveySummaryViewModel.updateDidiPatStatus()
                            surveySummaryViewModel.markPatComplete(
                                surveySummaryViewModel.prefRepo.getSelectedVillage().id,
                                stepId
                            )
                            surveySummaryViewModel.savePatCompletionDate()
                            navController.navigate(
                                "pat_step_completion_screen/${
                                    context.getString(R.string.pat_survey_completed_message).replace(
                                        "{VILLAGE_NAME}",
                                        surveySummaryViewModel.prefRepo.getSelectedVillage().name
                                    )
                                }"
                            )
                        } else {
                            navController.navigate("form_picture_screen/$stepId")
                        }
                    } else {
                        showToast(context, "Previous Step Not Complete.")
                    }
                }

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
                        text = stringResource(
                            id = if (showDidiListForStatus.first) {
                                if (fromScreen == ARG_FROM_PAT_SURVEY) {
                                    if (showDidiListForStatus.second == PatSurveyStatus.COMPLETED.ordinal) {
                                        R.string.didi_pat_complete_text
                                    } else {
                                        R.string.didi_not_available_sub_heading_text
                                    }
                                } else {
                                    if (showDidiListForStatus.second == DidiEndorsementStatus.ENDORSED.ordinal) {
                                        R.string.didi_endorsed_text_plural
                                    } else {
                                        R.string.didi_rejected_text_plural
                                    }
                                }
                            } else R.string.summary_text
                        ),
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
                            if (fromScreen == ARG_FROM_PAT_SURVEY) {
                                itemsIndexed(didids.value.filter { it.patSurveyStatus == showDidiListForStatus.second }) { index, didi ->
                                    DidiItemCardForPat(
                                        didi = didi,
                                        modifier = modifier,
                                        onItemClick = {
                                            if (showDidiListForStatus.second == PatSurveyStatus.COMPLETED.ordinal)
                                                navController.navigate("pat_complete_didi_summary_screen/${didi.id}/${ARG_FROM_PAT_SUMMARY_SCREEN}")
                                        }
                                    )
                                }
                            } else {
                                itemsIndexed(didids.value.filter { it.voEndorsementStatus == showDidiListForStatus.second }) { index, didi ->
                                    DidiItemCardForVo(
                                        navController = navController,
                                        didi = didi,
                                        modifier = modifier,
                                        onItemClick = {
                                            navController.navigate("vo_endorsement_summary_screen/${didi.id}/${didi.voEndorsementStatus}")
                                        }
                                    )
                                }
                            }
                            item { Spacer(modifier = Modifier.height(6.dp)) }
                        }
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    SummaryBox(
                        count = if (fromScreen == ARG_FROM_PAT_SURVEY)
                            didids.value.filter { it.patSurveyStatus == PatSurveyStatus.COMPLETED.ordinal }.size
                        else
                            didids.value.filter { it.voEndorsementStatus == DidiEndorsementStatus.ENDORSED.ordinal }.size,
                        boxColor = blueLighter,
                        boxTitle = if (fromScreen == ARG_FROM_PAT_SURVEY) stringResource(R.string.pat_completed_box_title) else stringResource(
                            id = if (didids.value.filter { it.voEndorsementStatus == DidiEndorsementStatus.ENDORSED.ordinal }.size <= 1) R.string.didi_endorsed_text_singula else R.string.didi_endorsed_text_plural
                        ),
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 20.dp)
                    ) {
                        showDidiListForStatus = if (fromScreen == ARG_FROM_PAT_SURVEY) Pair(
                            true,
                            PatSurveyStatus.COMPLETED.ordinal
                        ) else Pair(true, DidiEndorsementStatus.ENDORSED.ordinal)
                    }
                    SummaryBox(
                        count = if (fromScreen == ARG_FROM_PAT_SURVEY)
                            didids.value.filter { it.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal }.size
                        else
                            didids.value.filter { it.voEndorsementStatus == DidiEndorsementStatus.REJECTED.ordinal }.size,
                        boxColor = if (fromScreen == ARG_FROM_PAT_SURVEY) yellowLight else redLight,
                        boxTitle = if (fromScreen == ARG_FROM_PAT_SURVEY) stringResource(id = if (didids.value.filter { it.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal }.size <= 1)
                            R.string.didi_not_available_text_singular else R.string.didi_not_available_text_plural) else stringResource(
                            id = if (didids.value.filter { it.voEndorsementStatus == DidiEndorsementStatus.REJECTED.ordinal }.size <= 1)
                                R.string.didi_rejected_text_singula else R.string.didi_rejected_text_plural
                        ),
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 20.dp)
                    ) {
                        showDidiListForStatus =
                            if (fromScreen == ARG_FROM_PAT_SURVEY)
                                Pair(true, PatSurveyStatus.NOT_AVAILABLE.ordinal)
                            else
                                Pair(true, DidiEndorsementStatus.REJECTED.ordinal)
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
                positiveButtonText =
                if (fromScreen == ARG_FROM_PAT_SURVEY) {
                    if (showDidiListForStatus.first)
                        stringResource(id = R.string.done_text)
                    else
                        stringResource(id = R.string.send_for_vo_text)
                } else {
                    if (showDidiListForStatus.first)
                        stringResource(id = R.string.done_text)
                    else
                        stringResource(id = R.string.confirm_text)
                       },
                isArrowRequired = !showDidiListForStatus.first,
                positiveButtonOnClick = {
                    if (showDidiListForStatus.first) {
                        showDidiListForStatus = if (fromScreen == ARG_FROM_PAT_SURVEY)
                            Pair(false, PatSurveyStatus.NOT_STARTED.ordinal)
                        else
                            Pair(false, DidiEndorsementStatus.NOT_STARTED.ordinal)
                    }else
                        showDialog.value = true
                }
            )
        } else {
            bottomPadding = 0.dp
        }
    }

}