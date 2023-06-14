package com.patsurvey.nudge.activities.settings

import android.content.Context.BATTERY_SERVICE
import android.os.BatteryManager
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateInt
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.MainActivity
import com.patsurvey.nudge.activities.MainTitle
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.black100Percent
import com.patsurvey.nudge.activities.ui.theme.borderGreyLight
import com.patsurvey.nudge.activities.ui.theme.didiDetailItemStyle
import com.patsurvey.nudge.activities.ui.theme.greenDark
import com.patsurvey.nudge.activities.ui.theme.greenOnline
import com.patsurvey.nudge.activities.ui.theme.greyBorder
import com.patsurvey.nudge.activities.ui.theme.mediumTextStyle
import com.patsurvey.nudge.activities.ui.theme.redBgLight
import com.patsurvey.nudge.activities.ui.theme.redIconColor
import com.patsurvey.nudge.activities.ui.theme.redMessageColor
import com.patsurvey.nudge.activities.ui.theme.redOffline
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.activities.ui.theme.textColorDark80
import com.patsurvey.nudge.activities.ui.theme.white
import com.patsurvey.nudge.model.dataModel.SettingOptionModel
import com.patsurvey.nudge.navigation.home.HomeScreens
import com.patsurvey.nudge.navigation.home.SettingScreens
import com.patsurvey.nudge.navigation.navgraph.Graph
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.ButtonNegative
import com.patsurvey.nudge.utils.ButtonPositive
import com.patsurvey.nudge.utils.EXPANSTION_TRANSITION_DURATION
import com.patsurvey.nudge.utils.LAST_SYNC_TIME
import com.patsurvey.nudge.utils.showCustomToast
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun SettingScreen(
    viewModel: SettingViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
//    LaunchedEffect(key1 = true) {
    val list = ArrayList<SettingOptionModel>()
    val lastSyncTimeInMS = viewModel.lastSyncTime.value

    val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.US)
    val lastSyncTime = if (lastSyncTimeInMS != 0L) dateFormat.format(lastSyncTimeInMS) else ""
    list.add(
        SettingOptionModel(
            1,
            context.getString(R.string.sync_up),
            context.getString(R.string.last_syncup_text)
                .replace("{LAST_SYNC_TIME}", lastSyncTime.toString())
        )
    )
    list.add(SettingOptionModel(2, context.getString(R.string.profile), BLANK_STRING))
    list.add(SettingOptionModel(3, context.getString(R.string.forms), BLANK_STRING))
    list.add(SettingOptionModel(4, context.getString(R.string.training_videos), BLANK_STRING))
    list.add(SettingOptionModel(5, context.getString(R.string.language_text), BLANK_STRING))
    viewModel.createSettingMenu(list)
//    }
    /*LaunchedEffect(key1 = true) {
        val villageId = viewModel.prefRepo.getSelectedVillage().id
        viewModel.isFormAAvailableForVillage(villageId)
        viewModel.isFormBAvailableForVillage(villageId)
        viewModel.isFormCAvailableForVillage(villageId)
    }*/

    val formList = mutableListOf<String>()
    formList.add("Digital Form A")
    formList.add("Digital Form B")
    formList.add("Digital Form C")

    val optionList = viewModel.optionList.collectAsState()

//    val defaultStepSize = "-"
    val expanded = remember {
        mutableStateOf(false)
    }
    val showSyncDialogStatus = remember {
        mutableStateOf(false)
    }
    val networkError = viewModel.networkErrorMessage.value
    val isDataNeedToBeSynced = remember {
        mutableStateOf(0)
    }
