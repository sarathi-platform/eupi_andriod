package com.patsurvey.nudge.activities

import android.content.Context
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import com.patsurvey.nudge.BuildConfig
import com.patsurvey.nudge.R
import com.patsurvey.nudge.RetryHelper
import com.patsurvey.nudge.activities.ui.progress.VillageSelectionViewModel
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.activities.ui.theme.dropDownBg
import com.patsurvey.nudge.activities.ui.theme.greenLight
import com.patsurvey.nudge.activities.ui.theme.greenOnline
import com.patsurvey.nudge.activities.ui.theme.greyRadioButton
import com.patsurvey.nudge.activities.ui.theme.smallerTextStyle
import com.patsurvey.nudge.activities.ui.theme.stepBoxActiveColor
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.activities.ui.theme.white
import com.patsurvey.nudge.customviews.CustomSnackBarShow
import com.patsurvey.nudge.customviews.CustomSnackBarViewPosition
import com.patsurvey.nudge.customviews.SearchWithFilterView
import com.patsurvey.nudge.customviews.rememberSnackBarState
import com.patsurvey.nudge.navigation.AuthScreen
import com.patsurvey.nudge.utils.ApiType
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.BlueButtonWithIconWithFixedWidthWithoutIcon
import com.patsurvey.nudge.utils.ButtonPositive
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.PREF_KEY_TYPE_NAME
import com.patsurvey.nudge.utils.PageFrom
import com.patsurvey.nudge.utils.StepStatus
import com.patsurvey.nudge.utils.showCustomDialog
import com.patsurvey.nudge.utils.showCustomToast

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun VillageSelectionScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: VillageSelectionViewModel,
    onNavigateToSetting:()->Unit
) {
    val context = LocalContext.current

    LaunchedEffect(key1 = Unit) {
        viewModel.compareWithPreviousUser(context = context)
    }

    val villages by viewModel.filterVillageList.collectAsState()

    val snackState = rememberSnackBarState()

    if (viewModel.networkErrorMessage.value.isNotEmpty()) {
        if (BuildConfig.DEBUG)
            showCustomToast(context, viewModel.networkErrorMessage.value)
        viewModel.networkErrorMessage.value = BLANK_STRING
    }

    BackHandler {
        viewModel.showAppExitDialog.value = true
    }
    if(viewModel.showAppExitDialog.value){
        showCustomDialog(
            title = stringResource(id = R.string.are_you_sure),
            message =stringResource(id = R.string.do_you_want_to_exit_the_app),
            positiveButtonTitle = stringResource(id = R.string.exit),
            negativeButtonTitle = stringResource(id = R.string.cancel),
            onNegativeButtonClick = {viewModel.showAppExitDialog.value =false},
            onPositiveButtonClick = {
                (context as? MainActivity)?.finish()
            })
    }
    if (viewModel.showUserChangedDialog.value) {
        showCustomDialog(
            title = stringResource(id = R.string.warning),
            message = stringResource(id = R.string.data_lost_message),
            positiveButtonTitle = stringResource(id = R.string.proceed),
            negativeButtonTitle = stringResource(id = R.string.cancel),
            dismissOnBackPress = false,
            onNegativeButtonClick = {
                viewModel.showUserChangedDialog.value = false
                viewModel.logout()
                navController.navigate(AuthScreen.LOGIN.route)
            },
            onPositiveButtonClick = {

                viewModel.clearLocalDB(context = context)
                viewModel.showUserChangedDialog.value = false
            })
    }

    LaunchedEffect(key1 = true) {
        val imagesList= (context as MainActivity).quesImageList
        if(imagesList.isNotEmpty()){
            imagesList.forEach {
                viewModel.downloadImageItem(context,it)
            }
        }
        viewModel.saveVideosToDb(context)
    }
    val showRetryLoader = remember {
        mutableStateOf(false)
    }

    val pullRefreshState = rememberPullRefreshState(
        viewModel.showLoader.value,
        {
            if ((context as MainActivity).isOnline.value ?: false) {
                if (viewModel.prefRepo.isUserBPC()) viewModel.refreshBpcData(context) else viewModel.refreshCrpData(context)
            } else {
                showCustomToast(context, context.getString(R.string.refresh_failed_please_try_again))
            }

        })

    if (viewModel.showLoader.value) {
        Scaffold(
            modifier = Modifier,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.seletc_village_screen_text),
                            fontFamily = NotoSans,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 24.sp, color = textColorDark,
                            modifier = Modifier.fillMaxWidth()
                        )

                    },
                    actions = {
                        IconButton(onClick = {
                            viewModel.prefRepo.saveSettingOpenFrom(PageFrom.VILLAGE_PAGE.ordinal)
//                            viewModel.prefRepo.savePref(PREF_OPEN_FROM_HOME,true)
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
                    backgroundColor = White,
                    elevation = 10.dp
                )
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(top = 30.dp + it.calculateTopPadding())
            ) {
                CircularProgressIndicator(
                    color = blueDark,
                    modifier = Modifier
                        .size(28.dp)
                        .align(Alignment.Center)
                )
            }
        }
    } else {
        Scaffold(
            modifier = Modifier,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.seletc_village_screen_text),
                            fontFamily = NotoSans,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 24.sp, color = textColorDark,
                            modifier = Modifier.fillMaxWidth()
                        )

                    },
                    actions = {
                        IconButton(onClick = {
                            viewModel.prefRepo.saveSettingOpenFrom(PageFrom.VILLAGE_PAGE.ordinal)
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
                    backgroundColor = White,
                    elevation = 10.dp
                )
            }
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(it)
                    .pullRefresh(pullRefreshState)
            ) {
                if (RetryHelper.retryApiList.contains(ApiType.VILLAGE_LIST_API)) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                            .align(Alignment.TopCenter)
                            .fillMaxWidth()
                            .then(modifier)
                    ) {

                        Row{
                            if (showRetryLoader.value) {
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
                            BlueButtonWithIconWithFixedWidthWithoutIcon(
                                modifier = Modifier,
                                buttonText = stringResource(id = R.string.click_to_refresh),
                                onClick = {
                                    viewModel.showLoader.value = true
                                    showRetryLoader.value = true

                                    RetryHelper.retryVillageListApi() { success, villageList ->
                                        if (success && !villageList?.isNullOrEmpty()!!) {
                                            viewModel.saveVillageListAfterTokenRefresh(villageList)
                                        }
                                        showRetryLoader.value = false
                                        NudgeLogger.d(
                                            "VillageSelectionScreen",
                                            "click_to_refresh onClick -> viewModel.showLoader.value = false"
                                        )
                                        viewModel.showLoader.value = false
                                    }
                                }
                            )
                        }
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                            .align(Alignment.TopCenter)
                            .fillMaxWidth()
                            .then(modifier)
                    ) {

                        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
//                item { Spacer(modifier = Modifier.height(4.dp)) }
                            item {
                                SearchWithFilterView(
                                    placeholderString = stringResource(id = R.string.search_village),
                                    filterSelected = false,
                                    showFilter = false,
                                    onFilterSelected = {
                                    },
                                    onSearchValueChange = {
                                        viewModel.performQuery(it)
                                    }
                                )
                            }
                            NudgeLogger.d("Village_UI_LIST","$villages :: ${villages.size}")
                            itemsIndexed(villages.distinctBy { it.id }) { index, village ->
                                VillageAndVoBoxForBottomSheet(
                                    tolaName = village.name,
                                    voName = village.federationName,
                                    index = index,
                                    selectedIndex = viewModel.villageSelected.value,
                                    isUserBPC = if (villages.isNotEmpty()) viewModel.prefRepo.isUserBPC() else false,
                                    isVoEndorsementComplete = (if(!viewModel.prefRepo.isUserBPC()) viewModel.isVoEndorsementComplete.value[village.id] else true)
                                        ?: false,
                                    stepId = village.stepId,
                                    statusId = village.statusId,
                                    context = context
                                ) {
                                    NudgeLogger.d("VillageAndVoBoxForBottomSheet","id = $it")
                                    viewModel.villageSelected.value = it
                                    viewModel.updateSelectedVillage(villages)
                                }
                            }
                            item { Spacer(modifier = Modifier.height(50.dp)) }
                        }
//                    }
                        CustomSnackBarShow(
                            state = snackState,
                            position = CustomSnackBarViewPosition.Bottom
                        )
                    }
                }

                if (villages.isNotEmpty() && !viewModel.showLoader.value) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(White)
                            .padding(horizontal = dimensionResource(id = R.dimen.padding_16dp))
                            .padding(bottom = 16.dp)
                            .align(Alignment.BottomCenter)
                    ) {
                        ButtonPositive(
                            buttonTitle = stringResource(id = R.string.continue_text),
                            isArrowRequired = false,
                            isActive = villages.isNotEmpty()
                        ) {
                            if (viewModel.prefRepo.isUserBPC()) {
                               val stepId= villages[viewModel.villageSelected.value].stepId
                               val statusId= villages[viewModel.villageSelected.value].statusId
                                when (fetchBorderColorForVillage(stepId, statusId)) {
                                    0,2 -> showCustomToast(context,  context.getString(R.string.village_is_not_vo_endorsed_right_now))
                                    else -> {
                                        viewModel.updateSelectedVillage(villageList = villages)
                                        navController.popBackStack()
                                        navController.navigate(
                                            "home_graph/${
                                                viewModel.prefRepo.getPref(
                                                    PREF_KEY_TYPE_NAME, ""
                                                ) ?: ""
                                            }"
                                        )
                                    }
                                }
                            } else {
                                viewModel.updateSelectedVillage(villageList = villages)
                                navController.popBackStack()
                                navController.navigate(
                                    "home_graph/${
                                        viewModel.prefRepo.getPref(
                                            PREF_KEY_TYPE_NAME, ""
                                        ) ?: ""
                                    }"
                                )
                            }

                        }
                    }
                }
                PullRefreshIndicator(
                    refreshing = viewModel.showLoader.value,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter),
                    contentColor = blueDark,
                )
            }
        }
    }
}

