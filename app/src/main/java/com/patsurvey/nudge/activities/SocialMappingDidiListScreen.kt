package com.patsurvey.nudge.activities

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateInt
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.activities.ui.theme.borderGreyLight
import com.patsurvey.nudge.activities.ui.theme.didiDetailItemStyle
import com.patsurvey.nudge.activities.ui.theme.didiDetailLabelStyle
import com.patsurvey.nudge.activities.ui.theme.greenOnline
import com.patsurvey.nudge.activities.ui.theme.inprogressYellow
import com.patsurvey.nudge.activities.ui.theme.languageItemActiveBg
import com.patsurvey.nudge.activities.ui.theme.quesOptionTextStyle
import com.patsurvey.nudge.activities.ui.theme.smallTextStyle
import com.patsurvey.nudge.activities.ui.theme.smallTextStyleMediumWeight
import com.patsurvey.nudge.activities.ui.theme.textColorBlueLight
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.activities.ui.theme.white
import com.patsurvey.nudge.activities.ui.theme.yellowBg
import com.patsurvey.nudge.customviews.CardArrow
import com.patsurvey.nudge.customviews.ModuleAddedSuccessView
import com.patsurvey.nudge.customviews.SearchWithFilterView
import com.patsurvey.nudge.customviews.VOAndVillageBoxView
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.intefaces.NetworkCallbackListener
import com.patsurvey.nudge.utils.ADD_DIDI_BLANK_ID
import com.patsurvey.nudge.utils.ARG_FROM_HOME
import com.patsurvey.nudge.utils.ARG_FROM_PAT_DIDI_LIST_SCREEN
import com.patsurvey.nudge.utils.ARG_FROM_PAT_SURVEY
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.ButtonNegativeForPAT
import com.patsurvey.nudge.utils.ButtonOutline
import com.patsurvey.nudge.utils.ButtonPositiveForPAT
import com.patsurvey.nudge.utils.DidiEndorsementStatus
import com.patsurvey.nudge.utils.DidiItemCardForPat
import com.patsurvey.nudge.utils.DidiStatus
import com.patsurvey.nudge.utils.DoubleButtonBox
import com.patsurvey.nudge.utils.EXPANSTION_TRANSITION_DURATION
import com.patsurvey.nudge.utils.ExclusionType
import com.patsurvey.nudge.utils.PAT_SURVEY
import com.patsurvey.nudge.utils.PageFrom
import com.patsurvey.nudge.utils.PatSurveyStatus
import com.patsurvey.nudge.utils.ShowDidisFromTola
import com.patsurvey.nudge.utils.StepStatus
import com.patsurvey.nudge.utils.SummaryNavigation
import com.patsurvey.nudge.utils.TYPE_EXCLUSION
import com.patsurvey.nudge.utils.TYPE_INCLUSION
import com.patsurvey.nudge.utils.WealthRank
import com.patsurvey.nudge.utils.rememberForeverLazyListState
import com.patsurvey.nudge.utils.showCustomToast
import com.patsurvey.nudge.utils.showDidiImageDialog
import com.patsurvey.nudge.utils.showToast
import com.patsurvey.nudge.utils.visible
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun SocialMappingDidiListScreen(
    navController: NavHostController,
    modifier: Modifier,
    didiViewModel: AddDidiViewModel,
    villageId: Int,
    stepId: Int
) {
    val didiList = didiViewModel.didiList
    val newFilteredDidiList = didiViewModel.filterDidiList
    val newFilteredTolaDidiList = didiViewModel.filterTolaMapList
    val localDensity = LocalDensity.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var expendedDidiIndex by remember {
        mutableStateOf(-1)
    }
    val showDialog = remember { mutableStateOf(false) }
    var bottomPadding by remember {
        mutableStateOf(0.dp)
    }

    val expandedIds = remember {
        mutableStateListOf<Int>()
    }
    var filterSelected by remember {
        mutableStateOf(
                (context as MainActivity).isFilterApplied.value
        )
    }



    val showLoader = remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = true) {
        didiViewModel.isSocialMappingComplete(stepId)
        if(filterSelected){
            didiViewModel.getValidDidisFromDB(didiViewModel.isComingPatScreen())
            didiViewModel.filterList()
        }
    }

    var completeTolaAdditionClicked by remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp

    val focusManager = LocalFocusManager.current
    BackHandler() {
        if (completeTolaAdditionClicked)
            completeTolaAdditionClicked = false
        else {
            if (!didiViewModel.getFromPage().equals(ARG_FROM_PAT_SURVEY, true)) {
                (context as MainActivity).isFilterApplied.value = false
            }
            navController.popBackStack()
        }
    }

    if(didiViewModel.showDidiImageDialog.value){
        didiViewModel.dialogDidiEntity.value?.let {
            showDidiImageDialog(didi = it){
                didiViewModel.showDidiImageDialog.value = false
            }
        }
    }


    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .pointerInput(true) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
            .then(modifier)
    ) {
        val (bottomActionBox, mainBox) = createRefs()
        Box(modifier = Modifier
            .constrainAs(mainBox) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
            }
            .padding(top = 14.dp)
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                VOAndVillageBoxView(
                    prefRepo = didiViewModel.addDidiRepository.prefRepo,
                    modifier = Modifier.fillMaxWidth(),
                )

                val count = didiList.value.filter { it.activeStatus == DidiStatus.DIDI_ACTIVE.ordinal }.size
                if (showLoader.value) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(top = 30.dp)
                    ) {
                        CircularProgressIndicator(
                            color = blueDark,
                            modifier = Modifier
                                .size(28.dp)
                                .align(Alignment.Center)
                        )
                    }
                }
                else {
                    ModuleAddedSuccessView(
                        completeAdditionClicked = completeTolaAdditionClicked,
                        message = stringResource(
                            if (count < 2) R.string.didi_conirmation_text_singular else R.string.didi_conirmation_text_plural,
                            count
                        ),
                        modifier = Modifier.padding(vertical = (screenHeight / 4).dp)
                    )
                    var listState = rememberLazyListState()

                    if (didiViewModel.getFromPage()
                            .equals(ARG_FROM_PAT_SURVEY, true)
                    ) {
                        listState = rememberForeverLazyListState(key = PAT_SURVEY)
                    }
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = white)
                            .weight(1f)
                            .padding(
                                bottom =
                                if (!didiViewModel.addDidiRepository.prefRepo
                                        .getFromPage()
                                        .equals(ARG_FROM_HOME, true)
                                ) {
                                    0.dp
                                } else {
                                    50.dp
                                }
                            ),
                        contentPadding = PaddingValues(bottom = 10.dp, start = 20.dp, end = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        state = listState
                    ) {
                        item {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceAround,
                                modifier = Modifier
                            ) {
                                val title = if (didiViewModel.getFromPage()
                                        .equals(ARG_FROM_PAT_SURVEY, true)
                                )
                                    stringResource(R.string.pat_survey_title)
                                else if (!didiViewModel.getFromPage()
                                        .equals(ARG_FROM_HOME, true)
                                )
                                    stringResource(R.string.social_mapping)
                                else
                                    stringResource(R.string.didis_item_text_plural)
                                MainTitle(title, Modifier.weight(1f))
                                Spacer(modifier = Modifier.padding(14.dp))
                                if (!didiViewModel.getFromPage()
                                        .equals(ARG_FROM_HOME, true)
                                    && !didiViewModel.getFromPage()
                                        .equals(ARG_FROM_PAT_SURVEY, true)
                                ) {
//                                    if (!didiViewModel.isVoEndorsementComplete.value) {
                                    ButtonOutline(
                                        modifier = Modifier
                                            .weight(0.9f)
                                            .height(45.dp),
                                        buttonTitle = stringResource(id = R.string.add_didi),
                                        ) {
                                            didiViewModel.resetAllFields()
                                            navController.navigate("add_didi_graph/$ADD_DIDI_BLANK_ID") {
                                                launchSingleTop = true
                                            }
                                        }
                                        /*BlueButtonWithIconWithFixedWidth(
                                            modifier = Modifier
                                                .weight(0.5f),
                                            buttonText = stringResource(id = R.string.add_didi),
                                            icon = Icons.Default.Add
                                        ) {
                                            didiViewModel.resetAllFields()
                                            navController.navigate("add_didi_graph/$ADD_DIDI_BLANK_ID") {
                                                launchSingleTop = true
                                            }
                                        }*/
//                                    }
                                }
                            }
                        }
                        item {
                            SearchWithFilterView(placeholderString = stringResource(id = R.string.search_didis),
                                modifier = Modifier,
                                filterSelected = filterSelected,
                                onFilterSelected = {
                                    if (didiList.value.isNotEmpty()) {
                                        filterSelected = !it
                                            (context as MainActivity).isFilterApplied.value = !it
                                        didiViewModel.filterList()
                                    }
                                }, onSearchValueChange = {
                                    didiViewModel.performQuery(it, filterSelected)

                                })
                        }

                        item {
                            AnimatedVisibility(
                                visible = !filterSelected,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(
                                            style = SpanStyle(
                                                color = greenOnline,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                fontFamily = NotoSans
                                            )
                                        ) {
                                            append(
                                                if (!didiViewModel.getFromPage()
                                                        .equals(ARG_FROM_PAT_SURVEY, true)
                                                )
                                                    "${newFilteredDidiList.size}"
                                                else
                                                    "${didiViewModel.pendingDidiCount.value}"
                                            )
                                        }
                                        append(
                                            " ${
                                                if (!didiViewModel.getFromPage()
                                                        .equals(ARG_FROM_PAT_SURVEY, true)
                                                )
                                                    pluralStringResource(
                                                        id = R.plurals.didis_added,
                                                        newFilteredDidiList.size
                                                    )
                                                else {
                                                    pluralStringResource(
                                                        id = R.plurals.poor_didis_pending_text,
                                                        count = didiViewModel.pendingDidiCount.value
                                                    )
                                                }
                                            }"
                                        )
                                    },
                                    style = TextStyle(
                                        color = textColorDark,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        fontFamily = NotoSans
                                    ),
                                    modifier = Modifier
                                        .align(Alignment.Start)
                                )
                            }
                        }
                        if (filterSelected) {
                            itemsIndexed(
                                newFilteredTolaDidiList.keys.toList()
                            ) { index, didiKey ->
                                ShowDidisFromTola(navController = navController,
                                    prefRepo = didiViewModel.addDidiRepository.prefRepo,
                                    addDidiViewModel = didiViewModel,
                                    didiTola = didiKey,
                                    answerDao = didiViewModel.addDidiRepository.answerDao,
                                    questionListDao = didiViewModel.addDidiRepository.questionListDao,
                                    didiList = if (didiViewModel.getFromPage()
                                            .equals(ARG_FROM_PAT_SURVEY, true)
                                    )
                                        newFilteredTolaDidiList[didiKey]?.filter { it.wealth_ranking == WealthRank.POOR.rank }
                                            ?: emptyList()
                                    else newFilteredTolaDidiList[didiKey] ?: emptyList(),
                                    modifier = modifier,
                                    expandedIds = expandedIds,
                                    onExpendClick = { _, didiDetailModel ->
                                        if (expandedIds.contains(didiDetailModel.id)) {
                                            expandedIds.remove(didiDetailModel.id)
                                        } else {
                                            expandedIds.add(didiDetailModel.id)
                                        }
                                    },
                                    onNavigate = {
                                        if (!didiViewModel.getFromPage().equals(
                                                ARG_FROM_PAT_SURVEY,
                                                true
                                            ) && !didiViewModel.isSocialMappingComplete.value
                                        ) {
                                            navController.navigate("add_didi_graph/${it.id}") {
                                                launchSingleTop = true
                                            }
                                        } else if (didiViewModel.getFromPage().equals(
                                                ARG_FROM_HOME, true
                                            )
                                        ) {
                                            navController.navigate("add_didi_graph/${it.id}") {
                                                launchSingleTop = true
                                            }
                                        }
                                    },
                                    onDeleteClicked = { didi ->
                                        didiViewModel.deleteDidiOffline(
                                            didi,
                                            isFilterSelected = true,
                                            isOnline = (context as MainActivity).isOnline.value
                                                ?: false,
                                            networkCallbackListener = object : NetworkCallbackListener {
                                                override fun onSuccess() {
                                                    showCustomToast(
                                                        context,
                                                        "Didi Deleted Successfully"
                                                    )
                                                }

                                                override fun onFailed() {
                                                    TODO("Not yet implemented")
                                                }

                                            }
                                            )
                                    },
                                    onCircularImageClick = { didiEntity ->
                                        didiViewModel.dialogDidiEntity.value = didiEntity
                                        didiViewModel.showDidiImageDialog.value = true
                                    }
                                )

                                if (index < newFilteredTolaDidiList.keys.size - 1) {
                                    Divider(
                                        color = borderGreyLight,
                                        thickness = 1.dp,
                                        modifier = Modifier.padding(
                                            top = 22.dp,
                                            bottom = 1.dp
                                        )
                                    )
                                }
                            }
                            if (!didiViewModel.isSocialMappingComplete.value)
                                item { Spacer(modifier = Modifier.height(bottomPadding)) }
                        } else {
                            itemsIndexed(
                                if (didiViewModel.getFromPage()
                                        .equals(ARG_FROM_PAT_SURVEY, true)
                                ) newFilteredDidiList.filter { it.wealth_ranking == WealthRank.POOR.rank } else newFilteredDidiList) { index, didi ->
                                if (didiViewModel.getFromPage()
                                        .equals(ARG_FROM_PAT_SURVEY, true)
                                ) {
                                    DidiItemCardForPat(
                                        navController = navController,
                                        prefRepo = didiViewModel.addDidiRepository.prefRepo,
                                        didi = didi,
                                        answerDao = didiViewModel.addDidiRepository.answerDao,
                                        questionListDao = didiViewModel.addDidiRepository.questionListDao,
                                        expanded = expandedIds.contains(didi.id),
                                        modifier = modifier,
                                        onExpendClick = { _, _ -> },
                                        onNotAvailableClick = { didiEntity ->
                                            didiViewModel.setDidiAsUnavailable(didiEntity.id)
                                        },
                                        onItemClick = {}
                                        ,onCircularImageClick = { didi->
                                            didiViewModel.dialogDidiEntity.value =didi
                                            didiViewModel.showDidiImageDialog.value =true
                                        }
                                    )
                                } else {
                                    DidiItemCard(navController,
                                        didiViewModel,
                                        didi,
                                        expandedIds.contains(didi.id),
                                        modifier,
                                        onExpendClick = { _, didiDetailModel ->
                                            if (expandedIds.contains(didiDetailModel.id)) {
                                                expandedIds.remove(didiDetailModel.id)
                                            } else {
                                                expendedDidiIndex = index
                                                expandedIds.add(didiDetailModel.id)
                                                coroutineScope.launch {
                                                    delay(EXPANSTION_TRANSITION_DURATION.toLong() - 100)
                                                    listState.animateScrollToItem(index + 3)
                                                }
                                            }
                                        },
                                        onItemClick = { didi ->
                                            if (!didiViewModel.getFromPage().equals(
                                                    ARG_FROM_PAT_SURVEY,
                                                    true
                                                ) && !didiViewModel.isSocialMappingComplete.value
                                            ) {
                                                navController.navigate("add_didi_graph/${didi.id}") {
                                                    launchSingleTop = true
                                                }
                                            } else if (didiViewModel.getFromPage().equals(
                                                    ARG_FROM_HOME, true
                                                )
                                            ) {
                                                navController.navigate("add_didi_graph/${didi.id}") {
                                                    launchSingleTop = true
                                                }
                                            }
                                        },
                                        onDeleteClicked = { didi ->
                                            didiViewModel.deleteDidiOffline(
                                                didi,
                                                isOnline = (context as MainActivity).isOnline.value
                                                    ?: false,
                                                networkCallbackListener = object : NetworkCallbackListener {
                                                    override fun onSuccess() {
                                                        showCustomToast(
                                                            context,
                                                            "Didi Deleted Successfully"
                                                        )
                                                    }

                                                    override fun onFailed() {
                                                        TODO("Not yet implemented")
                                                    }

                                                })
                                        },
                                        onCircularImageClick = { didi->
                                            didiViewModel.dialogDidiEntity.value =didi
                                            didiViewModel.showDidiImageDialog.value =true
                                        }
                                    )
                                }
                            }
                            if (!didiViewModel.isSocialMappingComplete.value)
                                item { Spacer(modifier = Modifier.height(bottomPadding)) }
                        }
                    }
                }
            }
        }

       // Didi Add\Edit and Wealth Ranking
        if (didiList.value.isNotEmpty() && !didiViewModel.isSocialMappingComplete.value) {
            if (!didiViewModel.getFromPage().equals(ARG_FROM_HOME, true)
                && !didiViewModel.getFromPage().equals(ARG_FROM_PAT_SURVEY, true)
            ) {
                DoubleButtonBox(
                    modifier = Modifier
                        .shadow(10.dp)
                        .constrainAs(bottomActionBox) {
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                        }
                        .onGloballyPositioned { coordinates ->
                            bottomPadding = with(localDensity) {
                                coordinates.size.height.toDp()
                            }
                        },
                    negativeButtonRequired = false,
                    positiveButtonText = if (completeTolaAdditionClicked) stringResource(id = R.string.complete_social_walk_text) else stringResource(
                        id = R.string.complete_didi_addition
                    ),
                    positiveButtonOnClick = {
                        if (completeTolaAdditionClicked) {
                            didiViewModel.checkIfLastStepIsComplete(stepId) { isPreviousStepComplete ->
                                if (isPreviousStepComplete) {
                                    didiViewModel.saveSocialMappingCompletionDate()
                                    //TODO Integrate Api when backend fixes the response.
                                    if ((context as MainActivity).isOnline.value ?: false) {
                                        if (didiViewModel.isTolaSynced.value == 2) {
                                            didiViewModel.addDidisToNetwork(object : NetworkCallbackListener {
                                                override fun onSuccess() {
                                                    didiViewModel.callWorkFlowAPI(
                                                        villageId,
                                                        stepId,
                                                        object : NetworkCallbackListener {
                                                            override fun onSuccess() {
                                                            }

                                                            override fun onFailed() {
//                                                                showCustomToast(context, SYNC_FAILED)
                                                            }
                                                        })
                                                }
                                                override fun onFailed() {

                                                }

                                            })

                                        }
                                    }
                                    didiViewModel.markSocialMappingComplete(villageId, stepId)
                                    didiViewModel.saveWorkflowEventIntoDb(
                                        stepStatus = StepStatus.COMPLETED,
                                        villageId = villageId,
                                        stepId = stepId
                                    )
                                    (context as MainActivity).isFilterApplied.value = false
                                    navController.navigate(
                                        "sm_step_completion_screen/${
                                            context.getString(R.string.social_mapping_completed_message)
                                                .replace(
                                                    "{VILLAGE_NAME}",
                                                    didiViewModel.villageEntity.value?.name ?: ""
                                                )
                                        }"
                                    )
                                } else {
                                    showToast(context, context.getString(R.string.previous_step_not_complete_messgae_text))
                                }
                            }
                        } else {
                            completeTolaAdditionClicked = true
                        }
                    },
                    negativeButtonOnClick = {}
                )
            }

        }

        // Didi PAT Survey

        if (didiList.value.isNotEmpty() && didiViewModel.pendingDidiCount.value == 0) {
            if (didiViewModel.getFromPage()
                    .equals(ARG_FROM_PAT_SURVEY, true) && didiViewModel.pendingDidiCount.value == 0
            ) {
                DoubleButtonBox(
                    modifier = Modifier
                        .shadow(10.dp)
                        .constrainAs(bottomActionBox) {
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                        }
                        .onGloballyPositioned { coordinates ->
                            bottomPadding = with(localDensity) {
                                coordinates.size.height.toDp()
                            }
                        },
                    negativeButtonRequired = false,
                    positiveButtonText = stringResource(
                        id = R.string.complete
                    ),
                    positiveButtonOnClick = {
                        didiViewModel.getPatStepStatus(stepId) {
                            Log.d("SocialMappingDidiListScreen", "getPatStepStatus -> $it")
                            navController.navigate("pat_survey_summary/$stepId/$it")
                        }
                    },
                    negativeButtonOnClick = {}
                )
            }

        }
    }
}



