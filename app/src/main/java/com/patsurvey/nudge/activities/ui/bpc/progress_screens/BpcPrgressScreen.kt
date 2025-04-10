package com.patsurvey.nudge.activities.ui.bpc.progress_screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import com.nudge.core.enums.NetworkSpeed
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.MainActivity
import com.patsurvey.nudge.activities.UserDataView
import com.patsurvey.nudge.activities.VillageSelectorDropDown
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.activities.ui.theme.borderGrey
import com.patsurvey.nudge.activities.ui.theme.buttonTextStyle
import com.patsurvey.nudge.activities.ui.theme.greenLight
import com.patsurvey.nudge.activities.ui.theme.greenOnline
import com.patsurvey.nudge.activities.ui.theme.greyBorder
import com.patsurvey.nudge.activities.ui.theme.mediumTextStyle
import com.patsurvey.nudge.activities.ui.theme.smallerTextStyle
import com.patsurvey.nudge.activities.ui.theme.smallerTextStyleNormalWeight
import com.patsurvey.nudge.activities.ui.theme.stepBoxActiveColor
import com.patsurvey.nudge.activities.ui.theme.stepIconCompleted
import com.patsurvey.nudge.activities.ui.theme.stepIconDisableColor
import com.patsurvey.nudge.activities.ui.theme.stepIconEnableColor
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.activities.ui.theme.white
import com.patsurvey.nudge.database.BpcSummaryEntity
import com.patsurvey.nudge.database.StepListEntity
import com.patsurvey.nudge.utils.ARG_FROM_PAT_SURVEY
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.ConnectionMonitor
import com.patsurvey.nudge.utils.DottedShape
import com.patsurvey.nudge.utils.IconButtonForward
import com.patsurvey.nudge.utils.NudgeCore
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.PREF_KEY_IDENTITY_NUMBER
import com.patsurvey.nudge.utils.PREF_KEY_NAME
import com.patsurvey.nudge.utils.PREF_OPEN_FROM_HOME
import com.patsurvey.nudge.utils.PageFrom
import com.patsurvey.nudge.utils.StepStatus
import com.patsurvey.nudge.utils.TableCell
import com.patsurvey.nudge.utils.TextButtonWithIcon
import com.patsurvey.nudge.utils.setKeyboardToPan
import com.patsurvey.nudge.utils.showCustomToast
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BpcProgressScreen(
    modifier: Modifier = Modifier,
    bpcProgreesScreenViewModel: BpcProgressScreenViewModel,
    navController: NavHostController,
    onNavigateToStep:(Int, Int) ->Unit,
    onNavigateToSetting:()->Unit,
    onBackClick:()->Unit
) {

    val context = LocalContext.current

    val mainActivity = context as? MainActivity

    LaunchedEffect(key1 = Unit) {
        bpcProgreesScreenViewModel.init(context = context)
        delay(1000)
        bpcProgreesScreenViewModel.showLoader.value = false
    }

    val scaffoldState =
        rememberModalBottomSheetState(ModalBottomSheetValue.Hidden, skipHalfExpanded = false)
    val scope = rememberCoroutineScope()

//    val steps by bpcProgreesScreenViewModel.stepList.collectAsState()
    val villages by bpcProgreesScreenViewModel.villageList.collectAsState()
//    val steps by bpcProgreesScreenViewModel.stepList.collectAsState()


    val bpcSummaryData = remember {
        mutableStateOf(BpcSummaryEntity.getEmptySummaryForVillage(bpcProgreesScreenViewModel.getSelectedVillage().id))
    }

    val stepListData = remember {
        mutableStateListOf<StepListEntity>()
    }

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp

    mainActivity?.isLoggedInLive?.postValue(bpcProgreesScreenViewModel.isLoggedIn())

    setKeyboardToPan(mainActivity!!)

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(key1 = context) {

        bpcProgreesScreenViewModel.bpcSummaryLiveData.observe(lifecycleOwner) { bpcSummary ->
            if (bpcSummary != null)
                bpcSummaryData.value = bpcSummary
//            else
//                bpcSummaryData.value = BpcSummaryEntity.getEmptySummaryForVillage(bpcProgreesScreenViewModel.getSelectedVillage().id)
        }
        bpcProgreesScreenViewModel.stepListLive.observe(lifecycleOwner) { stepList ->
            stepListData.addAll(stepList)
        }

        onDispose {
            bpcProgreesScreenViewModel.bpcSummaryLiveData.removeObservers(lifecycleOwner)
            bpcProgreesScreenViewModel.stepListLive.removeObservers(lifecycleOwner)
        }
    }

    val isOnline  = remember {
        mutableStateOf(true)
    }

    DisposableEffect(key1 = context) {
        val connectionLiveData = ConnectionMonitor(context)
        connectionLiveData.observe(lifecycleOwner) { isNetworkAvailable ->

            NudgeLogger.d("SettingScreen",
                "DisposableEffect: connectionLiveData.observe isNetworkAvailable -> isNetworkAvailable.isOnline = ${isNetworkAvailable.isOnline}, isNetworkAvailable.connectionSpeed = ${isNetworkAvailable.connectionSpeed}, isNetworkAvailable.speedType = ${isNetworkAvailable.speedType}")
            isOnline.value = isNetworkAvailable.isOnline
                    && (isNetworkAvailable.speedType != NetworkSpeed.POOR || isNetworkAvailable.speedType != NetworkSpeed.UNKNOWN)
            NudgeCore.updateIsOnline(isNetworkAvailable.isOnline
                    && (isNetworkAvailable.speedType != NetworkSpeed.POOR || isNetworkAvailable.speedType != NetworkSpeed.UNKNOWN)
            )
        }
        onDispose {
            connectionLiveData.removeObservers(lifecycleOwner)
        }
    }

    val pullRefreshState = rememberPullRefreshState(
        bpcProgreesScreenViewModel.showLoader.value,
        {
            bpcProgreesScreenViewModel.refreshDataForCurrentVillage()
        })

    BackHandler {
        onBackClick()
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier)
    ) {
        ModalBottomSheetLayout(
            sheetContent = {
                /*Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, bottom = 50.dp)
                        .height(((2 * screenHeight) / 3).dp)
                ) {
                    Text(
                        text = stringResource(R.string.seletc_village_screen_text),
                        fontFamily = NotoSans,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = textColorDark,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                        itemsIndexed(villages) { index, village ->
                            BpcVillageAndVoBoxForBottomSheet(
                                context = context,
                                villageEntity = village,
                                index = index,
                                selectedIndex = bpcProgreesScreenViewModel.villageSelected.value,
                            ) {
                                bpcProgreesScreenViewModel.showLoader.value = true
                                bpcProgreesScreenViewModel.villageSelected.value = it
                                bpcProgreesScreenViewModel.fetchBpcSummaryData(village.id)
                                bpcProgreesScreenViewModel.updateSelectedVillage(village)
                                bpcProgreesScreenViewModel.getStepsList(village.id)
                                bpcProgreesScreenViewModel.getBpcCompletedDidiCount()
                                bpcProgreesScreenViewModel.setBpcVerificationCompleteForVillages()
                                bpcProgreesScreenViewModel.selectedText.value = bpcProgreesScreenViewModel.villageList.value[it].name
                                scope.launch {
                                    scaffoldState.hide()
                                    delay(1000)
                                    bpcProgreesScreenViewModel.showLoader.value = false
                                }
                            }
                        }
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }

                }*/
            },
            sheetState = scaffoldState,
            sheetElevation = 20.dp,
            sheetBackgroundColor = Color.White,
            sheetShape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
        ) {
            Scaffold(
                modifier = Modifier,
                topBar = {
                    TopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start) {
                                Icon(painter = painterResource(id = R.drawable.sarathi_logo_mini), contentDescription = "app bar icon", tint = textColorDark,modifier= Modifier.size(26.dp))
                                Spacer(modifier = Modifier.size(5.dp))
                                Text(
                                    text = "SARATHI",
                                    color = textColorDark,
                                    fontFamily = NotoSans,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                bpcProgreesScreenViewModel.repository.prefRepo.savePref(PREF_OPEN_FROM_HOME, true)
                                bpcProgreesScreenViewModel.repository.prefRepo.saveSettingOpenFrom(PageFrom.HOME_PAGE.ordinal)
                                onNavigateToSetting()
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.more_icon),
                                    contentDescription = "more action button",
                                    tint = blueDark,
                                    modifier = Modifier
                                        .padding(10.dp)
                                )
                            }
                        },
                        backgroundColor = Color.White
                    )
                }
            ) { it ->
                if (bpcProgreesScreenViewModel.showLoader.value) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(top = it.calculateTopPadding() + 30.dp)
                    ) {
                        CircularProgressIndicator(
                            color = blueDark,
                            modifier = Modifier
                                .size(28.dp)
                                .align(Alignment.Center)
                        )
                    }
                } else {

                    var isStepCompleted =
                        if (!stepListData.isNullOrEmpty()) {
                            bpcProgreesScreenViewModel.isStepComplete(
                                stepListData.sortedBy { it.orderNumber }.last().id,
                                bpcProgreesScreenViewModel.repository.prefRepo.getSelectedVillage().id
                            )?.observeAsState()?.value ?: 0
                        } else {
                            1
                        }


                    Column(modifier = Modifier.pullRefresh(pullRefreshState)) {
                        
                        LazyColumn(
                            Modifier
                                .background(Color.White)
                                .padding(
                                    start = 16.dp,
                                    end = 16.dp,
                                    top = it.calculateTopPadding()
                                ),
                        ) {

                            item {
                                UserDataView(
                                    modifier = Modifier,
                                    name = bpcProgreesScreenViewModel.repository.prefRepo.getPref(
                                        PREF_KEY_NAME,
                                        BLANK_STRING
                                    ) ?: "",
                                    identity = bpcProgreesScreenViewModel.repository.prefRepo.getPref(
                                        PREF_KEY_IDENTITY_NUMBER,
                                        BLANK_STRING
                                    ) ?: "",
                                    isBackButtonShow = true,
                                    isBPCUser = true
                                ){
                                    onBackClick()
                                }
                            }

                            item {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    VillageSelectorDropDown(
                                        modifier = Modifier.weight(1f),
                                        selectedText = bpcProgreesScreenViewModel.selectedText.value,
                                        showCarrotIcon = false
                                    ) {
                                        /*scope.launch {
                                            if (!scaffoldState.isVisible) {
                                                scaffoldState.show()
                                            } else {
                                                scaffoldState.hide()
                                            }
                                        }*/
                                    }
                                    IconButton(
                                        onClick = {
                                            if (isOnline.value)
                                                bpcProgreesScreenViewModel.refreshDataForCurrentVillage()
                                            else
                                                showCustomToast(context,
                                                    context.getString(R.string.network_not_available_message))
                                        }
                                    )
                                    {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = "Refresh Data button",
                                            tint = blueDark
                                        )
                                    }
                                }
                            }
                            item {
                                Spacer(modifier = Modifier.height(10.dp))
                            }

                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(white, shape = RoundedCornerShape(6.dp))
                                        .border(
                                            width = 1.dp,
                                            shape = RoundedCornerShape(6.dp),
                                            color = borderGrey
                                        )
                                ) {
                                    Column(modifier = Modifier) {
                                        Text(
                                            text = bpcProgreesScreenViewModel.repository.prefRepo.getSelectedVillage().federationName/*stringResource(R.string.crp_group_name_text)*/,
                                            textAlign = TextAlign.Start,
                                            style = mediumTextStyle,
                                            color = textColorDark,
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                                        )

                                        Divider(
                                            thickness = 1.dp,
                                            color = borderGrey
                                        )

                                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {

                                            val numberColumnWeight = 0.15f
                                            val labelColumnWeight = 0.9f

                                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                                TableCell(
                                                    text = (bpcSummaryData.value.cohortCount ?: 0).toString(),
                                                    style = TextStyle(
                                                        color = textColorDark,
                                                        fontSize = 18.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        fontFamily = NotoSans
                                                    ),
                                                    alignment = TextAlign.End,
                                                    weight = numberColumnWeight,
                                                    modifier = Modifier
                                                        .padding(vertical = 8.dp)
                                                )
                                                Spacer(modifier = Modifier.width(10.dp))
                                                TableCell(
                                                    text = if ((bpcSummaryData.value.cohortCount ?: 0) > 1)
                                                        stringResource(R.string.summary_tolas_added_text_plural)
                                                    else
                                                        stringResource(R.string.summary_tolas_added_text_singular),
                                                    style = TextStyle(
                                                        color = textColorDark,
                                                        fontSize = 15.sp,
                                                        fontWeight = FontWeight.Normal,
                                                        fontFamily = NotoSans
                                                    ),
                                                    alignment = TextAlign.Start,
                                                    weight = labelColumnWeight,
                                                    modifier = Modifier
                                                        .padding(vertical = 8.dp)
                                                )
                                            }

                                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                                TableCell(
                                                    text = (bpcSummaryData.value.mobilisedCount ?: 0).toString(),
                                                    style = TextStyle(
                                                        color = textColorDark,
                                                        fontSize = 18.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        fontFamily = NotoSans
                                                    ),
                                                    alignment = TextAlign.End,
                                                    weight = numberColumnWeight,
                                                    modifier = Modifier
                                                        .padding(vertical = 8.dp)
                                                )
                                                Spacer(modifier = Modifier.width(10.dp))
                                                TableCell(
                                                    text = if ((bpcSummaryData.value.mobilisedCount ?: 0) > 1)
                                                        stringResource(R.string.summary_didis_mobilised_text_plural)
                                                    else
                                                        stringResource(R.string.summary_didis_mobilised_text_singular),
                                                    style = TextStyle(
                                                        color = textColorDark,
                                                        fontSize = 15.sp,
                                                        fontWeight = FontWeight.Normal,
                                                        fontFamily = NotoSans
                                                    ),
                                                    alignment = TextAlign.Start,
                                                    weight = labelColumnWeight,
                                                    modifier = Modifier
                                                        .padding(vertical = 8.dp)
                                                )
                                            }

                                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                                TableCell(
                                                    text = (bpcSummaryData.value.poorDidiCount ?: 0).toString(),
                                                    style = TextStyle(
                                                        color = textColorDark,
                                                        fontSize = 18.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        fontFamily = NotoSans
                                                    ),
                                                    alignment = TextAlign.End,
                                                    weight = numberColumnWeight,
                                                    modifier = Modifier
                                                        .padding(vertical = 8.dp)
                                                )
                                                Spacer(modifier = Modifier.width(10.dp))
                                                TableCell(
                                                    text = if ((bpcSummaryData.value.poorDidiCount ?: 0) > 1)
                                                        stringResource(R.string.summary_wealth_ranking_text_plural)
                                                    else
                                                        stringResource(R.string.summary_wealth_ranking_text_singular),
                                                    style = TextStyle(
                                                        color = textColorDark,
                                                        fontSize = 15.sp,
                                                        fontWeight = FontWeight.Normal,
                                                        fontFamily = NotoSans
                                                    ),
                                                    alignment = TextAlign.Start,
                                                    weight = labelColumnWeight,
                                                    modifier = Modifier
                                                        .padding(vertical = 8.dp)
                                                )
                                            }

                                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                                TableCell(
                                                    text = (bpcSummaryData.value.sentVoEndorsementCount ?: 0).toString(),
                                                    style = TextStyle(
                                                        color = textColorDark,
                                                        fontSize = 18.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        fontFamily = NotoSans
                                                    ),
                                                    alignment = TextAlign.End,
                                                    weight = numberColumnWeight,
                                                    modifier = Modifier
                                                        .padding(vertical = 8.dp)
                                                )
                                                Spacer(modifier = Modifier.width(10.dp))
                                                TableCell(
                                                    text = if ((bpcSummaryData.value.sentVoEndorsementCount ?: 0) > 1)
                                                        stringResource(R.string.summary_vo_endoresement_text_plural)
                                                    else
                                                        stringResource(R.string.summary_vo_endoresement_text_singular),
                                                    style = TextStyle(
                                                        color = textColorDark,
                                                        fontSize = 15.sp,
                                                        fontWeight = FontWeight.Normal,
                                                        fontFamily = NotoSans
                                                    ),
                                                    alignment = TextAlign.Start,
                                                    weight = labelColumnWeight,
                                                    modifier = Modifier
                                                        .padding(vertical = 8.dp)
                                                )
                                            }

                                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                                TableCell(
                                                    text = (bpcSummaryData.value.voEndorsedCount ?: 0).toString(),
                                                    style = TextStyle(
                                                        color = textColorDark,
                                                        fontSize = 18.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        fontFamily = NotoSans
                                                    ),
                                                    alignment = TextAlign.End,
                                                    weight = numberColumnWeight,
                                                    modifier = Modifier
                                                        .padding(vertical = 8.dp)
                                                )
                                                Spacer(modifier = Modifier.width(10.dp))
                                                TableCell(
                                                    text = if ((bpcSummaryData.value.voEndorsedCount ?: 0) > 1)
                                                        stringResource(R.string.didis_endorsed_by_vo_plural)
                                                    else stringResource(
                                                        R.string.didi_endorsed_by_vo_singular),
                                                    style = TextStyle(
                                                        color = textColorDark,
                                                        fontSize = 15.sp,
                                                        fontWeight = FontWeight.Normal,
                                                        fontFamily = NotoSans
                                                    ),
                                                    alignment = TextAlign.Start,
                                                    weight = labelColumnWeight,
                                                    modifier = Modifier
                                                        .padding(vertical = 8.dp)
                                                )

                                            }


                                            Row(
                                                Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                TableCell(
                                                    text = bpcProgreesScreenViewModel.bpcCompletedDidiCount.value.toString()
                                                        ?: "0",
                                                    style = TextStyle(
                                                        color = textColorDark,
                                                        fontSize = 18.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        fontFamily = NotoSans
                                                    ),
                                                    alignment = TextAlign.End,
                                                    weight = numberColumnWeight,
                                                    modifier = Modifier
                                                        .padding(vertical = 8.dp)
                                                )
                                                Spacer(modifier = Modifier.width(10.dp))
                                                TableCell(
                                                    text = stringResource(R.string.didi_verified_by_bpc),
                                                    style = TextStyle(
                                                        color = textColorDark,
                                                        fontSize = 15.sp,
                                                        fontWeight = FontWeight.Normal,
                                                        fontFamily = NotoSans
                                                    ),
                                                    alignment = TextAlign.Start,
                                                    weight = labelColumnWeight,
                                                    modifier = Modifier
                                                        .padding(vertical = 8.dp)
                                                )

                                            }

                                            Row(
                                                Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                TableCell(
                                                    text = (bpcProgreesScreenViewModel.passPercentage.collectAsState().value).toString() + "%",
                                                    style = TextStyle(
                                                        color = textColorDark,
                                                        fontSize = 18.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        fontFamily = NotoSans
                                                    ),
                                                    alignment = TextAlign.End,
                                                    weight = 0.2f,
                                                    modifier = Modifier
                                                        .padding(vertical = 8.dp)
                                                )
                                                Spacer(modifier = Modifier.width(10.dp))
                                                TableCell(
                                                    text = stringResource(R.string.match_percentage_box_text)
                                                        .replace("{PERCENTAGE}%", "", true),
                                                    style = TextStyle(
                                                        color = textColorDark,
                                                        fontSize = 15.sp,
                                                        fontWeight = FontWeight.Normal,
                                                        fontFamily = NotoSans
                                                    ),
                                                    alignment = TextAlign.Start,
                                                    weight = 0.8f,
                                                    modifier = Modifier
                                                        .padding(vertical = 8.dp)
                                                )

                                            }



                                            Text(
                                                text = stringResource(R.string.current_status_text),
                                                style = buttonTextStyle,
                                                color = textColorDark
                                            )

                                            Spacer(modifier = Modifier.height(8.dp))

                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Box(modifier = Modifier) {
                                                    Divider(
                                                        modifier = Modifier
                                                            .height(1.dp)
                                                            .align(Alignment.Center)
                                                            .padding(horizontal = 16.dp)
                                                            .background(
                                                                greyBorder,
                                                                shape = DottedShape(step = 4.dp)
                                                            )
                                                    )
                                                    Row(
                                                        modifier = Modifier
                                                            .align(Alignment.Center)
                                                            .fillMaxWidth(),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.SpaceEvenly
                                                    ) {
                                                        Box(
                                                            modifier = Modifier
                                                                .clip(CircleShape)
                                                                .border(
                                                                    width = 1.dp,
                                                                    color = greenOnline,
                                                                    shape = CircleShape
                                                                )
                                                                .background(
                                                                    greenOnline,
                                                                    shape = CircleShape
                                                                )
                                                                .padding(6.dp)
                                                                .size(24.dp)
                                                                .aspectRatio(1f),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Text(
                                                                text = "1",
                                                                color = white,
                                                                textAlign = TextAlign.Center,
                                                                modifier = Modifier
                                                                    .align(Alignment.Center)
                                                                    .absolutePadding(bottom = 3.dp),
                                                                style = smallerTextStyleNormalWeight,
                                                            )
                                                        }
                                                        Box(
                                                            modifier = Modifier
                                                                .clip(CircleShape)
                                                                .border(
                                                                    width = 1.dp,
                                                                    color = greenOnline,
                                                                    shape = CircleShape
                                                                )
                                                                .background(
                                                                    greenOnline,
                                                                    shape = CircleShape
                                                                )
                                                                .padding(6.dp)
                                                                .size(24.dp)
                                                                .aspectRatio(1f),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Text(
                                                                text = "2",
                                                                color = white,
                                                                textAlign = TextAlign.Center,
                                                                modifier = Modifier
                                                                    .align(Alignment.Center)
                                                                    .absolutePadding(bottom = 3.dp),
                                                                style = smallerTextStyleNormalWeight,
                                                            )
                                                        }
                                                        Box(
                                                            modifier = Modifier
                                                                .clip(CircleShape)
                                                                .border(
                                                                    width = 1.dp,
                                                                    color = greenOnline,
                                                                    shape = CircleShape
                                                                )
                                                                .background(
                                                                    greenOnline,
                                                                    shape = CircleShape
                                                                )
                                                                .padding(6.dp)
                                                                .size(24.dp)
                                                                .aspectRatio(1f),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Text(
                                                                text = "3",
                                                                color = white,
                                                                textAlign = TextAlign.Center,
                                                                modifier = Modifier
                                                                    .align(Alignment.Center)
                                                                    .absolutePadding(bottom = 3.dp),
                                                                style = smallerTextStyleNormalWeight,
                                                            )
                                                        }
                                                        Box(
                                                            modifier = Modifier
                                                                .clip(CircleShape)
                                                                .border(
                                                                    width = 1.dp,
                                                                    color = greenOnline,
                                                                    shape = CircleShape
                                                                )
                                                                .background(
                                                                    greenOnline,
                                                                    shape = CircleShape
                                                                )
                                                                .padding(6.dp)
                                                                .size(24.dp)
                                                                .aspectRatio(1f),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Text(
                                                                text = "4",
                                                                color = white,
                                                                textAlign = TextAlign.Center,
                                                                modifier = Modifier
                                                                    .align(Alignment.Center)
                                                                    .absolutePadding(bottom = 3.dp),
                                                                style = smallerTextStyleNormalWeight,
                                                            )
                                                        }

                                                        Box(
                                                            modifier = Modifier
                                                                .clip(CircleShape)
                                                                .border(
                                                                    width = 1.dp,
                                                                    color = greenOnline,
                                                                    shape = CircleShape
                                                                )
                                                                .background(
                                                                    greenOnline,
                                                                    shape = CircleShape
                                                                )
                                                                .padding(6.dp)
                                                                .size(24.dp)
                                                                .aspectRatio(1f),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Text(
                                                                text = "5",
                                                                color = white,
                                                                textAlign = TextAlign.Center,
                                                                modifier = Modifier
                                                                    .align(Alignment.Center)
                                                                    .absolutePadding(bottom = 3.dp),
                                                                style = smallerTextStyleNormalWeight,
                                                            )
                                                        }

                                                        Box(
                                                            modifier = Modifier
                                                                .clip(CircleShape)
                                                                .border(
                                                                    width = 1.dp,
                                                                    color = if (isStepCompleted == StepStatus.COMPLETED.ordinal) greenOnline else stepBoxActiveColor,
                                                                    shape = CircleShape
                                                                )
                                                                .background(
                                                                    if (isStepCompleted == StepStatus.COMPLETED.ordinal) greenOnline else stepBoxActiveColor,
                                                                    shape = CircleShape
                                                                )
                                                                .padding(6.dp)
                                                                .size(24.dp)
                                                                .aspectRatio(1f),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Text(
                                                                text = "6",
                                                                color = if (isStepCompleted == StepStatus.COMPLETED.ordinal) white else textColorDark,
                                                                textAlign = TextAlign.Center,
                                                                modifier = Modifier
                                                                    .align(Alignment.Center)
                                                                    .absolutePadding(bottom = 3.dp),
                                                                style = smallerTextStyleNormalWeight,
                                                            )
                                                        }
                                                    }
                                                }
                                                if (isStepCompleted != StepStatus.COMPLETED.ordinal) {
                                                    Spacer(modifier = Modifier.height(10.dp))
                                                    Text(
                                                        text = stringResource(id = R.string.vo_endorsement_completed),
                                                        fontSize = 12.sp,
                                                        fontFamily = NotoSans,
                                                        fontWeight = FontWeight.Medium,
                                                        color = textColorDark,
                                                        textAlign = TextAlign.Center,
                                                        modifier = Modifier.padding(start = (3 * 48).dp)
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(16.dp))
                                        }
                                    }
                                }
                            }

                            if (isStepCompleted == StepStatus.COMPLETED.ordinal) {
                                bpcProgreesScreenViewModel.getBpcCompletedDidiCount()
                            }

                            bpcProgreesScreenViewModel.isBpcVerificationComplete.value[bpcProgreesScreenViewModel.repository.prefRepo.getSelectedVillage().id] = isStepCompleted == StepStatus.COMPLETED.ordinal

                            item {
                                StepsBoxForBpc(
                                    boxTitle = stringResource(id = R.string.step_bpc_verification),
                                    stepNo = 6,
                                    index = 1,
                                    iconId = 6,
                                    viewModel = bpcProgreesScreenViewModel,
                                    stepListData = stepListData,
                                    shouldBeActive = isStepCompleted == StepStatus.INPROGRESS.ordinal || isStepCompleted == StepStatus.COMPLETED.ordinal,
                                    isCompleted = isStepCompleted == StepStatus.COMPLETED.ordinal,
                                    onclick = {
                                        if ((context as MainActivity).isOnline.value ?: false) {
                                            bpcProgreesScreenViewModel.callWorkFlowApiToGetWorkFlowId()
                                        }
//                                        bpcProgreesScreenViewModel.prefRepo.savePref(PREF_NEED_TO_POST_BPC_MATCH_SCORE_FOR_ + bpcProgreesScreenViewModel.prefRepo.getSelectedVillage().id, false)
                                        if (isStepCompleted == StepStatus.INPROGRESS.ordinal || isStepCompleted == StepStatus.COMPLETED.ordinal) {
                                            if (!stepListData.isNullOrEmpty()) {
                                                val stepId =
                                                    stepListData.sortedBy { it.orderNumber }.last().id
                                                onNavigateToStep(
                                                    bpcProgreesScreenViewModel.repository.prefRepo.getSelectedVillage().id,
                                                    stepId,
                                                )
                                                bpcProgreesScreenViewModel.repository.prefRepo.saveFromPage(ARG_FROM_PAT_SURVEY)
                                            } else {
                                                showCustomToast(context, context.getString(R.string.something_went_wrong))
                                            }
                                        }
                                    }
                                )
                            }


                            item { Spacer(modifier = Modifier.height(200.dp)) }
                        }

                    }
                }
            }
        }
    }
}


