package com.patsurvey.nudge.activities.survey

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.blueLighter
import com.patsurvey.nudge.activities.ui.theme.mediumTextStyle
import com.patsurvey.nudge.activities.ui.theme.redLight
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.activities.ui.theme.textColorDark80
import com.patsurvey.nudge.activities.ui.theme.yellowLight
import com.patsurvey.nudge.activities.ui.transect_walk.VillageDetailView
import com.patsurvey.nudge.intefaces.NetworkCallbackListener
import com.patsurvey.nudge.navigation.home.HomeScreens
import com.patsurvey.nudge.navigation.navgraph.Graph
import com.patsurvey.nudge.utils.ARG_FROM_PAT_SUMMARY_SCREEN
import com.patsurvey.nudge.utils.ARG_FROM_PAT_SURVEY
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.BottomButtonBox
import com.patsurvey.nudge.utils.DidiEndorsementStatus
import com.patsurvey.nudge.utils.DidiItemCardForPat
import com.patsurvey.nudge.utils.DidiItemCardForVoForSummary
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.PREF_NEED_TO_POST_BPC_MATCH_SCORE_FOR_
import com.patsurvey.nudge.utils.PatSurveyStatus
import com.patsurvey.nudge.utils.SYNC_FAILED
import com.patsurvey.nudge.utils.SummaryBox
import com.patsurvey.nudge.utils.WealthRank
import com.patsurvey.nudge.utils.showCustomToast
import com.patsurvey.nudge.utils.showToast