/*    val stepOneSize = remember {
        mutableStateOf(defaultStepSize)
    }
    val stepTwoSize = remember {
        mutableStateOf(defaultStepSize)
    }
    val stepThreeSize = remember {
        mutableStateOf(defaultStepSize)
    }
    val stepFourSize = remember {
        mutableStateOf(defaultStepSize)
    }
    val stepFiveSize = remember {
        mutableStateOf(defaultStepSize)
    }*/

    val stepOneStatus = remember {
        mutableStateOf(0)
    }
    val stepTwoStatus = remember {
        mutableStateOf(0)
    }
    val stepThreeStatus = remember {
        mutableStateOf(0)
    }
    val stepFourStatus = remember {
        mutableStateOf(0)
    }
    val stepFiveStatus = remember {
        mutableStateOf(0)
    }

    BackHandler() {
        navController.navigate(Graph.HOME) {
            popUpTo(HomeScreens.PROGRESS_SCREEN.route) {
                inclusive = true
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .then(modifier),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = mediumTextStyle,
                        color = textColorDark,
                        modifier = Modifier,
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, null, tint = textColorDark)
                    }
                },
                backgroundColor = Color.White
            )
            /*Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "back press",
                    tint = textColorDark,
                    modifier = Modifier
                        .padding(vertical = 10.dp, horizontal = 16.dp)
                        .clickable {
                            navController.navigate(Graph.HOME) {
                                popUpTo(HomeScreens.PROGRESS_SCREEN.route) {
                                    inclusive = true
                                }
                            }
                        }
                )
                Text(text = "Settings", style = mediumTextStyle, color = textColorDark, modifier = Modifier
                    .padding(vertical = 10.dp)
                    .weight(1f), textAlign = TextAlign.Center)
                Spacer(modifier = Modifier
                    .size(24.dp)
                    .padding(vertical = 10.dp))
            }*/
        }
    ) {
        ConstraintLayout(
            modifier = Modifier
                .background(Color.White)
                .padding(top = it.calculateTopPadding())
                .fillMaxSize()
        ) {
            val (mainBox, logoutButton) = createRefs()

            Column(modifier = Modifier
                .background(Color.White)
                .fillMaxWidth()
                .constrainAs(mainBox) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                }) {
                LazyColumn {
                    itemsIndexed(optionList.value) { index, item ->
                        SettingCard(
                            title = item.title,
                            subTitle = item.subTitle,
                            expanded = item.title == stringResource(id = R.string.forms) && expanded.value,
                            showArrow = item.title == stringResource(id = R.string.forms),
                            formList = formList,
                            navController = navController
                        ) {
                            when (index) {
                                0 -> {
//                                    showSyncDialogStatus.value = true
                                    viewModel.showSyncDialog.value = true
                                }

                                1 -> {
                                    navController.navigate(SettingScreens.PROFILE_SCREEN.route)
                                }

                                2 -> {
                                    expanded.value = !expanded.value
                                }

                                3 -> {
                                    navController.navigate(SettingScreens.VIDEO_LIST_SCREEN.route)
                                }

                                4 -> {
                                    navController.navigate(SettingScreens.LANGUAGE_SCREEN.route)
                                }

                                else -> {
                                    showCustomToast(
                                        context,
                                        context.getString(R.string.this_section_is_in_progress)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(id = R.dimen.dp_15))
                .padding(vertical = dimensionResource(id = R.dimen.dp_15))
                .constrainAs(logoutButton) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }) {
                ButtonPositive(
                    buttonTitle = stringResource(id = R.string.logout),
                    isArrowRequired = false,
                    isActive = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp)

                ) {
                }
            }
            /*if (viewModel.showLoader.value) {
                showSyncInProgressDialog(setShowDialog = {
                    viewModel.showLoader.value = false
                }, viewModel)
                viewModel.syncDataOnServer(context)
                viewModel.prefRepo.savePref(LAST_SYNC_TIME, System.currentTimeMillis())
            }*/
            if (viewModel.showSyncDialog.value) {
                /*stepOneStatus.value = 0
                stepTwoStatus.value = 0
                stepThreeStatus.value = 0
                stepFourStatus.value = 0
                stepFiveStatus.value = 0*/
                showSyncDialog(setShowDialog = {
//                    showSyncDialogStatus.value = it
                    viewModel.showSyncDialog.value = it
                }, positiveButtonClicked = {
                    viewModel.showLoader.value = true
                }, stepOneStatus = stepOneStatus.value,
                    stepTwoStatus = stepTwoStatus.value,
                    stepThreeStatus = stepThreeStatus.value,
                    stepFourStatus = stepFourStatus.value,
                    stepFiveStatus = stepFiveStatus.value,
                    settingViewModel = viewModel,
                    showSyncDialogStatus = viewModel.showSyncDialog,
                    isDataNeedToBeSynced = isDataNeedToBeSynced
                )
                viewModel.isFirstStepNeedToBeSync(stepOneStatus)
                viewModel.isSecondStepNeedToBeSync(stepTwoStatus)
                viewModel.isThirdStepNeedToBeSync(stepThreeStatus)
                viewModel.isFourthStepNeedToBeSync(stepFourStatus)
                viewModel.isFifthStepNeedToBeSync(stepFiveStatus)
                if(stepOneStatus.value == 2 && stepTwoStatus.value == 2 && stepThreeStatus.value == 2 && stepFourStatus.value == 2 && stepFiveStatus.value == 2)
                    isDataNeedToBeSynced.value = 1
                else if((stepOneStatus.value == 3 || stepOneStatus.value == 0)
                        && (stepTwoStatus.value == 3 || stepTwoStatus.value == 0)
                        && (stepThreeStatus.value == 3 || stepThreeStatus.value == 0)
                        && (stepFourStatus.value == 3 || stepFourStatus.value == 0)
                        && (stepFiveStatus.value == 3 || stepFiveStatus.value == 0))
                    isDataNeedToBeSynced.value = 2
                else
                    isDataNeedToBeSynced.value = 0
                viewModel.isDataNeedToBeSynced(stepOneStatus,stepTwoStatus,stepThreeStatus,stepFourStatus,stepFiveStatus)
            }
        }
    }
    if(networkError.isNotEmpty()){
        showCustomToast(context,networkError)
    }
}

@Composable
fun showSyncDialog(
    setShowDialog: (Boolean) -> Unit,
    positiveButtonClicked: () -> Unit,
    stepOneStatus: Int,
    stepTwoStatus: Int,
    stepThreeStatus: Int,
    stepFourStatus: Int,
    stepFiveStatus: Int,
    settingViewModel: SettingViewModel,
    showSyncDialogStatus : MutableState<Boolean>,
    isDataNeedToBeSynced : MutableState<Int>
) {

    val context = LocalContext.current

    val isInternetConnected = (context as MainActivity).isOnline.value

    val backgroundIndicatorColor = Color.LightGray.copy(alpha = 0.3f)
    val indicatorPadding = 48.dp
    val gradientColors = listOf(Color(0xFF2EE08E), Color(0xFF2EE08E))
    val numberStyle: TextStyle = mediumTextStyle
    val animationDuration = 1000
    val animationDelay = 0

    settingViewModel.syncPercentage.value = 0f
    val syncPercentage: Float = settingViewModel.syncPercentage.value
    Log.e("sync", "->$syncPercentage")

    val animateNumber = animateFloatAsState(
        targetValue = syncPercentage,
        animationSpec = tween(
            durationMillis = animationDuration,
            delayMillis = animationDelay
        )
    )



    Dialog(onDismissRequest = { setShowDialog(false) }, properties = DialogProperties(
        dismissOnClickOutside = false
    )) {
        Surface(
            color = Color.Transparent,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(contentAlignment = Alignment.Center) {
                Column(
                    modifier = Modifier
                        .background(color = white, shape = RoundedCornerShape(6.dp)),
                ) {
                    Column(Modifier.padding(vertical = 16.dp, horizontal = 16.dp),verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceAround,
                            modifier = Modifier
                        ) {
                            MainTitle(
                                if (isDataNeedToBeSynced.value == 0) stringResource(R.string.sync_your_data) else if (isDataNeedToBeSynced.value == 1 ) stringResource(R.string.your_data_already_synced) else stringResource(R.string.data_synced_successfully),
                                Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                align = TextAlign.Center
                            )
                        }
                        val batSystemService =
                            LocalContext.current.getSystemService(BATTERY_SERVICE) as BatteryManager
                        val batteryLevel =
                            batSystemService.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column() {
                                Text(
                                    text = stringResource(id = R.string.battery) + ": $batteryLevel%",
                                    style = didiDetailItemStyle,
                                    textAlign = TextAlign.Start,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier
                                )
                                if (batteryLevel < 30)
                                    Text(
                                        text = "(Min 30% required)",
                                        color = redOffline,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        fontFamily = NotoSans
                                    )
                            }

                            Text(
                                text = buildAnnotatedString {
                                    withStyle(
                                        style = SpanStyle(
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = textColorDark80,
                                        )
                                    ) {
                                        append(stringResource(id = R.string.mobile_data) + ": ")
                                    }
                                    withStyle(
                                        style = SpanStyle(
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isInternetConnected) greenOnline else redOffline,
                                        )
                                    ) {
                                        append(
                                            if (isInternetConnected) stringResource(id = R.string.connected) else stringResource(
                                                id = R.string.no_internet
                                            )
                                        )
                                    }
                                },
                            )
                        }

                        Divider(
                            thickness = 1.dp,
                            color = greyBorder
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Box(Modifier.fillMaxWidth()) {
                                Row(modifier = Modifier.align(Alignment.CenterStart)) {
                                    Text(
                                        text = stringResource(id = R.string.step_1) + ": ",
                                        style = didiDetailItemStyle,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        textAlign = TextAlign.Start,
                                        modifier = Modifier
                                    )
                                    Text(
                                        text = stringResource(id = R.string.transect_wale_title),
                                        style = didiDetailItemStyle,
                                        textAlign = TextAlign.Start,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 12.sp,
                                        modifier = Modifier
                                    )
                                    /*Text(
                                    text = " $stepOneSize",
                                    style = didiDetailItemStyle,
                                    textAlign = TextAlign.Start,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 12.sp,
                                    modifier = Modifier
                                )*/
                                }
                                if (stepOneStatus == 2 || stepOneStatus == 3)
                                    Icon(
                                        painter = painterResource(id = R.drawable.icon_check_green_without_border),
                                        contentDescription = null,
                                        tint = greenDark,
                                        modifier = Modifier
                                            .align(Alignment.CenterEnd)
                                            .size(24.dp)
                                            .padding(4.dp)
                                    )
                                else if (stepOneStatus == 1) {
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .align(Alignment.CenterEnd)
                                            .padding(4.dp),
                                        color = textColorDark,
                                        strokeWidth = 1.dp
                                    )
                                }
                            }
                            Box(Modifier.fillMaxWidth()) {
                                Row(modifier = Modifier.align(Alignment.CenterStart)) {
                                    Text(
                                        text = stringResource(id = R.string.step_2) + ": ",
                                        style = didiDetailItemStyle,
                                        textAlign = TextAlign.Start,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier
                                    )
                                    Text(
                                        text = stringResource(id = R.string.social_mapping),
                                        style = didiDetailItemStyle,
                                        textAlign = TextAlign.Start,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 12.sp,
                                        modifier = Modifier
                                    )
                                    /*Text(
                                    text = " $stepTwoSize",
                                    style = didiDetailItemStyle,
                                    textAlign = TextAlign.Start,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 12.sp,
                                    modifier = Modifier
                                )*/
                                }
                                if (stepTwoStatus == 2 || stepTwoStatus == 3)
                                    Icon(
                                        painter = painterResource(id = R.drawable.icon_check_green_without_border),
                                        contentDescription = null,
                                        tint = greenDark,
                                        modifier = Modifier
                                            .align(Alignment.CenterEnd)
                                            .size(24.dp)
                                            .padding(4.dp)
                                    )
                                else if (stepTwoStatus == 1) {
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .align(Alignment.CenterEnd)
                                            .padding(4.dp),
                                        color = textColorDark,
                                        strokeWidth = 1.dp
                                    )
                                }
                            }
                            Box(Modifier.fillMaxWidth()) {
                                Row(modifier = Modifier.align(Alignment.CenterStart)) {
                                    Text(
                                        text = stringResource(id = R.string.step_3) + ": ",
                                        style = didiDetailItemStyle,
                                        textAlign = TextAlign.Start,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier
                                    )
                                    Text(
                                        text = stringResource(id = R.string.particaptory_wealth_ranking_text),
                                        style = didiDetailItemStyle,
                                        textAlign = TextAlign.Start,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 12.sp,
                                        modifier = Modifier
                                    )
                                    /*Text(
                                    text = " $stepThreeSize",
                                    style = didiDetailItemStyle,
                                    textAlign = TextAlign.Start,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 12.sp,
                                    modifier = Modifier
                                )*/
                                }
                                if (stepThreeStatus == 2 || stepThreeStatus == 3)
                                    Icon(
                                        painter = painterResource(id = R.drawable.icon_check_green_without_border),
                                        contentDescription = null,
                                        tint = greenDark,
                                        modifier = Modifier
                                            .align(Alignment.CenterEnd)
                                            .size(24.dp)
                                            .padding(4.dp)
                                    )
                                else if (stepThreeStatus == 1) {
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .align(Alignment.CenterEnd)
                                            .padding(4.dp),
                                        color = textColorDark,
                                        strokeWidth = 1.dp
                                    )
                                }
                            }
                            Box(Modifier.fillMaxWidth()) {
                                Row(modifier = Modifier.align(Alignment.CenterStart)) {
                                    Text(
                                        text = stringResource(id = R.string.step_4) + ": ",
                                        style = didiDetailItemStyle,
                                        textAlign = TextAlign.Start,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier
                                    )
                                    Text(
                                        text = stringResource(id = R.string.pat_survey_title),
                                        style = didiDetailItemStyle,
                                        textAlign = TextAlign.Start,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 12.sp,
                                        modifier = Modifier
                                    )
                                    /*Text(
                                    text = " $stepFourSize",
                                    style = didiDetailItemStyle,
                                    textAlign = TextAlign.Start,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 12.sp,
                                    modifier = Modifier
                                )*/
                                }
                                if (stepFourStatus == 2 || stepFourStatus == 3)
                                    Icon(
                                        painter = painterResource(id = R.drawable.icon_check_green_without_border),
                                        contentDescription = null,
                                        tint = greenDark,
                                        modifier = Modifier
                                            .align(Alignment.CenterEnd)
                                            .size(24.dp)
                                            .padding(4.dp)
                                    )
                                else if (stepFourStatus == 1) {
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .align(Alignment.CenterEnd)
                                            .padding(4.dp),
                                        color = textColorDark,
                                        strokeWidth = 1.dp
                                    )
                                }
                            }
                            Box(Modifier.fillMaxWidth()) {

                                Row(modifier = Modifier.align(Alignment.CenterStart)) {
                                    Text(
                                        text = stringResource(id = R.string.step_5) + ": ",
                                        style = didiDetailItemStyle,
                                        textAlign = TextAlign.Start,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier
                                    )
                                    Text(
                                        text = stringResource(id = R.string.vo_endorsement),
                                        style = didiDetailItemStyle,
                                        fontSize = 12.sp,
                                        textAlign = TextAlign.Start,
                                        fontWeight = FontWeight.Normal,
                                        modifier = Modifier
                                    )
                                    /*Text(
                                    text = " $stepFiveSize",
                                    style = didiDetailItemStyle,
                                    textAlign = TextAlign.Start,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 12.sp,
                                    modifier = Modifier
                                )*/
                                }
                                if (stepFiveStatus == 2 || stepFiveStatus == 3)
                                    Icon(
                                        painter = painterResource(id = R.drawable.icon_check_green_without_border),
                                        contentDescription = null,
                                        tint = greenDark,
                                        modifier = Modifier
                                            .align(Alignment.CenterEnd)
                                            .size(24.dp)
                                            .padding(4.dp)
                                    )
                                else if (stepFiveStatus == 1) {
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .align(Alignment.CenterEnd)
                                            .padding(4.dp),
                                        color = textColorDark,
                                        strokeWidth = 1.dp
                                    )
                                }
                            }
                        }

                        Divider(thickness = 1.dp, color = greyBorder)
                        Spacer(modifier = Modifier.height(4.dp))

                        if (settingViewModel.showLoader.value) {
                            Canvas(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(24.dp)
                                    .padding(start = indicatorPadding, end = indicatorPadding)
                            ) {

                                // Background indicator
                                drawLine(
                                    color = backgroundIndicatorColor,
                                    cap = StrokeCap.Round,
                                    strokeWidth = size.height,
                                    start = Offset(x = 0f, y = 0f),
                                    end = Offset(x = size.width, y = 0f)
                                )

                                // Convert the downloaded percentage into progress (width of foreground indicator)
                                val progress =
                                    (animateNumber.value / 100) * size.width // size.width returns the width of the canvas

                                // Foreground indicator
                                drawLine(
                                    brush = Brush.linearGradient(
                                        colors = gradientColors
                                    ),
                                    cap = StrokeCap.Round,
                                    strokeWidth = size.height,
                                    start = Offset(x = 0f, y = 0f),
                                    end = Offset(x = progress, y = 0f)
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Top, modifier = Modifier
                                .fillMaxWidth()
                                .background(color = redBgLight, shape = RoundedCornerShape(6.dp))
                                .padding(10.dp)) {

                                Box(modifier = Modifier
                                    .absolutePadding(top = 4.dp)) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.info_icn),
                                        contentDescription = null,
                                        tint = redIconColor,
                                        modifier = Modifier
                                            .size(16.dp)
                                    )
                                }


                                Text(
                                    text = "Please don't close the app or switch off the phone.",
                                    style = numberStyle,
                                    textAlign = TextAlign.Start,
                                    fontSize = 12.sp,
                                    fontFamily = NotoSans,
                                    overflow = TextOverflow.Ellipsis,
                                    fontWeight = FontWeight.Normal,
                                    color = redMessageColor,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                )
                            }
                        }

                        if (isInternetConnected
                            && (batteryLevel > 30)
                            && !settingViewModel.showLoader.value
                            && isDataNeedToBeSynced.value == 0
                        ) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                ButtonNegative(
                                    buttonTitle = stringResource(id = R.string.cancel_tola_text),
                                    isArrowRequired = false,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    setShowDialog(false)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                ButtonPositive(
                                    buttonTitle = stringResource(id = R.string.sync),
                                    isArrowRequired = false,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(vertical = 2.dp)
                                ) {
                                    settingViewModel.showLoader.value = true
                                    settingViewModel.syncDataOnServer(context, showSyncDialogStatus)
                                    val updatedSyncTime = System.currentTimeMillis()
                                    settingViewModel.lastSyncTime.value = updatedSyncTime
                                    settingViewModel.prefRepo.savePref(LAST_SYNC_TIME, updatedSyncTime)
//                                setShowDialog(false)
//                                positiveButtonClicked()
                                }
                            }
                        } else if(isDataNeedToBeSynced.value == 1
                                || isDataNeedToBeSynced.value == 2
                                || !isInternetConnected
                                || batteryLevel < 30){
                            Row(modifier = Modifier.fillMaxWidth()) {
                                ButtonNegative(
                                    buttonTitle = stringResource(id = R.string.close),
                                    isArrowRequired = false,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    setShowDialog(false)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun showSyncInProgressDialog(
    setShowDialog: (Boolean) -> Unit,
    settingViewModel: SettingViewModel
) {
    Dialog(onDismissRequest = { setShowDialog(false) }) {
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = Color.White
        ) {
            Box(contentAlignment = Alignment.Center) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                    ) {
                        MainTitle(
                            stringResource(R.string.syncing),
                            Modifier
                                .weight(1f)
                                .fillMaxWidth(), align = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    GradientProgressbar(settingViewModel)
                }
            }
        }
    }
}

@Composable
fun GradientProgressbar(
    settingViewModel: SettingViewModel
) {
    val backgroundIndicatorColor = Color.LightGray.copy(alpha = 0.3f)
    val indicatorPadding = 24.dp
    val gradientColors = listOf(Color(0xFF2EE08E), Color(0xFF2EE08E))
    val numberStyle: TextStyle = mediumTextStyle
    val animationDuration = 1000
    val animationDelay = 0

    val syncPercentage: Float = settingViewModel.syncPercentage.value
    Log.e("sync", "->$syncPercentage")

    val animateNumber = animateFloatAsState(
        targetValue = syncPercentage,
        animationSpec = tween(
            durationMillis = animationDuration,
            delayMillis = animationDelay
        )
    )
    Box(contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.padding(1.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(Modifier.fillMaxWidth()) {
                Box(contentAlignment = Alignment.Center) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterStart)
                            .height(24.dp)
                            .padding(start = indicatorPadding, end = indicatorPadding)
                    ) {

                        // Background indicator
                        drawLine(
                            color = backgroundIndicatorColor,
                            cap = StrokeCap.Round,
                            strokeWidth = size.height,
                            start = Offset(x = 0f, y = 0f),
                            end = Offset(x = size.width, y = 0f)
                        )

                        // Convert the downloaded percentage into progress (width of foreground indicator)
                        val progress =
                            (animateNumber.value / 100) * size.width // size.width returns the width of the canvas

                        // Foreground indicator
                        drawLine(
                            brush = Brush.linearGradient(
                                colors = gradientColors
                            ),
                            cap = StrokeCap.Round,
                            strokeWidth = size.height,
                            start = Offset(x = 0f, y = 0f),
                            end = Offset(x = progress, y = 0f)
                        )

                    }
                    Text(
                        text = syncPercentage.toInt().toString() + "%",
                        style = numberStyle,
                        textAlign = TextAlign.Start,
                        fontSize = 14.sp,
                        fontFamily = NotoSans,
                        fontWeight = FontWeight.SemiBold,
                        color = textColorDark,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Time Remaining : 2 mins",
                    style = numberStyle,
                    textAlign = TextAlign.Start,
                    fontSize = 11.sp,
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.SemiBold,
                    color = textColorDark,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Text(
                    text = "Please don't close the app or switch off the phone.",
                    style = numberStyle,
                    textAlign = TextAlign.Start,
                    fontSize = 12.sp,
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.SemiBold,
                    color = textColorDark,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Log.e("sync", "new ->$syncPercentage")
            }
        }
    }
}

@Composable
fun SettingCard(
    title: String,
    subTitle: String,
    expanded: Boolean,
    showArrow: Boolean = false,
    formList: List<String>,
    navController: NavController,
    onClick: () -> Unit
) {

    val transition = updateTransition(expanded, label = "transition")

    val animateInt by transition.animateInt({
        tween(durationMillis = 10)
    }, label = "animate float") {
        if (it) 1 else 0
    }

    val arrowRotationDegree by transition.animateFloat({
        tween(durationMillis = EXPANSTION_TRANSITION_DURATION)
    }, label = "rotationDegreeTransition") {
        if (it) 0f else -90f
    }

    Column(modifier = Modifier
        .background(Color.White)
        .fillMaxWidth()
        .clickable {
            onClick()
        }) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(id = R.dimen.dp_20))
                .padding(vertical = dimensionResource(id = R.dimen.dp_15))
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = title,
                    textAlign = TextAlign.Start,
                    fontSize = 16.sp,
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.SemiBold,
                    color = textColorDark,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
                if (showArrow) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_keyboard_arrow_down_24),
                        contentDescription = null,
                        tint = textColorDark,
                        modifier = Modifier.rotate(arrowRotationDegree)
                    )
                }
            }
            if (!subTitle.isNullOrEmpty()) {
                Text(
                    text = subTitle,
                    textAlign = TextAlign.Start,
                    fontSize = 13.sp,
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.Normal,
                    color = black100Percent,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        ExpandedSettingsList(
            modifier = Modifier,
            expanded = animateInt == 1,
            formList = formList,
            navController = navController
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(id = R.dimen.dp_2))
                .background(borderGreyLight)
        )

    }
}

