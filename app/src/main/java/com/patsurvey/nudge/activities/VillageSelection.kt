package com.patsurvey.nudge.activities

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
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import com.patsurvey.nudge.activities.ui.theme.greenOnline
import com.patsurvey.nudge.activities.ui.theme.greyBorder
import com.patsurvey.nudge.activities.ui.theme.greyRadioButton
import com.patsurvey.nudge.activities.ui.theme.smallerTextStyle
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.activities.ui.theme.white
import com.patsurvey.nudge.customviews.CustomSnackBarShow
import com.patsurvey.nudge.customviews.CustomSnackBarViewPosition
import com.patsurvey.nudge.customviews.rememberSnackBarState
import com.patsurvey.nudge.utils.ApiType
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.BlueButtonWithIconWithFixedWidthWithoutIcon
import com.patsurvey.nudge.utils.ButtonPositive
import com.patsurvey.nudge.utils.PREF_KEY_TYPE_NAME
import com.patsurvey.nudge.utils.PageFrom
import com.patsurvey.nudge.utils.showCustomToast

@Composable
fun VillageSelectionScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: VillageSelectionViewModel,
    onNavigateToSetting:()->Unit
) {
    LaunchedEffect(key1 = Unit) {
        viewModel.init()
    }

    val villages by viewModel.villageList.collectAsState()

    val snackState = rememberSnackBarState()

    val context = LocalContext.current
    var showToast by remember { mutableStateOf(false) }
    if (viewModel.networkErrorMessage.value.isNotEmpty()) {
        if (BuildConfig.DEBUG)
            showCustomToast(context, viewModel.networkErrorMessage.value)
        viewModel.networkErrorMessage.value = BLANK_STRING
    }

    BackHandler {
        (context as? MainActivity)?.finish()
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
            Modifier
                .fillMaxSize()
                .padding(it)) {
            if (RetryHelper.retryApiList.contains(ApiType.VILLAGE_LIST_API) || villages.isEmpty()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .then(modifier)
                ) {
                    /*Text(
                        text = stringResource(R.string.seletc_village_screen_text),
                        fontFamily = NotoSans,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 24.sp, color = textColorDark,
                        modifier = Modifier
                    )*/

                    Row() {
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

                                RetryHelper.retryVillageListApi(viewModel.multiVillageRequest.value) { success, villageList ->
                                    if (success && !villageList?.isNullOrEmpty()!!) {
                                        viewModel.saveVillageListAfterTokenRefresh(villageList)
                                    }
                                    showRetryLoader.value = false
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

                    /*Row(modifier = Modifier
                        .padding(start = 16.dp, top = 12.dp)
                        .fillMaxWidth()) {
                        Text(
                            text = stringResource(R.string.seletc_village_screen_text),
                            fontFamily = NotoSans,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 24.sp, color = textColorDark,
                            modifier = Modifier.weight(1f)
                        )
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
                    }*/

                    if (viewModel.showLoader.value) {
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

                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
//                item { Spacer(modifier = Modifier.height(4.dp)) }
                            itemsIndexed(villages) { index, village ->
                                VillageAndVoBoxForBottomSheet(
                                    tolaName = village.name,
                                    voName = village.federationName,
                                    index = index,
                                    selectedIndex = viewModel.villageSelected.value,
                                    isBpcUser = if (villages.isNotEmpty()) viewModel.prefRepo.isUserBPC() else false,
                                    isVoEndorsementComplete = viewModel.isVoEndorsementComplete.value[village.id] ?: false
                                ) {
                                    viewModel.villageSelected.value = it
                                    viewModel.updateSelectedVillage()
                                }
                            }
                            item { Spacer(modifier = Modifier.height(16.dp)) }
                        }
                    }
                    CustomSnackBarShow(state = snackState, position = CustomSnackBarViewPosition.Bottom)
                }
            }

            if (villages.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimensionResource(id = R.dimen.padding_16dp))
                        .padding(bottom = 16.dp)
                        .align(Alignment.BottomCenter)
                ) {
                    ButtonPositive(
                        buttonTitle = stringResource(id = R.string.continue_text),
                        isArrowRequired = false,
                        isActive = villages.isNotEmpty()
                    ) {
                        viewModel.updateSelectedVillage()
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
    }


}

@Composable
fun VillageAndVoBox(
    tolaName: String = "",
    voName: String = "",
    index: Int,
    selectedIndex: Int,
    modifier: Modifier = Modifier,
    onVillageSeleted: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (index == selectedIndex) blueDark else greyBorder,
                shape = RoundedCornerShape(6.dp)
            )
            .shadow(
                elevation = 16.dp,
                ambientColor = White,
                spotColor = Black,
                shape = RoundedCornerShape(6.dp),
            )
            .clip(RoundedCornerShape(6.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    bounded = true,
                    color = Black
                )
            ) {
                onVillageSeleted(index)
            }
            .background(if (index == selectedIndex) blueDark else White)
            .then(modifier),
        elevation = 10.dp
    ) {
        Column(
            modifier = Modifier
                .background(if (index == selectedIndex) blueDark else White)
        ) {
            Row(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.home_icn),
                    contentDescription = null,
                    tint = if (index == selectedIndex) White else textColorDark,
                )
                Text(
                    text = " $tolaName",
                    modifier = Modifier
                        .fillMaxWidth(),
                    color = if (index == selectedIndex) White else textColorDark,
                    fontSize = 14.sp,
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Row(
                modifier = Modifier
                    .absolutePadding(left = 4.dp)
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            ) {
                Text(
                    text = "VO: ",
                    modifier = Modifier,
                    color = if (index == selectedIndex) White else textColorDark,
                    fontSize = 14.sp,
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = voName,
                    modifier = Modifier
                        .fillMaxWidth(),
                    color = if (index == selectedIndex) White else textColorDark,
                    fontSize = 14.sp,
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun VillageAndVoBoxForBottomSheet(
    modifier: Modifier = Modifier,
    tolaName: String = "",
    voName: String = "",
    index: Int,
    selectedIndex: Int,
    isBpcUser: Boolean = false,
    isVoEndorsementComplete: Boolean = false,
    onVillageSeleted: (Int) -> Unit,

) {
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
                    color = Black
                )
            ) {
                onVillageSeleted(index)
            }
            .background(if (index == selectedIndex) dropDownBg else White)
            .then(modifier),
        elevation = 10.dp
    ) {
        Column() {
            Column(
                modifier = Modifier
                    .background(if (index == selectedIndex) dropDownBg else White)
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
                        text = " $tolaName",
                        color = textColorDark,
                        fontSize = 14.sp,
                        fontFamily = NotoSans,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.constrainAs(textRef) {
                            top.linkTo(parent.top)
                            start.linkTo(iconRef.end)
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
                        .padding(start = 16.dp, end = 16.dp, bottom = 10.dp)
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
                        text = voName,
                        modifier = Modifier
                            .fillMaxWidth(),
                        color = textColorDark,
                        fontSize = 14.sp,
                        fontFamily = NotoSans,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            if (isVoEndorsementComplete) {
                Row(
                    Modifier
                        .background(
                            greenOnline,
                            shape = RoundedCornerShape(bottomStart = 6.dp, bottomEnd = 6.dp)
                        )
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
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
        }
    }
}