@Composable
fun StepsBoxForBpc(
    modifier: Modifier = Modifier,
    boxTitle: String,
    stepNo: Int,
    index: Int,
    iconId: Int,
    viewModel: BpcProgressScreenViewModel,
    stepListData: SnapshotStateList<StepListEntity>,
    isCompleted: Boolean = false,
    shouldBeActive: Boolean = false,
    onclick: (Int) -> Unit
) {
    val dividerMargins = 32.dp
    if (stepNo == 6)
        Spacer(modifier = Modifier.height(20.dp))

    val context = LocalContext.current

    ConstraintLayout(
        modifier = Modifier
            .background(Color.White)
            .border(
                width = 0.dp,
                color = Color.Transparent,
            )
            .then(modifier)
    ) {
        val (step_no, stepBox, divider1, divider2) = createRefs()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .border(
                    width = 1.dp,
                    color = if (isCompleted) greenOnline else greyBorder,
                    shape = RoundedCornerShape(6.dp)
                )
                .background(Color.White)
                .clickable {
                    if (!stepListData.isNullOrEmpty()) {
                        onclick(index)
                    } else {
                        showCustomToast(context, "Something went wrong!") //Confirm this message.
                    }
                }
                .constrainAs(stepBox) {
                    start.linkTo(parent.start)
                    top.linkTo(step_no.bottom, margin = -16.dp)
                }

        ) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isCompleted) greenLight else if (shouldBeActive) stepBoxActiveColor else white)
                    .padding(vertical = /*if (isCompleted) 10.dp else */14.dp)
                    .padding(end = 16.dp, start = 8.dp),
            ) {
                val (textContainer, buttonContainer, iconContainer) = createRefs()
                val iconResourceId = R.drawable.bpc_verification_icon
                if (iconResourceId != null) {
                    Icon(
                        painter = painterResource(id = iconResourceId),
                        contentDescription = null,
                        tint = if (shouldBeActive) {
                            if (isCompleted) stepIconCompleted else stepIconEnableColor
                        } else stepIconDisableColor,
                        modifier = Modifier
                            .constrainAs(iconContainer) {
                                start.linkTo(parent.start)
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                            }
                            .size(48.dp)
                            .padding(
                                top = if (isCompleted) 0.dp else 6.dp,
                                start = if (isCompleted) 0.dp else 4.dp
                            )
                    )
                }

                Column(
                    modifier = Modifier
                        .padding(horizontal = 14.dp)
                        .constrainAs(textContainer) {
                            top.linkTo(iconContainer.top)
                            start.linkTo(iconContainer.end)
                            bottom.linkTo(iconContainer.bottom)
                            end.linkTo(buttonContainer.start)
                            width = Dimension.fillToConstraints
                        }
                        .fillMaxWidth()
                ) {
                    Text(
                        text = if (boxTitle.contains("pat ", true)) boxTitle.replace(
                            "pat ",
                            "PAT ",
                            true
                        ) else boxTitle,
                        color = if (isCompleted) greenOnline else textColorDark,
                        modifier = Modifier
                            .padding(
                                top = 10.dp,
                                bottom = if (isCompleted) 0.dp else 10.dp,
                                end = 10.dp
                            )
                            .fillMaxWidth(),
                        softWrap = true,
                        textAlign = TextAlign.Start,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2,
                        style = buttonTextStyle
                    )
                    if (isCompleted) {
//                        Spacer(modifier = Modifier.height(4.dp))
                        //TODO add string for other steps when steps is complete.
                        val count = viewModel.bpcCompletedDidiCount.value
                        val subText = if (count > 1) stringResource(R.string.ultra_poor_didis_verified_text_plural, count) else stringResource(R.string.ultra_poor_didis_verified_text_singular, count)
                        if (subText != null || subText != "") {
                            Text(
                                text = subText,
                                color = if (isCompleted) greenOnline else textColorDark,
                                modifier = Modifier
                                    .padding(bottom = 10.dp)
                                    .fillMaxWidth(),
                                softWrap = true,
                                textAlign = TextAlign.Start,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 2,
                                style = smallerTextStyle
                            )
                        }
                    }
                }

                if (shouldBeActive) {
                    if (isCompleted) {
                        TextButtonWithIcon(modifier = Modifier
                            .constrainAs(buttonContainer) {
                                bottom.linkTo(textContainer.bottom)
                                top.linkTo(textContainer.top)
                                end.linkTo(parent.end)
                            }

                        )
                        {
                            if (!stepListData.isNullOrEmpty()) {
                                onclick(index)
                            } else {
                                showCustomToast(context, "Something went wrong!")
                            }
                        }
                    } else {
                        IconButtonForward(
                            modifier = Modifier
                                .constrainAs(buttonContainer) {
                                    bottom.linkTo(textContainer.bottom)
                                    top.linkTo(textContainer.top)
                                    end.linkTo(parent.end)
                                }
                                .size(40.dp)
                        ) {
                            if (!stepListData.isNullOrEmpty()) {
                                onclick(index)
                            } else {
                                showCustomToast(context, "Something went wrong!")
                            }
                        }
                    }
                } else {
                    Spacer(modifier = Modifier
                        .width(48.dp)
                        .constrainAs(buttonContainer) {
                            bottom.linkTo(textContainer.bottom)
                            top.linkTo(textContainer.top)
                            end.linkTo(parent.end)
                        }
                    )
                }
            }
        }

        if (isCompleted) {
            Image(
                painter = painterResource(id = R.drawable.icon_check_circle_green),
                contentDescription = null,
                modifier = modifier
                    .border(
                        width = 2.dp,
                        color = Color.Transparent,
                        shape = CircleShape

                    )
                    .clip(CircleShape)
                    .background(Color.Transparent)
                    .constrainAs(step_no) {
                        start.linkTo(parent.start, margin = 16.dp)
                        top.linkTo(parent.top)
                    }
            )
        } else {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .border(
                        width = 1.dp,
                        color = greyBorder,
                        shape = CircleShape
                    )
                    .background(Color.White, shape = CircleShape)
                    .padding(6.dp)
                    .size(25.dp)
                    .aspectRatio(1f)
                    .constrainAs(step_no) {
                        start.linkTo(parent.start, margin = 16.dp)
                        top.linkTo(parent.top)
                    }
            ) {
                Text(
                    text = "$stepNo",
                    color = textColorDark,
                    style = smallerTextStyleNormalWeight,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center)

                )
            }

        }
    }
}