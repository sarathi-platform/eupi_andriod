package com.patsurvey.nudge.activities

import android.app.Activity
import android.content.Context
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
import androidx.compose.material.Surface
import androidx.compose.material.Text
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import com.patsurvey.nudge.BuildConfig
import com.patsurvey.nudge.R
import com.patsurvey.nudge.RetryHelper
import com.patsurvey.nudge.activities.ui.login.OtpInputFieldForDialog
import com.patsurvey.nudge.activities.ui.progress.VillageSelectionViewModel
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.activities.ui.theme.buttonTextStyle
import com.patsurvey.nudge.activities.ui.theme.dropDownBg
import com.patsurvey.nudge.activities.ui.theme.greenOnline
import com.patsurvey.nudge.activities.ui.theme.greyBorder
import com.patsurvey.nudge.activities.ui.theme.greyRadioButton
import com.patsurvey.nudge.activities.ui.theme.smallTextStyleMediumWeight
import com.patsurvey.nudge.activities.ui.theme.smallerTextStyle
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.activities.ui.theme.white
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.customviews.CustomSnackBarShow
import com.patsurvey.nudge.customviews.CustomSnackBarViewPosition
import com.patsurvey.nudge.customviews.CustomSnackBarViewState
import com.patsurvey.nudge.customviews.rememberSnackBarState
import com.patsurvey.nudge.navigation.navgraph.Graph
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.ButtonPositive
import com.patsurvey.nudge.utils.OTP_LENGTH
import com.patsurvey.nudge.utils.SEC_30_STRING
import com.patsurvey.nudge.utils.showCustomToast