private fun decoupledConstraints(): ConstraintSet {
    return ConstraintSet {
        val didiImage = createRefFor("didiImage")
        val didiName = createRefFor("didiName")
        val didiRow = createRefFor("didiRow")
        val homeImage = createRefFor("homeImage")
        val houseNumber_1 = createRefFor("houseNumber_1")
        val village = createRefFor("village")
        val expendArrowImage = createRefFor("expendArrowImage")
        val expendArrowImageEnd = createRefFor("expendArrowImageEnd")
        val moreActionIcon = createRefFor("moreActionIcon")
        val moreDropDown = createRefFor("moreDropDown")
        val didiDetailLayout = createRefFor("didiDetailLayout")
        val latestStatusLabelCollapsed = createRefFor("latestStatusLabelCollapsed")
        val latestStatusCollapsed = createRefFor("latestStatusCollapsed")




        constrain(didiImage) {
            top.linkTo(parent.top, margin = 12.dp)
            start.linkTo(parent.start, margin = 10.dp)
        }
        constrain(didiName) {
            start.linkTo(didiImage.end, 10.dp)
            top.linkTo(parent.top, 10.dp)
            end.linkTo(moreActionIcon.start, margin = 10.dp)
            width = Dimension.fillToConstraints
        }
        constrain(didiRow) {
            start.linkTo(didiImage.end, 6.dp)
            top.linkTo(parent.top, 10.dp)
            end.linkTo(moreActionIcon.start, margin = 10.dp)
            width = Dimension.fillToConstraints
        }
        constrain(village) {
            start.linkTo(homeImage.end, margin = 10.dp)
            top.linkTo(didiName.bottom)
            end.linkTo(moreActionIcon.start, margin = 10.dp)
            width = Dimension.fillToConstraints
        }
        constrain(homeImage) {
            start.linkTo(didiImage.end, margin = 10.dp)
            top.linkTo(didiName.bottom)

        }
        constrain(houseNumber_1) {
            start.linkTo(didiImage.end, margin = 10.dp)
            top.linkTo(homeImage.bottom)
        }
        constrain(expendArrowImage) {
            top.linkTo(didiName.top)
           // bottom.linkTo(village.bottom)
            end.linkTo(moreActionIcon.start)
        }

        constrain(expendArrowImageEnd) {
            top.linkTo(didiName.top)
           // bottom.linkTo(village.bottom)
            end.linkTo(parent.end, margin = 10.dp)
        }

        constrain(moreActionIcon) {
            top.linkTo(didiName.top)
//            bottom.linkTo(village.bottom)
            end.linkTo(parent.end, margin = 10.dp)
        }

        constrain(moreDropDown) {
            top.linkTo(moreActionIcon.bottom)
            end.linkTo(moreActionIcon.end)
        }

        constrain(didiDetailLayout) {
            top.linkTo(houseNumber_1.bottom, margin = 15.dp, goneMargin = 20.dp)
            end.linkTo(parent.end)
            start.linkTo(parent.start)
        }

        constrain(latestStatusLabelCollapsed) {
            top.linkTo(homeImage.bottom, margin = 3.dp, goneMargin = 3.dp)
            bottom.linkTo(latestStatusCollapsed.bottom)
            start.linkTo(homeImage.start)
        }
        constrain(latestStatusCollapsed) {
            end.linkTo(parent.end, margin = 10.dp)
            top.linkTo(homeImage.top, margin = 8.dp)
            width = Dimension.fillToConstraints
        }
    }
}

