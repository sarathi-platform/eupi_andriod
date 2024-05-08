package com.patsurvey.nudge.activities.ui.bpc.bpc_village_screen

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
import androidx.compose.material.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nudge.navigationmanager.graphs.AuthScreen
import com.nudge.navigationmanager.graphs.NudgeNavigationGraph
import com.patsurvey.nudge.BuildConfig
import com.patsurvey.nudge.MyApplication
import com.patsurvey.nudge.R
import com.patsurvey.nudge.RetryHelper
import com.patsurvey.nudge.activities.MainActivity
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.activities.ui.theme.dropDownBg
import com.patsurvey.nudge.activities.ui.theme.greyLightBgColor
import com.patsurvey.nudge.activities.ui.theme.greyRadioButton
import com.patsurvey.nudge.activities.ui.theme.smallerTextStyle
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.activities.ui.theme.white
import com.patsurvey.nudge.customviews.CustomSnackBarShow
import com.patsurvey.nudge.customviews.CustomSnackBarViewPosition
import com.patsurvey.nudge.customviews.SearchWithFilterView
import com.patsurvey.nudge.customviews.rememberSnackBarState
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.utils.ApiType
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.BPCVillageStatus
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
fun BpcVillageSelectionScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: BpcVillageScreenViewModel = hiltViewModel(),
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
            message = stringResource(id = R.string.do_you_want_to_exit_the_app),
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
            if ((context as MainActivity).isOnline.value) {
                viewModel.refreshVillageData(context)
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
                            viewModel.saveSettingOpenFrom(PageFrom.VILLAGE_PAGE.ordinal)
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
                    backgroundColor = Color.White,
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
                            viewModel.saveSettingOpenFrom(PageFrom.VILLAGE_PAGE.ordinal)
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
                    backgroundColor = Color.White,
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
                                BpcVillageAndVoBoxForBottomSheet(
                                    context = context,
                                    villageEntity = village,
                                    index = index,
                                    selectedIndex = viewModel.villageSelected.value,
                                ) {
                                    NudgeLogger.d("BpcVillageAndVoBoxForBottomSheet","id = $it")
                                    viewModel.villageSelected.value = it
                                    viewModel.updateSelectedVillage(villages)
                                }
                            }
                            item { Spacer(modifier = Modifier.height(80.dp)) }
                        }
