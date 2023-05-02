package com.patsurvey.nudge.activities.ui.login


import android.annotation.SuppressLint
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.*
import com.patsurvey.nudge.navigation.ScreenRoutes
import com.patsurvey.nudge.utils.MOBILE_NUMBER_LENGTH
import com.patsurvey.nudge.utils.OTP_RESEND_DURATION
import com.patsurvey.nudge.utils.showToast
import com.patsurvey.nudge.utils.visible

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun OtpVerificationScreen(
    navController: NavController,
    viewModel: OtpVerificationViewModel,
    modifier:Modifier=Modifier
) {
    var otpValue by remember {
        mutableStateOf("")
    }
    var isResendOTPVisible by remember {
        mutableStateOf(true)
    }
    val context= LocalContext.current
    val textAlpha: Float by animateFloatAsState(
        targetValue = if (isResendOTPVisible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 100,
            easing = LinearEasing,
        )
    )
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
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
        ) {
            Text(
                text = "Enter OTP",
                color = blueDark,
                fontSize = 14.sp,
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
                })
            }
            Row(horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()) {

                Text(
                    text = stringResource(id = R.string.resend_otp),
                    color = greenDark,
                    fontSize = 14.sp,
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    textDecoration = TextDecoration.Underline
                )
            }
//                Row(
//                    horizontalArrangement = Arrangement.End,
//                    modifier = Modifier.fillMaxWidth()
//                        .alpha(textAlpha),
//
//                ) {
//                    val timeData = remember {
//                        mutableStateOf(OTP_RESEND_DURATION)
//                    }
//
//                    val countDownTimer =
//                        object : CountDownTimer(OTP_RESEND_DURATION, 1000) {
//                            override fun onTick(millisUntilFinished: Long) {
//                                var secs = (millisUntilFinished / 1000)
//
//                                if (secs == 0L) {
//                                    secs = 1L
//                                }
//                                timeData.value = secs
//                            }
//
//                            override fun onFinish() {
//                                viewModel.isResendOTPEnable.value = true
//                                isResendOTPVisible = !isResendOTPVisible
//                            }
//
//                        }
//                    DisposableEffect(key1 = "Key") {
//                        countDownTimer.start()
//                        onDispose {
//                            countDownTimer.cancel()
//                        }
//                    }
//                    Text(
//                        text = stringResource(
//                            id = R.string.expiry_login_verify_otp,
//                            timeData.value
//                        ),
//                        color = textColorDark,
//                        fontSize = 14.sp,
//                        fontFamily = NotoSans,
//                        fontWeight = FontWeight.SemiBold,
//                        textAlign = TextAlign.End,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .background(Color.Transparent)
//                    )
//                }


                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        navController.navigate(ScreenRoutes.VILLAGE_SELECTION_SCREEN.route)
                    },
                    modifier = Modifier
                        .background(Color.Transparent)
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(blueDark),
                    shape = RoundedCornerShape(6.dp)
                ) {

                    Text(
                        text = stringResource(id = R.string.submit),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontFamily = NotoSans,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .background(blueDark)
                    )

                }
            }
        }
}


