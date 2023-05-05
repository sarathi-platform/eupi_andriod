package com.patsurvey.nudge.activities.ui.login


import android.annotation.SuppressLint
import android.os.CountDownTimer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.HomeScreen
import com.patsurvey.nudge.activities.ui.theme.*
import com.patsurvey.nudge.customviews.SarathiLogoTextView
import com.patsurvey.nudge.navigation.ScreenRoutes
import com.patsurvey.nudge.navigation.StartFlowNavigation
import com.patsurvey.nudge.utils.*
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun OtpVerificationScreen(
    navController: NavController,
    viewModel: OtpVerificationViewModel,
    modifier: Modifier = Modifier
) {
    var otpValue by remember {
        mutableStateOf("")
    }
    val navHomeController = rememberNavController()
    val formattedTime = remember {
        mutableStateOf(SEC_30_STRING)
    }
    var isResendOTPVisible by remember {
        mutableStateOf(true)
    }

    val isResendOTPEnable = remember { mutableStateOf(false) }

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .background(color = Color.White)
            .fillMaxSize()
            .padding(
                horizontal = dimensionResource(id = R.dimen.padding_16dp),
                vertical = dimensionResource(id = R.dimen.padding_32dp)
            )
            .then(modifier)

    ) {
        SarathiLogoTextView()

        AnimatedVisibility(visible = viewModel.showLoader.value, exit = fadeOut(), enter = fadeIn(), modifier = Modifier.align(
            Alignment.Center)) {
            Box(modifier = Modifier
                .size(28.dp)
                .padding(top = 30.dp)
                .align(Alignment.Center)) {
                CircularProgressIndicator(color = blueDark, modifier = Modifier
                    .size(28.dp)
                    .align(Alignment.Center))
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
        ) {
            Text(
                text = stringResource(id = R.string.enter_otp_text),
                color = blueDark,
                fontSize = 16.sp,
                fontFamily = NotoSans,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Start
            )
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
                OtpInputField(otpLength = 6, onOtpChanged = { otp ->
                    otpValue = otp
                    viewModel.otpNumber.value = otpValue
                })
            }
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
                            .padding(horizontal = dimensionResource(id = R.dimen.dp_10))
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
                        viewModel.validateOtp { success, message ->
                            if (success){
                                    navController.navigate(ScreenRoutes.VILLAGE_SELECTION_SCREEN.route)
                            }
                            else
                                showToast(context, message)
                        }
                        isResendOTPEnable.value = false
                    }
                )
            }


            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.dp_25)))
            Button(
                onClick = {
                    viewModel.validateOtp { success, message ->
                        if (success){
                            navController.navigate(ScreenRoutes.LOGIN_HOME_SCREEN.route)
                        }

                        else
                            showToast(context, message)
                    }
                },
                modifier = Modifier
                    .background(Color.Transparent)
                    .fillMaxWidth()
                    .height(dimensionResource(id = R.dimen.height_60dp)),
                colors = if (otpValue.length == OTP_LENGTH) ButtonDefaults.buttonColors(blueDark) else ButtonDefaults.buttonColors(
                    buttonBgColor
                ),
                shape = RoundedCornerShape(6.dp),
                enabled = otpValue.length == OTP_LENGTH
            ) {

                Text(
                    text = stringResource(id = R.string.submit),
                    color = if (otpValue.length == OTP_LENGTH) white else blueDark,
                    fontSize = 18.sp,
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                )

            }
        }
    }
}