//                    }
                        CustomSnackBarShow(
                            state = snackState,
                            position = CustomSnackBarViewPosition.Bottom
                        )
                    }
                }

                if (villages.isNotEmpty() && !viewModel.showLoader.value) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(horizontal = dimensionResource(id = R.dimen.padding_16dp))
                            .padding(vertical = 16.dp)
                            .align(Alignment.BottomCenter),
                        elevation = 10.dp
                    ) {
                        ButtonPositive(
                            buttonTitle = stringResource(id = R.string.continue_text),
                            isArrowRequired = false,
                            isActive = villages.isNotEmpty()
                        ) {
                            if (viewModel.isUserBpc()) {
                                val stepId= villages[viewModel.villageSelected.value].stepId
                                val statusId= villages[viewModel.villageSelected.value].statusId
                                when (fetchBPCVillageStatus(stepId, statusId)) {
                                    BPCVillageStatus.VO_ENDORSEMENT_NOT_STARTED.ordinal, BPCVillageStatus.VO_ENDORSEMENT_IN_PROGRESS.ordinal -> showCustomToast(
                                        context,
                                        context.getString(R.string.village_is_not_vo_endorsed_right_now)
                                    )
                                    else -> {
                                        viewModel.updateSelectedVillage(villageList = villages)
                                        navController.popBackStack()
                                        navController.navigate(NudgeNavigationGraph.HOME)
                                    }
                                }
                            } else {
                                viewModel.updateSelectedVillage(villageList = villages)
                                navController.popBackStack()
                                navController.navigate(NudgeNavigationGraph.HOME)
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
fun BpcVillageAndVoBoxForBottomSheet(
    modifier: Modifier = Modifier,
    context: Context,
    villageEntity: VillageEntity,
    index: Int,
    selectedIndex: Int,
    onVillageSeleted: (Int) -> Unit
) {
    val bpcVillageStatus = fetchBPCVillageStatus(
        villageEntity.stepId,
        villageEntity.statusId
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (index == selectedIndex) blueDark else greyRadioButton,
                shape = RoundedCornerShape(6.dp)
            )
            .clip(RoundedCornerShape(6.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    bounded = true,
                    color = Color.Black
                )
            ) {
                when (bpcVillageStatus) {
                    BPCVillageStatus.VO_ENDORSEMENT_NOT_STARTED.ordinal, BPCVillageStatus.VO_ENDORSEMENT_IN_PROGRESS.ordinal -> showCustomToast(
                        context,
                        context.getString(R.string.village_is_not_vo_endorsed_right_now)
                    )

                    else -> onVillageSeleted(index)
                }
            }
            .background(if (index == selectedIndex) dropDownBg else Color.White)
            .then(modifier),
        elevation = 10.dp
    ) {
        Column {
            Column(
                modifier = Modifier
                    .background(
                        when (bpcVillageStatus) {
                            BPCVillageStatus.VO_ENDORSEMENT_NOT_STARTED.ordinal, BPCVillageStatus.VO_ENDORSEMENT_IN_PROGRESS.ordinal -> greyLightBgColor
                            BPCVillageStatus.VO_ENDORSEMENT_COMPLETED.ordinal, BPCVillageStatus.BPC_VERIFICATION_NOT_STARTED.ordinal, BPCVillageStatus.BPC_VERIFICATION_IN_PROGRESS.ordinal -> white
                            BPCVillageStatus.BPC_VERIFICATION_COMPLETED.ordinal -> white
                            else -> white
                        }
                    )
                    .alpha(
                        when (bpcVillageStatus) {
                            BPCVillageStatus.VO_ENDORSEMENT_NOT_STARTED.ordinal, BPCVillageStatus.VO_ENDORSEMENT_IN_PROGRESS.ordinal -> .5f
                            else -> 1f
                        }
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
                        tint = textColorDark,
                        modifier = Modifier.constrainAs(iconRef) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                        }
                    )
                    Text(
                        text = " ${villageEntity.name}",
                        color = textColorDark,
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
                            color = if (index == selectedIndex) blueDark else Color.White,
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
                        color = textColorDark,
                        fontSize = 14.sp,
                        fontFamily = NotoSans,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = villageEntity.federationName,
                        modifier = Modifier
                            .fillMaxWidth(),
                        color = textColorDark,
                        fontSize = 14.sp,
                        fontFamily = NotoSans,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            if ((villageEntity.stepId == 44 && villageEntity.statusId == StepStatus.COMPLETED.ordinal) || villageEntity.stepId == 45) {
                Row(
                    Modifier
                        .background(
                            when (bpcVillageStatus) {
                                BPCVillageStatus.VO_ENDORSEMENT_NOT_STARTED.ordinal, BPCVillageStatus.VO_ENDORSEMENT_IN_PROGRESS.ordinal -> greyLightBgColor
                                BPCVillageStatus.VO_ENDORSEMENT_COMPLETED.ordinal, BPCVillageStatus.BPC_VERIFICATION_NOT_STARTED.ordinal, BPCVillageStatus.BPC_VERIFICATION_IN_PROGRESS.ordinal -> white
                                BPCVillageStatus.BPC_VERIFICATION_COMPLETED.ordinal -> white
                                else -> white
                            },
                            shape = RoundedCornerShape(bottomStart = 6.dp, bottomEnd = 6.dp)
                        )
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Log.d("TAG", "VillageAndVoBoxForBottomSheet IconCaes:  ${bpcVillageStatus}")
                    if (bpcVillageStatus > BPCVillageStatus.VO_ENDORSEMENT_IN_PROGRESS.ordinal) {
                        Icon(
                            painter = painterResource(id = R.drawable.icon_feather_check_circle_white),
                            contentDescription = null,
                            tint = blueDark
                        )
                    }
                    if (bpcVillageStatus < BPCVillageStatus.VO_ENDORSEMENT_COMPLETED.ordinal) {
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
                                if (villageEntity.stepId == 44) R.string.vo_endorsement_completed_village_banner_text else {
                                    if (villageEntity.statusId == StepStatus.COMPLETED.ordinal) R.string.bpc_verification_completed_village_banner_text
                                    else if (villageEntity.statusId == StepStatus.INPROGRESS.ordinal) R.string.bpc_verification_in_progress_village_banner_text
                                    else R.string.vo_endorsement_completed_village_banner_text
                                }
                            ),
                            color = textColorDark,
                            style = smallerTextStyle,
                            modifier = Modifier.absolutePadding(bottom = 3.dp)
                        )
                    }
                }
            } else {

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                )
                Text(
                    text = stringResource(id = R.string.vo_endorsement_not_started),
                    color = textColorDark,
                    style = smallerTextStyle,
                    modifier = Modifier
                        .absolutePadding(bottom = 3.dp, left = 16.dp)
                        .alpha(.5f)
                )
            }
        }
    }
}


@Preview
@Composable
fun BpcVillageAndVoBoxForBottomSheetPreview(
    modifier: Modifier = Modifier
) {

    val villageEntity = VillageEntity(
        localVillageId = 1,
        id = 62074,
        name = "SALBARIBHURPAR",
        federationName = "Milijuli VO",
        stateId = 4,
        languageId = 2,
        steps_completed = null,
        stepId = 44,
        statusId = StepStatus.INPROGRESS.ordinal
    )

    Surface(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)) {
        BpcVillageAndVoBoxForBottomSheet(
            context = MyApplication.applicationContext(),
            villageEntity = villageEntity,
            index = 1,
            selectedIndex = 1,
            onVillageSeleted =  {

            }
        )
    }
}

private fun fetchBPCVillageStatus(stepId: Int, statusId: Int): Int {
    return if (stepId == 44 && statusId == StepStatus.NOT_STARTED.ordinal) {
        BPCVillageStatus.VO_ENDORSEMENT_NOT_STARTED.ordinal
    } else if (stepId == 44 && statusId == StepStatus.INPROGRESS.ordinal) {
        BPCVillageStatus.VO_ENDORSEMENT_IN_PROGRESS.ordinal
    } else if (stepId == 44 && statusId == StepStatus.COMPLETED.ordinal) {
        BPCVillageStatus.VO_ENDORSEMENT_COMPLETED.ordinal
    } else if (stepId == 45 && statusId == StepStatus.NOT_STARTED.ordinal) {
        BPCVillageStatus.BPC_VERIFICATION_NOT_STARTED.ordinal
    } else if (stepId == 45 && statusId == StepStatus.INPROGRESS.ordinal) {
        BPCVillageStatus.BPC_VERIFICATION_IN_PROGRESS.ordinal
    } else if (stepId == 45 && statusId == StepStatus.COMPLETED.ordinal) {
        BPCVillageStatus.BPC_VERIFICATION_COMPLETED.ordinal
    } else 0
}
