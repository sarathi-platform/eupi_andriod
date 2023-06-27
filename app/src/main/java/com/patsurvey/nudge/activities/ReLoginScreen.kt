package com.patsurvey.nudge.activities

import android.annotation.SuppressLint
import android.content.Context
import android.os.CountDownTimer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.patsurvey.nudge.R
import com.patsurvey.nudge.RetryHelper
import com.patsurvey.nudge.activities.ui.login.OtpInputFieldForDialog
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.buttonTextStyle
import com.patsurvey.nudge.activities.ui.theme.greenOnline
import com.patsurvey.nudge.activities.ui.theme.placeholderGrey
import com.patsurvey.nudge.activities.ui.theme.smallTextStyleMediumWeight
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.customviews.CustomSnackBarViewState
import com.patsurvey.nudge.utils.ButtonPositive
import com.patsurvey.nudge.utils.OTP_LENGTH
import com.patsurvey.nudge.utils.OTP_RESEND_DURATION
import com.patsurvey.nudge.utils.SEC_30_STRING
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun ShowOptDialogForVillageScreen(
    modifier: Modifier = Modifier,
    context: Context,
    viewModel: BaseViewModel,
    snackState: CustomSnackBarViewState,
    setShowDialog: (Boolean) -> Unit,
    positiveButtonClicked: () -> Unit,
) {

    val otpValue = remember {
        RetryHelper.autoReadOtp
    }

    /*var otpValue: MutableState<String> = autoReadOtpValue by remember {
        mutableStateOf("")
    }*/

    val isResendOTPEnable = remember { mutableStateOf(false) }
    val formattedTime = remember {
        mutableStateOf(SEC_30_STRING)
    }
    var isResendOTPVisible by remember {
        mutableStateOf(true)
    }

    Dialog(
        onDismissRequest = { setShowDialog(false) }, DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = Color.White
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

                    OtpInputFieldForDialog(otpLength = 6, otpValue, onOtpChanged = { otp ->
                        otpValue.value = otp
                        viewModel.baseOtpNumber.value = otpValue.value
                    })

                        AnimatedVisibility(visible = !isResendOTPEnable.value, exit = fadeOut(), enter = fadeIn()) {
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
                        }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        ButtonPositive(
                            buttonTitle = stringResource(id = R.string.submit),
                            isArrowRequired = false,
                            isActive = otpValue.value.length == OTP_LENGTH,
                            modifier = Modifier.weight(1f)
                        ) {
                            positiveButtonClicked()
//                            setShowDialog(false)
                        }
                    }
                }
            }
        }
    }
}