private fun didiDetailConstraints(): ConstraintSet {
    return ConstraintSet {
        val divider = createRefFor("divider")
        val houseNumberLabel = createRefFor("houseNumberLabel")
        val houseNumber = createRefFor("houseNumber")
        val dadaNameLabel = createRefFor("dadaNameLabel")
        val dadaName = createRefFor("dadaName")
        val casteLabel = createRefFor("casteLabel")
        val caste = createRefFor("caste")
        val tolaLabel = createRefFor("tolaLabel")
        val tola = createRefFor("tola")
        val latestStatusLabel = createRefFor("latestStatusLabel")
        val latestStatus = createRefFor("latestStatus")
        val bottomPadding = createRefFor("bottomPadding")

        val centerGuideline = createGuidelineFromStart(0.5f)


        constrain(divider) {
            top.linkTo(parent.top)
            end.linkTo(parent.end)
            start.linkTo(parent.start)
        }

        constrain(dadaNameLabel) {
            start.linkTo(parent.start, margin = 15.dp)
            top.linkTo(divider.bottom, margin = 15.dp)
            end.linkTo(centerGuideline)
            width = Dimension.fillToConstraints
        }

        constrain(dadaName) {
            start.linkTo(centerGuideline)
            top.linkTo(dadaNameLabel.top)
            bottom.linkTo(dadaNameLabel.bottom)
            end.linkTo(parent.end, margin = 10.dp)
            width = Dimension.fillToConstraints
        }

        constrain(houseNumberLabel) {
            start.linkTo(dadaNameLabel.start)
            top.linkTo(dadaNameLabel.bottom, margin = 20.dp)
            end.linkTo(centerGuideline)
            width = Dimension.fillToConstraints
        }

        constrain(houseNumber) {
            start.linkTo(centerGuideline)
            top.linkTo(houseNumberLabel.top)
            bottom.linkTo(houseNumberLabel.bottom)
            end.linkTo(parent.end, margin = 10.dp)
            width = Dimension.fillToConstraints
        }
        constrain(casteLabel) {
            start.linkTo(dadaNameLabel.start)
            top.linkTo(houseNumberLabel.bottom, margin = 20.dp)
            end.linkTo(centerGuideline)
            width = Dimension.fillToConstraints
        }

        constrain(caste) {
            start.linkTo(centerGuideline)
            top.linkTo(casteLabel.top)
            bottom.linkTo(casteLabel.bottom)
            end.linkTo(parent.end, margin = 10.dp)
            width = Dimension.fillToConstraints
        }
        constrain(tolaLabel) {
            start.linkTo(dadaNameLabel.start)
            top.linkTo(casteLabel.bottom, margin = 15.dp)
            end.linkTo(centerGuideline)
            width = Dimension.fillToConstraints
        }

        constrain(tola) {
            start.linkTo(centerGuideline)
            top.linkTo(tolaLabel.top)
            bottom.linkTo(tolaLabel.bottom)
            end.linkTo(parent.end, margin = 10.dp)
            width = Dimension.fillToConstraints
        }

        constrain(latestStatusLabel) {
            start.linkTo(dadaNameLabel.start)
            top.linkTo(tolaLabel.bottom, margin = 15.dp)
            end.linkTo(centerGuideline)
            width = Dimension.fillToConstraints
        }

        constrain(latestStatus) {
            start.linkTo(centerGuideline)
            top.linkTo(latestStatusLabel.top)
            end.linkTo(parent.end, margin = 10.dp)
            width = Dimension.fillToConstraints
        }

        constrain(bottomPadding) {
            start.linkTo(parent.start)
            top.linkTo(latestStatus.bottom)
        }
    }
}

