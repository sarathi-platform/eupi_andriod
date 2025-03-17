package com.patsurvey.nudge.activities.ui.login

import android.annotation.SuppressLint
import android.os.CountDownTimer
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nudge.core.maskMobileNumber
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_3_dp
import com.nudge.core.ui.theme.grayColor
import com.nudge.core.ui.theme.quesOptionTextStyle
import com.nudge.core.ui.theme.red
import com.nudge.core.value
import com.nudge.navigationmanager.routes.AUTH_LANGUAGE_SCREEN_ROUTE_NAME
import com.patsurvey.nudge.R
import com.patsurvey.nudge.RetryHelper
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.activities.ui.theme.buttonBgColor
import com.patsurvey.nudge.activities.ui.theme.midiumBlueColor
import com.patsurvey.nudge.activities.ui.theme.white
import com.patsurvey.nudge.customviews.CustomSnackBarShow
import com.patsurvey.nudge.customviews.SarathiLogoTextViewV2
import com.patsurvey.nudge.customviews.rememberSnackBarState
import com.patsurvey.nudge.utils.ARG_FROM_HOME
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.OTP_LENGTH
import com.patsurvey.nudge.utils.OTP_RESEND_DURATION
import com.patsurvey.nudge.utils.SEC_30_STRING
import com.patsurvey.nudge.utils.UPCM_USER
import java.text.SimpleDateFormat
import java.util.Date