@Composable
fun VillageSelectionScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: VillageSelectionViewModel
) {
    val villages by viewModel.villageList.collectAsState()

    val snackState = rememberSnackBarState()

    val context = LocalContext.current
    var showToast by remember { mutableStateOf(false) }
    if (viewModel.networkErrorMessage.value.isNotEmpty()) {
        if (BuildConfig.DEBUG) showCustomToast(context, viewModel.networkErrorMessage.value)
        RetryHelper.tokenExpired.value =true
        viewModel.networkErrorMessage.value = BLANK_STRING
    }

    val isResendOTPEnable = remember { mutableStateOf(false) }
    val formattedTime = remember {
        mutableStateOf(SEC_30_STRING)
    }
    var isResendOTPVisible by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(key1 = RetryHelper.tokenExpired.value) {
        if (RetryHelper.tokenExpired.value) {
            viewModel.tokenExpired.value = true
            RetryHelper.generateOtp { success, message, mobileNumber ->
                if (success) {
                    viewModel.tokenExpired.value = true
                    snackState.addMessage(
                        message = context.getString(R.string.otp_send_to_mobile_number_message_for_relogin)
                            .replace("{MOBILE_NUMBER}", mobileNumber, true),
                        isSuccess = true, isCustomIcon = false
                    )
                }
            }
        }
    }

    BackHandler {
        (context as? Activity)?.finish()
    }


    LaunchedEffect(key1 = true) {
        viewModel.saveVideosToDb(context)
    }

    Box() {
        if (viewModel.tokenExpired.value) {
            ShowOptDialogForVillageScreen(
                modifier = Modifier,
                context = LocalContext.current,
                viewModel = viewModel,
                snackState = snackState,
                /*isResendOTPEnable = isResendOTPEnable,
                formattedTime = formattedTime,*/
                setShowDialog = {
                    viewModel.tokenExpired.value = false
                },
                positiveButtonClicked = {
                    RetryHelper.updateOtp(viewModel.baseOtpNumber) { success, message ->
                        if (success){
                            RetryHelper.tokenExpired.value = false
                            RetryHelper.retryVillageListApi { success, villageList ->
                                if (success && !villageList?.isNullOrEmpty()!!) {
                                    viewModel.saveVillageListAfterTokenRefresh(villageList)
                                }
                            }
                        }
                        else {
                            snackState.addMessage(message = message, isSuccess = false, isCustomIcon = false)
                        }
                    }
                }
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 12.dp)
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .then(modifier)
        ) {
            Text(
                text = stringResource(R.string.seletc_village_screen_text),
                fontFamily = NotoSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp, color = textColorDark,
                modifier = Modifier
            )
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
                        VillageAndVoBox(
                            tolaName = village.name,
                            voName = village.federationName,
                            index = index,
                            viewModel.villageSelected.value,
                        ) {
                            viewModel.villageSelected.value = it
                            viewModel.updateSelectedVillage()
                            navController.popBackStack()
                            navController.navigate(Graph.HOME)
                        }
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
            CustomSnackBarShow(state = snackState, position = CustomSnackBarViewPosition.Bottom)
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
                        text = "VO Endorsement completed",
                        color = white,
                        style = smallerTextStyle,
                        modifier = Modifier.absolutePadding(bottom = 3.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ShowOptDialogForVillageScreen(
    modifier: Modifier = Modifier,
    context: Context,
    viewModel: BaseViewModel,
    snackState: CustomSnackBarViewState,
    setShowDialog: (Boolean) -> Unit,
    positiveButtonClicked: () -> Unit,
    /*isResendOTPEnable: MutableState<Boolean>,
    formattedTime: MutableState<String>,
    isResendOTPVisible: MutableState<Boolean>*/
) {
    var otpValue by remember {
        mutableStateOf("")
    }

    Dialog(onDismissRequest = { setShowDialog(false) }, DialogProperties(
        dismissOnBackPress = false,
        dismissOnClickOutside = false
    )
    ) {
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = White
        ) {
            Box(contentAlignment = Alignment.Center) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Session Expired!",
                        textAlign = TextAlign.Start,
                        style = buttonTextStyle,
                        maxLines = 1,
                        color = textColorDark,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Please enter OTP to relogin",
                        textAlign = TextAlign.Start,
                        style = smallTextStyleMediumWeight,
                        maxLines = 2,
                        color = textColorDark,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OtpInputFieldForDialog(otpLength = 6, onOtpChanged = { otp ->
                        otpValue = otp
                        viewModel.baseOtpNumber.value = otpValue
                    })

                    /*    AnimatedVisibility(visible = !isResendOTPEnable.value, exit = fadeOut(), enter = fadeIn()) {
                            Row(
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier.fillMaxWidth(),

                                ) {
                                val countDownTimer =
                                    object : CountDownTimer(OTP_RESEND_DURATION, 1000) {
                                        @SuppressLint("SimpleDateFormat")
                                        override fun onTick(millisUntilFinished: Long) {
                                            val dateTimeFormat= SimpleDateFormat("00:ss")
                                            formattedTime.value=dateTimeFormat.format(Date(millisUntilFinished))

                                        }

                                        override fun onFinish() {
                                            isResendOTPEnable.value = true
                                            isResendOTPVisible = !isResendOTPVisible
                                        }

                                    }
                                DisposableEffect(key1 = !isResendOTPEnable.value) {
                                    countDownTimer.start()
                                    onDispose {
                                        countDownTimer.cancel()
                                    }
                                }
                                Text(
                                    text = stringResource(
                                        id = R.string.expiry_login_verify_otp,
                                        formattedTime.value
                                    ),
                                    color = textColorDark,
                                    fontSize = 14.sp,
                                    fontFamily = NotoSans,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = dimensionResource(id = R.dimen.dp_8))
                                        .background(Color.Transparent)
                                )
                            }
                        }
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Text(
                                text = stringResource(id = R.string.resend_otp),
                                color = if (isResendOTPEnable.value) greenOnline else placeholderGrey,
                                fontSize = 14.sp,
                                fontFamily = NotoSans,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center,
                                textDecoration = TextDecoration.Underline,
                                modifier = Modifier.clickable(enabled = isResendOTPEnable.value) {
                                    RetryHelper.generateOtp() { success, message, mobileNumber ->
                                        snackState.addMessage(
                                            message = context.getString(R.string.otp_resend_to_mobile_number_message_for_relogin).replace("{MOBILE_NUMBER}", mobileNumber, true),
                                            isSuccess = true, isCustomIcon = false)
                                    }
                                    formattedTime.value = SEC_30_STRING
                                    isResendOTPEnable.value = false
                                }
                            )
                        }*/

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        ButtonPositive(
                            buttonTitle = stringResource(id = R.string.submit),
                            isArrowRequired = false,
                            isActive = otpValue.length == OTP_LENGTH,
                            modifier = Modifier.weight(1f)
                        ) {
                            positiveButtonClicked()
                            setShowDialog(false)
                        }
                    }
                }
            }
        }
    }
}