@Composable
fun ExpandedSettingsList(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    formList: List<String>,
    navController: NavController
) {
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

    val interactionSource = remember { MutableInteractionSource() }

    AnimatedVisibility(
        visible = expanded,
        enter = enterTransition,
        exit = exitTransition,
        modifier = Modifier.then(modifier)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
        ) {
            if (formList.isNotEmpty()) {
                formList.forEachIndexed { index, name ->

                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Text(
                            text = name,
                            textAlign = TextAlign.Start,
                            fontSize = 14.sp,
                            fontFamily = NotoSans,
                            fontWeight = FontWeight.SemiBold,
                            color = textColorDark,
                            modifier = Modifier
                                .padding(horizontal = 26.dp)
                                .padding(top = if (index == 0) 0.dp else 8.dp, bottom = 8.dp)
                                .fillMaxWidth()
                                .indication(
                                    interactionSource = interactionSource,
                                    indication = rememberRipple(
                                        bounded = true,
                                        color = Color.Black
                                    )
                                )
                                .clickable {
                                    when (index) {
                                        0 -> {
                                            navController.navigate(SettingScreens.FORM_A_SCREEN.route)
                                        }

                                        1 -> {
                                            navController.navigate(SettingScreens.FORM_B_SCREEN.route)
                                        }

                                        2 -> {
                                            navController.navigate(SettingScreens.FORM_C_SCREEN.route)
                                        }
                                    }

                                })

                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_keyboard_arrow_down_24),
                            contentDescription = null,
                            tint = textColorDark,
                            modifier = Modifier.rotate(-90f)
                        )
                    }
                    if (index < formList.size - 1)
                        Divider(
                            color = borderGreyLight,
                            thickness = 1.dp,
                            modifier = Modifier.padding(horizontal = 26.dp)
                        )
                }
            } else {
                Text(
                    text = "No Form available yet.",
                    textAlign = TextAlign.Start,
                    fontSize = 14.sp,
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.SemiBold,
                    color = textColorDark,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(top = 8.dp, bottom = 8.dp)
                        .fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(15.dp))
        }
    }
}