@SuppressLint("StringFormatMatches", "StateFlowValueCalledInComposition")
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

    LaunchedEffect(key1 = true){
            showDidiListForStatus =
                  Pair((context as MainActivity).isBackFromSummary.value, surveySummaryViewModel.baseSummarySecond.value)


    }
    BackHandler() {
        (context as MainActivity).isBackFromSummary.value = false
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
                        saveState = false
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
            if(fromScreen == ARG_FROM_PAT_SURVEY){
                surveySummaryViewModel.prepareDidiCountList(context)
            }
            val count = if (fromScreen == ARG_FROM_PAT_SURVEY) didids.value.filter { it.patSurveyStatus == PatSurveyStatus.COMPLETED.ordinal ||
                    it.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal || it.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal }.size
            else didids.value.filter { it.voEndorsementStatus == DidiEndorsementStatus.ENDORSED.ordinal || it.voEndorsementStatus == DidiEndorsementStatus.REJECTED.ordinal }.size
            ShowDialog(
                title = stringResource(id = R.string.are_you_sure),
                isBulletShow = fromScreen == ARG_FROM_PAT_SURVEY,
                list = if(fromScreen == ARG_FROM_PAT_SURVEY) surveySummaryViewModel.didiCountList.value else emptyList(),
                message = if(surveySummaryViewModel.prefRepo.isUserBPC()){
                    stringResource(id = R.string.bpc_final_pat_submition_message)
                }else{
                    if (fromScreen == ARG_FROM_PAT_SURVEY) {
                        if (count > 1){
                            context.getString(R.string.pat_completed_for_didi_plural,surveySummaryViewModel.totalPatDidiCount.value,surveySummaryViewModel.notAvailableDidiCount.value,surveySummaryViewModel.voEndorseDidiCount.value)
//                            stringResource(R.string.pat_completed_for_didi_plural).replace("{COUNT}", count.toString())
                        }else{
                            context.getString(R.string.pat_completed_for_didi_singular,surveySummaryViewModel.totalPatDidiCount.value,surveySummaryViewModel.notAvailableDidiCount.value,surveySummaryViewModel.voEndorseDidiCount.value)
//                            stringResource(R.string.pat_completion_dialog_message_plural).replace("{COUNT}", count.toString())
                        }

                    }
                    else {
                        if (count > 1)
                            stringResource(id = R.string.vo_endorsement_completion_dialog_message_plural).replace("{COUNT}", count.toString())
                        else
                            stringResource(id = R.string.vo_endorsement_completion_dialog_message_singular).replace("{COUNT}", count.toString())
                    }
                }
                   ,
                setShowDialog = {
                    showDialog.value = it
                }) {
                if(surveySummaryViewModel.prefRepo.isUserBPC()){
                    if ((context as MainActivity).isOnline.value ?: false) {
                        surveySummaryViewModel.sendBpcUpdatedDidiList(object :
                            NetworkCallbackListener {
                            override fun onSuccess() {

                            }

                            override fun onFailed() {

                            }

                        })
                        surveySummaryViewModel.savePATSummeryToServer(object :
                            NetworkCallbackListener {
                            override fun onSuccess() {

                            }

                            override fun onFailed() {
                                showCustomToast(context, SYNC_FAILED)
                            }

                        })

                        surveySummaryViewModel.updateBpcPatStatusToNetwork(object :
                            NetworkCallbackListener {
                            override fun onSuccess() {

                            }

                            override fun onFailed() {
                                showCustomToast(context, SYNC_FAILED)
                            }

                        })
                        surveySummaryViewModel.callWorkFlowAPIForBpc(
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
                        surveySummaryViewModel.sendBpcMatchScore(object :
                            NetworkCallbackListener {
                            override fun onSuccess() {
                            }

                            override fun onFailed() {
                                showCustomToast(context, SYNC_FAILED)
                            }
                        })
                    } else {
                        surveySummaryViewModel.prefRepo.savePref(PREF_NEED_TO_POST_BPC_MATCH_SCORE_FOR_ + surveySummaryViewModel.prefRepo.getSelectedVillage().id, false)
                    }
                    surveySummaryViewModel.updateDidiPatStatus()
                    surveySummaryViewModel.markBpcVerificationComplete(surveySummaryViewModel.prefRepo.getSelectedVillage().id, stepId)
                    surveySummaryViewModel.saveBpcPatCompletionDate()
                    navController.navigate(
                        "bpc_pat_step_completion_screen/${
                            context.getString(R.string.pat_survey_completed_message).replace("{VILLAGE_NAME}", surveySummaryViewModel.villageEntity.value?.name ?: BLANK_STRING)
                        }"
                    )
                } else {
                    surveySummaryViewModel.checkIfLastStepIsComplete(stepId) { isPreviousStepComplete ->
                        if (isPreviousStepComplete) {
                            if(fromScreen == ARG_FROM_PAT_SURVEY){
                                surveySummaryViewModel.updateDidiPatStatus()
                                surveySummaryViewModel.markPatComplete(
                                    surveySummaryViewModel.prefRepo.getSelectedVillage().id,
                                    stepId
                                )
                                surveySummaryViewModel.savePatCompletionDate()
                            }
                            if ((context as MainActivity).isOnline.value ?: false) {
                                if (surveySummaryViewModel.isTolaSynced.value == 2
                                    && surveySummaryViewModel.isDidiSynced.value == 2
                                    && surveySummaryViewModel.isDidiRankingSynced.value == 2
                                ) {
                                    if (fromScreen == ARG_FROM_PAT_SURVEY) {
                                        surveySummaryViewModel.savePATSummeryToServer(object :
                                            NetworkCallbackListener {
                                            override fun onSuccess() {
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
                                            }
                                            override fun onFailed() {
                                                showCustomToast(context, SYNC_FAILED)
                                            }
                                        })
                                    }
                                }
                            }
                            if (fromScreen == ARG_FROM_PAT_SURVEY) {

                                navController.navigate(
                                    "pat_step_completion_screen/${
                                        context.getString(R.string.pat_survey_completed_message)
                                            .replace(
                                                "{VILLAGE_NAME}",
                                                surveySummaryViewModel.villageEntity.value?.name ?: BLANK_STRING
                                            )
                                    }"
                                )
                            } else {
                                NudgeLogger.d("SurveySummary", "navigate to form_picture_screen")
                                navController.navigate("form_picture_screen/$stepId")
                            }
                        } else {
                            showToast(context, "Previous Step Not Complete.")
                        }
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

                Column( modifier = Modifier
                    .padding(horizontal = 4.dp)){
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                    ) {
                        Text(
                            text = stringResource(id = if (fromScreen == ARG_FROM_PAT_SURVEY) R.string.pat_survey else R.string.vo_endorsement),
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
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(bottom = bottomPadding),
                                modifier = Modifier.padding(bottom = 10.dp)
                            ) {
                                item {
                                    Spacer(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                    )
                                }
                                if (surveySummaryViewModel.prefRepo.isUserBPC()) {
                                    itemsIndexed(
                                        if (showDidiListForStatus.second == PatSurveyStatus.NOT_AVAILABLE.ordinal)
                                            didids.value.filter { it.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal }
                                        else
                                            didids.value.filter { it.patSurveyStatus == PatSurveyStatus.COMPLETED.ordinal }
                                    ) { index, didi ->
                                        DidiItemCardForPat(
                                            didi = didi,
                                            modifier = modifier,
                                            onItemClick = {
                                                navController.navigate("bpc_pat_complete_didi_summary_screen/${didi.id}/${ARG_FROM_PAT_SUMMARY_SCREEN}")
                                            }
                                        )
                                    }
                                } else {
                                    if (fromScreen == ARG_FROM_PAT_SURVEY) {
                                        itemsIndexed(if (showDidiListForStatus.second == PatSurveyStatus.NOT_AVAILABLE.ordinal)
                                            didids.value.filter { it.wealth_ranking == WealthRank.POOR.rank && (it.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal || it.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal) }
                                        else
                                            didids.value.filter { it.patSurveyStatus == showDidiListForStatus.second && it.wealth_ranking == WealthRank.POOR.rank }) { index, didi ->
                                            DidiItemCardForPat(
                                                didi = didi,
                                                modifier = modifier,
                                                onItemClick = {
                                                    if (showDidiListForStatus.second == PatSurveyStatus.COMPLETED.ordinal) {
                                                        navController.navigate("pat_complete_didi_summary_screen/${didi.id}/${ARG_FROM_PAT_SUMMARY_SCREEN}")
                                                    }
                                                }
                                            )
                                        }
                                    } else {
                                        if (showDidiListForStatus.second == DidiEndorsementStatus.ENDORSED.ordinal) {
                                            itemsIndexed(didids.value.filter {
                                                it.forVoEndorsement == 1 && it.section2Status == PatSurveyStatus.COMPLETED.ordinal
                                                        && it.voEndorsementStatus == DidiEndorsementStatus.ENDORSED.ordinal
                                            }) { index, didi ->
                                                DidiItemCardForVoForSummary(
                                                    navController = navController,
                                                    didi = didi,
                                                    modifier = modifier,
                                                    onItemClick = {
                                                        navController.navigate("vo_endorsement_summary_screen/${didi.id}/${didi.voEndorsementStatus}")
                                                    }
                                                )
                                            }
                                        } else {
                                            itemsIndexed(didids.value.filter { it.voEndorsementStatus == showDidiListForStatus.second }) { index, didi ->
                                                DidiItemCardForVoForSummary(
                                                    navController = navController,
                                                    didi = didi,
                                                    modifier = modifier,
                                                    onItemClick = {
                                                        navController.navigate("vo_endorsement_summary_screen/${didi.id}/${didi.voEndorsementStatus}")
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                                item {
                                    Spacer(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                    )
                                }
                            }
                        }
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        SummaryBox(
                            count = if (fromScreen == ARG_FROM_PAT_SURVEY)
                                didids.value.filter { it.wealth_ranking == WealthRank.POOR.rank && it.patSurveyStatus == PatSurveyStatus.COMPLETED.ordinal }.size
                            else
                                didids.value.filter {
                                    it.forVoEndorsement == 1 && it.section2Status == PatSurveyStatus.COMPLETED.ordinal
                                            && it.voEndorsementStatus == DidiEndorsementStatus.ENDORSED.ordinal
                                }.size,
                            boxColor = blueLighter,
                            boxTitle = if (fromScreen == ARG_FROM_PAT_SURVEY) stringResource(R.string.pat_completed_box_title) else stringResource(
                                id = if (didids.value.filter { it.voEndorsementStatus == DidiEndorsementStatus.ENDORSED.ordinal }.size <= 1) R.string.didi_endorsed_text_singula else R.string.didi_endorsed_text_plural
                            ),
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 20.dp)
                        ) {
                            if (fromScreen == ARG_FROM_PAT_SURVEY) {
                                showDidiListForStatus = Pair(
                                    true,
                                    PatSurveyStatus.COMPLETED.ordinal
                                )
                                surveySummaryViewModel.baseSummarySecond.value =
                                    PatSurveyStatus.COMPLETED.ordinal

                            } else showDidiListForStatus =
                                Pair(true, DidiEndorsementStatus.ENDORSED.ordinal)

                        }
                        SummaryBox(
                            count = if (fromScreen == ARG_FROM_PAT_SURVEY) didids.value.filter { it.wealth_ranking == WealthRank.POOR.rank && (it.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal || it.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal) }.size
                            else
                                didids.value.filter { it.voEndorsementStatus == DidiEndorsementStatus.REJECTED.ordinal }.size,
                            boxColor = if (fromScreen == ARG_FROM_PAT_SURVEY) yellowLight else redLight,
                            boxTitle = if (fromScreen == ARG_FROM_PAT_SURVEY) stringResource(
                                id = if (surveySummaryViewModel.notAvailableCount.value <= 1)
                                    R.string.didi_not_available_text_singular else R.string.didi_not_available_text_plural
                            ) else stringResource(
                                id = if (didids.value.filter { it.voEndorsementStatus == DidiEndorsementStatus.REJECTED.ordinal }.size <= 1)
                                    R.string.didi_rejected_text_singula else R.string.didi_rejected_text_plural
                            ),
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 20.dp)
                        ) {
                            if (fromScreen == ARG_FROM_PAT_SURVEY) {
                                showDidiListForStatus =
                                    Pair(true, PatSurveyStatus.NOT_AVAILABLE.ordinal)
                                surveySummaryViewModel.baseSummarySecond.value =
                                    PatSurveyStatus.NOT_AVAILABLE.ordinal
                            } else showDidiListForStatus =
                                Pair(true, DidiEndorsementStatus.REJECTED.ordinal)

                        }
                    }
                }
            }
        }

        if(surveySummaryViewModel.prefRepo.isUserBPC()){
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
                    positiveButtonText = if (showDidiListForStatus.first)
                        stringResource(id = R.string.done_text)
                    else
                        stringResource(id = R.string.submit_pat_verification),
                    isArrowRequired = !showDidiListForStatus.first,
                    positiveButtonOnClick = {
                        if (showDidiListForStatus.first) {
                            showDidiListForStatus = Pair(false, PatSurveyStatus.NOT_STARTED.ordinal)
                        } else {
                            showDialog.value = true
                        }
                    }
                )
            }
        }else{
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

}