@SuppressLint("UnrememberedMutableState", "StringFormatMatches")
@Composable
fun OtpVerificationScreenV2(
    navController: NavController,
    viewModel: OtpVerificationViewModel,
    modifier: Modifier = Modifier,
    mobileNumber: String
) {
    val otpValue = remember {
        RetryHelper.autoReadOtp
    }
    val snackState = rememberSnackBarState()
    val formattedTime = remember {
        mutableStateOf(SEC_30_STRING)
    }
    var isResendOTPVisible by remember {
        mutableStateOf(true)
    }
    var isOtpInputWrong by remember {
        mutableStateOf(false)
    }
    val networkErrorMessage = viewModel.networkErrorMessage.value
    if (networkErrorMessage.isNotEmpty()) {
        snackState.addMessage(
            message = networkErrorMessage,
            isSuccess = false,
            isCustomIcon = false
        )
        viewModel.networkErrorMessage.value = BLANK_STRING
    }
    val isResendOTPEnable = remember { mutableStateOf(false) }

    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        otpValue.value = BLANK_STRING
        viewModel.otpNumber.value = BLANK_STRING
    }
    if (otpValue.value.isBlank()) {
        isOtpInputWrong = false
    }

    Box(
        modifier = Modifier
            .background(color = Color.White)
            .fillMaxSize()
            .then(modifier)

    ) {
        Image(
            painter = painterResource(id = R.drawable.lokos_bg),
            contentDescription = "Background Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
        )
        SarathiLogoTextViewV2()

        AnimatedVisibility(
            visible = viewModel.showLoader.value,
            exit = fadeOut(),
            enter = fadeIn(),
            modifier = Modifier.align(
                Alignment.Center
            )
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .padding(top = 30.dp)
                    .align(Alignment.Center)
            ) {
                CircularProgressIndicator(
                    color = blueDark, modifier = Modifier
                        .size(28.dp)
                        .align(Alignment.Center)
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(
                    horizontal = dimensionResource(id = R.dimen.padding_16dp),
                    vertical = dimensionResource(id = R.dimen.padding_32dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.verify_otp),
                    color = blueDark,
                    fontSize = 16.sp,
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
                OtpSentMessage(mobileNumber = viewModel.getUserMobileNumber())
                Spacer(modifier = Modifier.height(dimen_3_dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            val currentShape = MaterialTheme.shapes.copy(small = RoundedCornerShape(8))
            val typography = MaterialTheme.typography.copy(
                h4 = TextStyle(
                    fontSize = 12.sp
                )
            )
            val currentColor = MaterialTheme.colors.copy(primary = Color.Magenta)

            MaterialTheme(
                shapes = currentShape,
                colors = currentColor,
                typography = typography
            ) {
                OtpInputField(
                    isOtpInputWrong = isOtpInputWrong.value(),
                    otpLength = 6,
                    autoReadOtp = otpValue,
                    onOtpChanged = { otp ->
                        otpValue.value = otp
                        viewModel.otpNumber.value = otpValue.value
                    })
            }
            AnimatedVisibility(
                visible = !isResendOTPEnable.value,
                exit = fadeOut(),
                enter = fadeIn()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,

                    ) {
                    val countDownTimer =
                        object : CountDownTimer(OTP_RESEND_DURATION, 1000) {
                            @SuppressLint("SimpleDateFormat")
                            override fun onTick(millisUntilFinished: Long) {
                                val dateTimeFormat = SimpleDateFormat("00:ss")
                                formattedTime.value =
                                    dateTimeFormat.format(Date(millisUntilFinished))

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
                        color = midiumBlueColor,
                        fontSize = 14.sp,
                        fontFamily = NotoSans,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 2.dp)
                            .background(Color.Transparent)
                    )
                }
            }

            AnimatedVisibility(
                visible = isOtpInputWrong.value(),
                exit = fadeOut(),
                enter = fadeIn()
            ) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text(
                        text = stringResource(R.string.otp_is_invalid),
                        color = red,
                        style = quesOptionTextStyle,
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 2.dp)
                            .background(Color.Transparent)
                    )
                }
            }
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.dp_25)))
            Button(
                onClick = {
                    viewModel.validateOtp { userType, success, message ->
                        isOtpInputWrong = !success
                        if (success) {
                            if (userType != UPCM_USER) {
                                viewModel.savePageFromOTPScreen()
                            }
                            navController.navigate(route = "$AUTH_LANGUAGE_SCREEN_ROUTE_NAME/$ARG_FROM_HOME")
                            RetryHelper.autoReadOtp.value = ""
                        } else {
                            Log.e("TAG", "OtpVerificationScreen: $message")
                            snackState.addMessage(
                                message = message,
                                isSuccess = false,
                                isCustomIcon = false
                            )
                        }
                    }
                },
                modifier = Modifier
                    .background(Color.Transparent)
                    .fillMaxWidth()
                    .height(dimensionResource(id = R.dimen.height_60dp)),
                colors = if (otpValue.value.length == OTP_LENGTH) ButtonDefaults.buttonColors(
                    blueDark
                ) else ButtonDefaults.buttonColors(
                    buttonBgColor
                ),
                shape = RoundedCornerShape(6.dp),
                enabled = otpValue.value.length == OTP_LENGTH
            ) {

                Text(
                    text = stringResource(id = R.string.verify),
                    color = if (otpValue.value.length == OTP_LENGTH) white else blueDark,
                    fontSize = 18.sp,
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                )

            }

            AnimatedVisibility(
                visible = isResendOTPEnable.value,
                exit = fadeOut(),
                enter = fadeIn()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = dimen_10_dp)
                ) {

                    Text(
                        text = stringResource(id = R.string.resend_otp),
                        color = if (isResendOTPEnable.value) midiumBlueColor else grayColor,
                        fontSize = 14.sp,
                        fontFamily = NotoSans,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .padding(start = 2.dp)
                            .clickable(enabled = isResendOTPEnable.value) {
                                viewModel.resendOtp() { success, message ->
                                    snackState.addMessage(
                                        message = context.getString(
                                            R.string.otp_resend_to_mobile_number_message,
                                            mobileNumber
                                        ), isSuccess = true, isCustomIcon = false
                                    )
                                }
                                formattedTime.value = SEC_30_STRING
                                isResendOTPEnable.value = false
                            }
                    )
                }
            }
        }
    }

    LaunchedEffect(key1 = mobileNumber) {
        snackState.addMessage(
            message = context.getString(R.string.otp_send_to_mobile_number_message, mobileNumber),
            isSuccess = true, isCustomIcon = false
        )
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CustomSnackBarShow(
            modifier = Modifier.padding(vertical = 100.dp),
            state = snackState
        )
    }
}

@Composable
fun OtpSentMessage(mobileNumber: String?) {
    val maskedNumber = mobileNumber?.let { maskMobileNumber(it) } ?: BLANK_STRING
    val annotatedText = buildAnnotatedString {
        append(stringResource(R.string.an_otp_has_been_sent_on))
        withStyle(style = SpanStyle(color = Color.Blue)) {
            append(maskedNumber)
        }
    }
    Text(
        text = annotatedText,
        color = blueDark,
        fontSize = 12.sp,
        fontFamily = NotoSans,
        fontWeight = FontWeight.SemiBold,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(16.dp)
    )
}