@Composable
fun DidiItemCard(
    navController:NavHostController,
    didiViewModel: AddDidiViewModel,
    didi: DidiEntity,
    expanded: Boolean,
    modifier: Modifier,
    onExpendClick: (Boolean, DidiEntity) -> Unit,
    onItemClick: (DidiEntity) -> Unit,
    onDeleteClicked: (DidiEntity) -> Unit,
    onCircularImageClick:(DidiEntity) -> Unit
) {

    val transition = updateTransition(expanded, label = "transition")

    val context = LocalContext.current

    val didiMarkedNotAvailable  = remember {
        mutableStateOf(didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal || didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal)
    }

    val animateColor by transition.animateColor({
        tween(durationMillis = EXPANSTION_TRANSITION_DURATION)
    }, label = "animate color") {
        if (it) {
            greenOnline
        } else {
            textColorDark
        }
    }

    val animateInt by transition.animateInt({
        tween(durationMillis = 10)
    }, label = "animate float") {
        if (it) 1 else 0
    }

    val arrowRotationDegree by transition.animateFloat({
        tween(durationMillis = EXPANSTION_TRANSITION_DURATION)
    }, label = "rotationDegreeTransition") {
        if (it) 180f else 0f
    }

    val showMenu = remember {
        mutableStateOf(false)
    }

    Card(
        elevation = 10.dp,
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (didiViewModel
                        .getFromPage()
                        .equals(
                            ARG_FROM_PAT_SURVEY,
                            true
                        ) && didi.patSurveyStatus == PatSurveyStatus.COMPLETED.ordinal
                ) {
                    navController.navigate("pat_complete_didi_summary_screen/${didi.id}/${ARG_FROM_PAT_DIDI_LIST_SCREEN}")
                } else {
                    onExpendClick(expanded, didi)
                }
            }
            .then(modifier)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BoxWithConstraints {
                val constraintSet = decoupledConstraints()
                ConstraintLayout(constraintSet, modifier = Modifier.fillMaxWidth()) {
                    CircularDidiImage(
                        didi = didi,
                        modifier = Modifier.layoutId("didiImage")
                    ){
                        onCircularImageClick(didi)
                    }
                    Row(modifier = Modifier
                        .layoutId("didiRow")
                        .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            text = didi.name,
                            style = TextStyle(
                                color = animateColor,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = NotoSans,
                                textAlign = TextAlign.Start
                            ),
                        )

                        if (didiViewModel.getFromPage().equals(ARG_FROM_PAT_SURVEY, true)) {
                            if (didi.patSurveyStatus.equals(PatSurveyStatus.COMPLETED.ordinal)) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_completed_tick),
                                    contentDescription = "home image",
                                    modifier = Modifier
                                        .width(30.dp)
                                        .height(30.dp)
                                        .padding(5.dp)
                                        .layoutId("successImage")
                                )
                            }

                            if (didi.patSurveyStatus == PatSurveyStatus.INPROGRESS.ordinal) {
                                Text(text = stringResource(R.string.pat_inprogresee_status_text), style = smallTextStyle, color = inprogressYellow, modifier = Modifier
                                    .padding(5.dp)
                                    .layoutId("successImage"))
                            }

                            if (didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal || didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal) {
                                Text(text = stringResource(R.string.not_avaliable), style = smallTextStyle, color = textColorBlueLight, modifier = Modifier
                                    .padding(5.dp)
                                    .layoutId("successImage"))
                            }
                        }
                    }

                    Text(
                        text = didi.guardianName,
                        style = TextStyle(
                            color = textColorBlueLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = NotoSans
                        ),
                        textAlign = TextAlign.Start,
                      modifier = Modifier.layoutId("homeImage")
                    )

                    Text(
                        text = didi.address,
                        style = TextStyle(
                            color = textColorBlueLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = NotoSans
                        ),
                        textAlign = TextAlign.Start,
                      modifier = Modifier.layoutId("houseNumber_1")
                    )

                    if (didiViewModel.getFromPage().equals(ARG_FROM_HOME, true)) {
                        Box(
                            modifier = Modifier
                                .background(languageItemActiveBg, RoundedCornerShape(6.dp))
                                .clip(RoundedCornerShape(6.dp))
                                .layoutId("latestStatusCollapsed")
                        ) {
                            Text(
                                text = getLatestStatusText(context, didi),
                                style = TextStyle(
                                    color = blueDark,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = NotoSans
                                ),
                                textAlign = TextAlign.Center,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .align(Center)
                                    .padding(vertical = 8.dp, horizontal = 8.dp)
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .layoutId("latestStatusCollapsed"))
                    }

                    if (!didiViewModel.getFromPage().equals(ARG_FROM_PAT_SURVEY, true)) {
                        if (didi.patSurveyStatus != PatSurveyStatus.COMPLETED.ordinal && didi.patEdit) {
                            IconButton(
                                onClick = {
                                    showMenu.value = !showMenu.value
                                }, modifier = Modifier
                                    .layoutId("moreActionIcon")
                                    .visible(
                                        !didiViewModel
                                            .getFromPage()
                                            .equals(
                                                ARG_FROM_PAT_SURVEY,
                                                true
                                            ) && !didiViewModel.isSocialMappingComplete.value
                                    )
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_more_icon),
                                    contentDescription = "more action",
                                    tint = textColorDark
                                )
                            }

                        Box(modifier = Modifier.layoutId("moreDropDown")) {
                            DropdownMenu(
                                expanded = showMenu.value,
                                onDismissRequest = { showMenu.value = false },
                                modifier = Modifier
                            ) {
                                DropdownMenuItem(onClick = {
                                    showMenu.value = false
                                    onItemClick(didi)
                                }) {
                                    Text(
                                        text = stringResource(id = R.string.edit_didi),
                                        style = quesOptionTextStyle,
                                        color = textColorDark
                                    )
                                }
                                if (didi.rankingEdit) {
                                    DropdownMenuItem(onClick = {
                                        showMenu.value = false
                                        onDeleteClicked(didi)
                                    }) {
                                        Text(
                                            text = stringResource(id = R.string.delete_didi),
                                            style = quesOptionTextStyle,
                                            color = textColorDark
                                        )
                                    }
                                }
                            }
                        }
                        }

                        CardArrow(
                            modifier = Modifier.layoutId(
                                if (!didiViewModel.getFromPage().equals(ARG_FROM_PAT_SURVEY, true)
                                    && !didiViewModel.isSocialMappingComplete.value && didi.patSurveyStatus != PatSurveyStatus.COMPLETED.ordinal
                                ) "expendArrowImage" else "expendArrowImageEnd"
                            ),
                            degrees = arrowRotationDegree,
                            iconColor = animateColor,
                            onClick = { onExpendClick(expanded, didi) }
                        )

                        DidiDetailExpendableContent(
                            modifier = Modifier.layoutId("didiDetailLayout"),
                            didi,
                            animateInt == 1,
                            didiViewModel
                        )
                    }
                }
            }
            if (didiViewModel.getFromPage()
                    .equals(ARG_FROM_PAT_SURVEY, true)
            ) {
                Divider(
                    color = borderGreyLight,
                    thickness = 1.dp,
                    modifier = Modifier
                        .layoutId("divider")
                        .padding(vertical = 4.dp)
                )

                if(didi.patSurveyStatus == PatSurveyStatus.INPROGRESS.ordinal ||
                    didi.patSurveyStatus == PatSurveyStatus.NOT_STARTED.ordinal ||
                    didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal ||
                    didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal ) {


                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp, horizontal = 16.dp)
                    ) {
                        ButtonNegativeForPAT(
                            buttonTitle = stringResource(id = R.string.not_avaliable),
                            isArrowRequired = false,
                            color = if (didiMarkedNotAvailable.value) blueDark else languageItemActiveBg,
                            textColor = if (didiMarkedNotAvailable.value) white else blueDark,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(45.dp)
                                .weight(1f)
                                .background(
                                    if (didiMarkedNotAvailable.value
                                    ) blueDark else languageItemActiveBg
                                )
                        ){
                            didiMarkedNotAvailable.value = true
                            didiViewModel.setDidiAsUnavailable(didi.id)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        ButtonPositiveForPAT(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(45.dp)
                                .weight(1f)
                                .background(
                                    if (didiMarkedNotAvailable.value
                                    ) languageItemActiveBg else blueDark
                                ),
                            buttonTitle = if(didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal
                                || didi.patSurveyStatus == PatSurveyStatus.NOT_STARTED.ordinal)
                                stringResource(id = R.string.start_pat)
                            else if (didi.patSurveyStatus == PatSurveyStatus.INPROGRESS.ordinal
                                || didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal)
                                stringResource(id = R.string.continue_pat)
                            else "",
                            true,
                            color = if (!didiMarkedNotAvailable.value) blueDark else languageItemActiveBg,
                            textColor = if (!didiMarkedNotAvailable.value) white else blueDark,
                            iconTintColor = if (!didiMarkedNotAvailable.value) white else blueDark
                        ) {

                            didiViewModel.validateDidiToNavigate(didiId = didi.id){ navigationValue->
                                if(navigationValue == SummaryNavigation.SECTION_1_PAGE.ordinal) {
                                    didiViewModel.saveSummaryScreenOpenFrom(PageFrom.SUMMARY_ONE_PAGE.ordinal)
                                    navigateSocialToSummeryPage(
                                        navController,
                                        1,
                                        didi.id,
                                        didiViewModel.addDidiRepository.prefRepo
                                    )

                                }else if(navigationValue == SummaryNavigation.SECTION_2_PAGE.ordinal) {
                                    didiViewModel.saveSummaryScreenOpenFrom(PageFrom.SUMMARY_TWO_PAGE.ordinal)
                                    navigateSocialToSummeryPage(
                                        navController,
                                        2,
                                        didi.id,
                                        didiViewModel.addDidiRepository.prefRepo
                                    )

                                }else{
                                    if (didi.patSurveyStatus == PatSurveyStatus.NOT_STARTED.ordinal
                                        || didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal) {
                                        if (didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal) {
                                            didiMarkedNotAvailable.value = false
                                        }
                                        navController.navigate("didi_pat_summary/${didi.id}")

                                    } else if (didi.patSurveyStatus == PatSurveyStatus.INPROGRESS.ordinal || didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal  ) {
                                        val quesIndex = 0
                                        didiViewModel.saveQuestionScreenOpenFrom(PageFrom.DIDI_LIST_PAGE.ordinal)
                                        didiViewModel.saveSummaryScreenOpenFrom(PageFrom.DIDI_LIST_PAGE.ordinal)
                                        if (didi.section1Status == 0 || didi.section1Status == 1)
                                            navController.navigate("yes_no_question_screen/${didi.id}/$TYPE_EXCLUSION/$quesIndex")
                                        else if ((didi.section2Status == 0 || didi.section2Status == 1) && didi.patExclusionStatus == ExclusionType.NO_EXCLUSION.ordinal) navController.navigate(
                                            "yes_no_question_screen/${didi.id}/$TYPE_INCLUSION/$quesIndex"
                                        )
                                        else if (didi.section1Status == 2 && didi.patExclusionStatus == ExclusionType.SIMPLE_EXCLUSION.ordinal) navController.navigate(
                                            "yes_no_question_screen/${didi.id}/$TYPE_EXCLUSION/$quesIndex"
                                        )
                                        else if (didi.section1Status == 2 && didi.patExclusionStatus == ExclusionType.EDIT_PAT_EXCLUSION.ordinal) navController.navigate(
                                            "yes_no_question_screen/${didi.id}/$TYPE_INCLUSION/$quesIndex"
                                        )
                                    }
                                }

                            }

                        }
                    }
                }
                else{
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                        .padding(horizontal = 20.dp)
                        .clickable {
                            navController.navigate("pat_complete_didi_summary_screen/${didi.id}/${ARG_FROM_PAT_DIDI_LIST_SCREEN}")
                        }
                        .then(modifier),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(id = R.string.show),
                            style = smallTextStyleMediumWeight,
                            color = textColorDark,
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = blueDark,
                            modifier = Modifier
                                .absolutePadding(top = 4.dp, left = 2.dp)
                                .size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

fun decoupledConstraintsForPatCard(): ConstraintSet {
    return ConstraintSet {
        val didiImage = createRefFor("didiImage")
        val didiName = createRefFor("didiName")
        val didiRow = createRefFor("didiRow")
        val homeImage = createRefFor("homeImage")
        val houseNumber_1 = createRefFor("houseNumber_1")
        val village = createRefFor("village")
        val expendArrowImage = createRefFor("expendArrowImage")
        val expendArrowImageEnd = createRefFor("expendArrowImageEnd")
        val moreActionIcon = createRefFor("moreActionIcon")
        val moreDropDown = createRefFor("moreDropDown")
        val didiDetailLayout = createRefFor("didiDetailLayout")




        constrain(didiImage) {
            top.linkTo(parent.top, margin = 12.dp)
            start.linkTo(parent.start, margin = 10.dp)
        }
        constrain(didiName) {
            start.linkTo(didiImage.end, 10.dp)
            top.linkTo(parent.top, 10.dp)
            end.linkTo(moreActionIcon.start, margin = 10.dp)
            width = Dimension.fillToConstraints
        }
        constrain(didiRow) {
            start.linkTo(didiImage.end, 6.dp)
            top.linkTo(parent.top, 10.dp)
            end.linkTo(moreActionIcon.start, margin = 10.dp)
            width = Dimension.fillToConstraints
        }
        constrain(village) {
            start.linkTo(homeImage.end, margin = 10.dp)
            top.linkTo(didiName.bottom)
            end.linkTo(moreActionIcon.start, margin = 10.dp)
            width = Dimension.fillToConstraints
        }

        constrain(homeImage) {
            start.linkTo(didiImage.end, margin = 10.dp)
            top.linkTo(didiName.bottom)
        }
        constrain(houseNumber_1) {
            start.linkTo(didiImage.end, margin = 10.dp)
            top.linkTo(homeImage.bottom)
        }
        constrain(expendArrowImage) {
            top.linkTo(didiName.top)
            bottom.linkTo(village.bottom)
            end.linkTo(moreActionIcon.start)
        }

        constrain(expendArrowImageEnd) {
            top.linkTo(didiName.top)
            bottom.linkTo(village.bottom)
            end.linkTo(parent.end, margin = 10.dp)
        }

        constrain(moreActionIcon) {
            top.linkTo(didiName.top)
            bottom.linkTo(village.bottom)
            end.linkTo(parent.end, margin = 10.dp)
        }

        constrain(moreDropDown) {
            top.linkTo(moreActionIcon.bottom)
            end.linkTo(moreActionIcon.end)
        }
    }
}

@Composable
fun DidiDetailExpendableContent(modifier: Modifier, didi: DidiEntity, expended: Boolean,didiViewModel: AddDidiViewModel) {
    val constraintSet = didiDetailConstraints()

    val context = LocalContext.current

    val enterTransition = remember {
        expandVertically(
            expandFrom = Alignment.Top,
            animationSpec = tween(EXPANSTION_TRANSITION_DURATION)
        ) + fadeIn(
            initialAlpha = 0.3f,
            animationSpec = tween(EXPANSTION_TRANSITION_DURATION)
        )
    }
    val exitTransition = remember {
        shrinkVertically(
            // Expand from the top.
            shrinkTowards = Alignment.Top,
            animationSpec = tween(EXPANSTION_TRANSITION_DURATION)
        ) + fadeOut(
            // Fade in with the initial alpha of 0.3f.
            animationSpec = tween(EXPANSTION_TRANSITION_DURATION)
        )
    }

    AnimatedVisibility(
        visible = expended,
        enter = enterTransition,
        exit = exitTransition,
        modifier = Modifier.then(modifier)
    ) {
        ConstraintLayout(constraintSet, modifier = Modifier.fillMaxWidth()) {
            Divider(
                color = borderGreyLight,
                thickness = 1.dp,
                modifier = Modifier.layoutId("divider")
            )

            Text(
                text = stringResource(id = R.string.house_number) + ":",
                style = didiDetailLabelStyle,
                textAlign = TextAlign.Start,
                modifier = Modifier.layoutId("houseNumberLabel")
            )

            Text(
                text = didi.address,
                style = didiDetailItemStyle,
                textAlign = TextAlign.Start,
                modifier = Modifier.layoutId("houseNumber")
            )

            Text(
                text = stringResource(id = R.string.dada_name) + ":",
                style = didiDetailLabelStyle,
                textAlign = TextAlign.Start,
                modifier = Modifier.layoutId("dadaNameLabel")
            )

            Text(
                text = didi.guardianName,
                style = didiDetailItemStyle,
                textAlign = TextAlign.Start,
                modifier = Modifier.layoutId("dadaName")
            )

            Text(
                text = stringResource(id = R.string.caste) + ":",
                style = didiDetailLabelStyle,
                textAlign = TextAlign.Start,
                modifier = Modifier.layoutId("casteLabel")
            )

            Text(
                text = didiViewModel.getCastName(didi.castId),
                style = didiDetailItemStyle,
                textAlign = TextAlign.Start,
                modifier = Modifier.layoutId("caste")
            )

            Text(
                text = stringResource(id = R.string.tola) + ":",
                style = didiDetailLabelStyle,
                textAlign = TextAlign.Start,
                modifier = Modifier.layoutId("tolaLabel")
            )

            Text(
                text = didi.cohortName,
                style = didiDetailItemStyle,
                textAlign = TextAlign.Start,
                modifier = Modifier.layoutId("tola")
            )

            Text(
                text = stringResource(R.string.latest_status_text),
                style = didiDetailLabelStyle,
                textAlign = TextAlign.Start,
                modifier = Modifier.layoutId("latestStatusLabel")
            )

            Text(
                text = getLatestStatusText(context, didi),
                style = didiDetailItemStyle,
                textAlign = TextAlign.Start,
                modifier = Modifier.layoutId("latestStatus")
            )

            Spacer(
                modifier = Modifier
                    .layoutId("bottomPadding")
                    .height(30.dp)
            )
        }
    }
}

fun getLatestStatusText(context: Context, didi: DidiEntity): String {
    var status = BLANK_STRING
    if (didi.wealth_ranking == WealthRank.NOT_RANKED.rank) {
        status = context.getString(R.string.social_mapping_complete_status_text)
    } else {
        if (!didi.rankingEdit) {
            if (!didi.patEdit) {
                status = if (didi.patSurveyStatus == PatSurveyStatus.COMPLETED.ordinal && didi.forVoEndorsement == 1) {
                    when (didi.voEndorsementStatus) {
                        DidiEndorsementStatus.ENDORSED.ordinal, DidiEndorsementStatus.ACCEPTED.ordinal -> {
                            context.getString(R.string.vo_selected_status_text)
                        }
                        DidiEndorsementStatus.REJECTED.ordinal -> {
                           context.getString(R.string.vo_rejected_status_text)
                        }
                        else -> {
                            context.getString(R.string.pat_selected_status_text)
                        }
                    }
                } else if (didi.patSurveyStatus == PatSurveyStatus.COMPLETED.ordinal && didi.forVoEndorsement == 0) {
                    context.getString(R.string.pat_rejected_status_text)
                } else if (didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal || didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal) {
                    context.getString(R.string.pat_not_available_status_text)
                } else {
                    context.getString(R.string.wealth_ranking_status_complete_text)
                        .replace("{RANK}", getRankInLanguage(context, didi.wealth_ranking))
                }
            } else {
                status = context.getString(R.string.wealth_ranking_status_complete_text)
                    .replace("{RANK}", getRankInLanguage(context, didi.wealth_ranking))
            }
        } else {
            status = context.getString(R.string.social_mapping_complete_status_text)
        }
    }

    return status
}

fun getRankInLanguage(context: Context, wealthRanking: String): String {
    return when (wealthRanking) {
        WealthRank.RICH.rank -> context.getString(R.string.ranking_text_rich)
        WealthRank.MEDIUM.rank -> context.getString(R.string.ranking_text_medium)
        WealthRank.POOR.rank -> context.getString(R.string.ranking_text_poor)
        else -> BLANK_STRING
    }
}

@Composable
fun TolaWithImage(toal: String, modifier: Modifier) {
    Row(verticalAlignment = CenterVertically, modifier = modifier.padding(top = 3.dp)) {
        Image(
            painter = painterResource(id = R.drawable.home_icn),
            contentDescription = "home image",
            modifier = Modifier
                .width(18.dp)
                .height(14.dp),
            colorFilter = ColorFilter.tint(textColorBlueLight)
        )

        Text(
            text = toal,
            style = TextStyle(
                color = textColorBlueLight,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = NotoSans
            ),
            modifier = Modifier.padding(start = 5.dp)
        )

    }
}



@Composable
fun CircularDidiImage(didi: DidiEntity, modifier: Modifier, onImageClick: (DidiEntity) -> Unit) {
    Box(
        modifier = modifier
            .then(modifier)
            .clip(CircleShape)
            .width(44.dp)
            .height(44.dp)
            .background(color = yellowBg)
            .clickable {
                onImageClick(didi)
            },
    ) {
        if (didi.localPath.isNotEmpty()) {
            Image(
                painter = rememberImagePainter(
                    Uri.fromFile(
                        File(
                            didi.localPath.split("|")[0]
                        )
                    )
                ),
                contentDescription = "didi image",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .align(Alignment.Center)
                    .aspectRatio(1f, matchHeightConstraintsFirst = true)
                    .width(25.dp)
                    .height(28.dp)
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.didi_icon),
                contentDescription = "didi image",
                modifier = Modifier
                    .align(Alignment.Center)
                    .width(25.dp)
                    .height(28.dp)
            )
        }

    }

}