@Composable
fun VillageAndVoBoxForBottomSheet(
    modifier: Modifier = Modifier,
    context: Context,
    tolaName: String = "",
    voName: String = "",
    index: Int,
    isUserBPC:Boolean,
    isVoEndorsementComplete:Boolean =false,
    selectedIndex: Int,
    statusId:Int=0,
    stepId:Int=0,
    onVillageSeleted: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (fetchBorderColorForVillage(stepId, statusId) == 4) greenOnline else {
                    if (index == selectedIndex) blueDark else greyRadioButton
                },
                shape = RoundedCornerShape(6.dp)
            )
            .clip(RoundedCornerShape(6.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    bounded = true, color = Black
                )
            ) {
                if (isUserBPC) {
                    when (fetchBorderColorForVillage(stepId, statusId)) {
                        0, 2 -> showCustomToast(
                            context,
                            context.getString(R.string.village_is_not_vo_endorsed_right_now)
                        )

                        else -> onVillageSeleted(index)
                    }
                } else onVillageSeleted(index)


            }
            .background(if (index == selectedIndex) dropDownBg else White)
            .then(modifier),
        elevation = 10.dp
    ) {
        Column {
            Column(
                modifier = Modifier
                    .background(
                        if (isUserBPC) {
                            when (fetchBorderColorForVillage(stepId, statusId)) {
                                0, 2 -> white
                                1, 3 -> stepBoxActiveColor
                                4 -> greenLight
                                else -> white
                            }
                        } else if (index == selectedIndex) dropDownBg else White
                    )
                    .alpha(
                        if (isUserBPC) {
                            when (fetchBorderColorForVillage(stepId, statusId)) {
                                0 -> .5f
                                else -> 1f
                            }
                        } else 1f
                    )
                    .padding(vertical = 4.dp)
                    .fillMaxWidth()
            ) {
                ConstraintLayout(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, top = 10.dp)
                        .fillMaxWidth()
                ) {
                    val (iconRef, textRef, radioRef) = createRefs()
                    Icon(
                        painter = painterResource(id = R.drawable.home_icn),
                        contentDescription = null,
                        tint = if (fetchBorderColorForVillage(stepId, statusId) == 4) greenOnline else textColorDark,
                        modifier = Modifier.constrainAs(iconRef) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                        }
                    )
                    Text(
                        text = " $tolaName",
                        color = if (fetchBorderColorForVillage(stepId, statusId) == 4) greenOnline else textColorDark,
                        fontSize = 14.sp,
                        fontFamily = NotoSans,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.constrainAs(textRef) {
                            top.linkTo(parent.top)
                            start.linkTo(iconRef.end)
                            end.linkTo(radioRef.start)
                            width = Dimension.fillToConstraints
                        }
                    )

                    Canvas(
                        modifier = Modifier
                            .constrainAs(radioRef) {
                                top.linkTo(textRef.top)
                                end.linkTo(parent.end)
                                bottom.linkTo(textRef.bottom)
                            }
                            .size(size = 20.dp)
                            .border(
                                width = 1.dp,
                                color = if (index == selectedIndex) blueDark else greyRadioButton,
                                shape = CircleShape
                            )
                            .padding(3.dp)

                    ) {
                        drawCircle(
                            color = if (index == selectedIndex) blueDark else White,
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .absolutePadding(left = 4.dp)
                        .padding(start = 16.dp, end = 16.dp)
                ) {
                    Text(
                        text = "VO: ",
                        modifier = Modifier,
                        color = if (fetchBorderColorForVillage(stepId, statusId) == 4) greenOnline else textColorDark,
                        fontSize = 14.sp,
                        fontFamily = NotoSans,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = voName,
                        modifier = Modifier
                            .fillMaxWidth(),
                        color = if (fetchBorderColorForVillage(stepId, statusId) == 4) greenOnline else textColorDark,
                        fontSize = 14.sp,
                        fontFamily = NotoSans,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            if(!isUserBPC && isVoEndorsementComplete){
                Row(
                    Modifier
                        .background(
                            greenOnline,
                            shape = RoundedCornerShape(bottomStart = 6.dp, bottomEnd = 6.dp)
                        )
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.icon_feather_check_circle_white),
                        contentDescription = null,
                        tint = white
                    )
                    Text(
                        text = stringResource(R.string.vo_endorsement_completed_village_banner_text),
                        color = white,
                        style = smallerTextStyle,
                        modifier = Modifier.absolutePadding(bottom = 3.dp)
                    )
                }
            }
            else if ((stepId == 44 && statusId == StepStatus.COMPLETED.ordinal) || stepId == 45) {
                    Row(
                        Modifier
                            .background(
                                when (fetchBorderColorForVillage(stepId, statusId)) {
                                    0, 2 -> white
                                    1, 3 -> stepBoxActiveColor
                                    4 -> greenLight
                                    else -> white
                                }, shape = RoundedCornerShape(bottomStart = 6.dp, bottomEnd = 6.dp)
                            )
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Log.d("TAG", "VillageAndVoBoxForBottomSheet IconCaes:  ${fetchBorderColorForVillage(stepId,statusId)}")
                        if(fetchBorderColorForVillage(stepId, statusId) !=2) {
                            Icon(
                                painter = painterResource(id = R.drawable.icon_feather_check_circle_white),
                                contentDescription = null,
                                tint = if (fetchBorderColorForVillage(
                                        stepId,
                                        statusId
                                    ) == 4
                                ) greenOnline else blueDark
                            )
                        }
                        if(fetchBorderColorForVillage(stepId, statusId)==2){
                            Text(
                                text = stringResource(id = R.string.vo_endorsement_not_started),
                                color = textColorDark,
                                style = smallerTextStyle,
                                modifier = Modifier
                                    .absolutePadding(bottom = 3.dp, left = 16.dp)
                            )
                        }else {
                            Text(
                                text = stringResource(
                                    if (stepId == 44) R.string.vo_endorsement_completed_village_banner_text else {
                                        if (statusId == StepStatus.COMPLETED.ordinal) R.string.bpc_verification_completed_village_banner_text else R.string.vo_endorsement_completed_village_banner_text
                                    }
                                ),
                                color = if (fetchBorderColorForVillage(
                                        stepId,
                                        statusId
                                    ) == 4
                                ) greenOnline else textColorDark,
                                style = smallerTextStyle,
                                modifier = Modifier.absolutePadding(bottom = 3.dp)
                            )
                        }
                    }
            } else {

                Spacer(modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isUserBPC) 5.dp else 10.dp))
                if(isUserBPC){
                Text(
                    text = stringResource(id = R.string.vo_endorsement_not_started),
                    color = textColorDark,
                    style = smallerTextStyle,
                    modifier = Modifier
                        .absolutePadding(bottom = 3.dp, left = 16.dp)
                        .alpha(.5f)
                ) }
            }

        }
    }
}

private fun fetchBorderColorForVillage(stepId: Int,statusId: Int) :Int{
    Log.d("TAG", "fetchBorderColorForVillage: stepId: $stepId :: statusId: ${StepStatus.getStepFromOrdinal(statusId)}")
    return if (stepId == 44 && (statusId == StepStatus.INPROGRESS.ordinal
                || statusId == StepStatus.COMPLETED.ordinal)) {
        1
    } else if (stepId == 45 && statusId == StepStatus.NOT_STARTED.ordinal) {
        2
    } else if (stepId == 45 && statusId == StepStatus.INPROGRESS.ordinal) {
        3
    } else if (stepId == 45 && statusId == StepStatus.COMPLETED.ordinal) {
        4
    } else 0
}