@Composable
fun CircularDidiImageView(didi: DidiEntity, modifier: Modifier,onImageClick:(DidiEntity)->Unit) {
    Box(
        modifier = modifier
            .then(modifier)
            .clip(CircleShape)
            .width(44.dp)
            .height(44.dp)
            .background(color = yellowBg)
            .clickable {
                onImageClick(didi)
            },
    ) {
        if (didi.localPath.isNotEmpty()) {
            Image(
                painter = rememberImagePainter(
                    Uri.fromFile(
                        File(
                            didi.localPath.split("|")[0]
                        )
                    )
                ),
                contentDescription = "didi image",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .align(Alignment.Center)
                    .aspectRatio(1f, matchHeightConstraintsFirst = true)
                    .width(25.dp)
                    .height(28.dp)
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.didi_icon),
                contentDescription = "didi image",
                modifier = Modifier
                    .align(Alignment.Center)
                    .width(25.dp)
                    .height(28.dp)
            )
        }

    }

}

@Preview(showBackground = true)
@Composable
fun SocialMappingDidiListPreview() {
    SocialMappingDidiListScreen(
        navController = rememberNavController(),
        modifier = Modifier,
        didiViewModel = viewModel(),
        -1,
        -1
    )


}
fun navigateSocialToSummeryPage(navController: NavHostController, section:Int,didiId: Int,prefRepo: PrefRepo) {
    if(section == 1){
        if(prefRepo.isUserBPC())
            navController.navigate("bpc_pat_section_one_summary_screen/$didiId")
        else navController.navigate("pat_section_one_summary_screen/$didiId")
    }else{
        if(prefRepo.isUserBPC())
            navController.navigate("bpc_pat_section_two_summary_screen/$didiId")
        else  navController.navigate("pat_section_two_summary_screen/$didiId")

    }
}