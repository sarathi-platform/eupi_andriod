package com.nrlm.baselinesurvey.ui.auth.presentation

import android.annotation.SuppressLint
import android.os.CountDownTimer
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.OTP_LENGTH
import com.nrlm.baselinesurvey.OTP_RESEND_DURATION
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.SEC_30_STRING
import com.nrlm.baselinesurvey.navigation.AuthScreen
import com.nrlm.baselinesurvey.navigation.navgraph.Graph
import com.nrlm.baselinesurvey.ui.auth.viewmodel.OtpVerificationViewModel
import com.nrlm.baselinesurvey.ui.common_components.CustomSnackBarShow
import com.nrlm.baselinesurvey.ui.common_components.LoaderComponent
import com.nrlm.baselinesurvey.ui.common_components.OtpInputField
import com.nrlm.baselinesurvey.ui.common_components.SarathiLogoTextViewComponent
import com.nrlm.baselinesurvey.ui.common_components.rememberSnackBarState
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.ui.theme.NotoSans
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.ui.theme.buttonBgColor
import com.nrlm.baselinesurvey.ui.theme.dimen_8_dp
import com.nrlm.baselinesurvey.ui.theme.greenOnline
import com.nrlm.baselinesurvey.ui.theme.placeholderGrey
import com.nrlm.baselinesurvey.ui.theme.textColorDark
import com.nrlm.baselinesurvey.ui.theme.white
import com.nrlm.baselinesurvey.utils.BaselineCore
import java.text.SimpleDateFormat
import java.util.Date

@SuppressLint("StateFlowValueCalledInComposition", "StringFormatInvalid")
@Composable
fun OtpVerificationScreenComponent(
    navController: NavController,
    viewModel: OtpVerificationViewModel,
    modifier: Modifier = Modifier,
    mobileNumber: String
) {
    val otpValue = remember {
        BaselineCore.autoReadOtp
    }

    val loaderState = viewModel.loaderState.value

    /*var otpValue: MutableState<String> = autoReadOtpValue by remember {
        mutableStateOf("")
    }*/

    val snackState = rememberSnackBarState()
    val formattedTime = remember {
        mutableStateOf(SEC_30_STRING)
    }
    var isResendOTPVisible by remember {
        mutableStateOf(true)
    }
    val networkErrorMessage = viewModel.networkErrorMessage.value
    if(networkErrorMessage.isNotEmpty()){
        snackState.addMessage(
            message = networkErrorMessage,
            isSuccess = false,
            isCustomIcon = false
        )
        viewModel.networkErrorMessage.value = BLANK_STRING
    }
    val isResendOTPEnable = remember { mutableStateOf(false) }

    val context = LocalContext.current
    LaunchedEffect(key1 = true){
        otpValue.value= BLANK_STRING
        viewModel.otpNumber.value= BLANK_STRING
    }

    BackHandler {
        navController.popBackStack()
    }

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
            verticalArrangement = Arrangement.spacedBy(dimen_8_dp),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {

            SarathiLogoTextViewComponent()

            LoaderComponent(visible = loaderState.isLoaderVisible)
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
                bodyMedium = TextStyle(
                    fontSize = 12.sp
                )
            )
            val currentColor = MaterialTheme.colorScheme.copy(
                primary = Color.Magenta
            )

            MaterialTheme(
                shapes = currentShape,
                colorScheme = currentColor,
                typography = typography
            ) {
                OtpInputField(otpLength = 6, otpValue, onOtpChanged = { otp ->
                    otpValue.value = otp
                    viewModel.otpNumber.value = otpValue.value
                })
            }
            AnimatedVisibility(visible = !isResendOTPEnable.value, exit = fadeOut(), enter = fadeIn()) {
                Row(
                    horizontalArrangement = Arrangement.Start,
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
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 2.dp)
                            .background(Color.Transparent)
                    )
                }
            }

            AnimatedVisibility(visible = isResendOTPEnable.value, exit = fadeOut(), enter = fadeIn()) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text(
                        text = stringResource(id = R.string.resend_otp),
                        color = if (isResendOTPEnable.value) greenOnline else placeholderGrey,
                        fontSize = 14.sp,
                        fontFamily = NotoSans,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Start,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier
                            .padding(start = 2.dp)
                            .clickable(enabled = isResendOTPEnable.value) {
                                viewModel.onEvent(OtpVerificationEvent.ResendOtpEvent(mobileNumber))
                                formattedTime.value = SEC_30_STRING
                                isResendOTPEnable.value = false
                            }
                    )
                }
            }
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.dp_25)))
            Button(
                onClick = {
                    viewModel.onEvent(LoaderEvent.UpdateLoaderState(true))
                    viewModel.onEvent(OtpVerificationEvent.ValidateOtpEvent(viewModel.otpNumber.value))
                },
                modifier = Modifier
                    .background(Color.Transparent)
                    .fillMaxWidth()
                    .height(dimensionResource(id = R.dimen.height_60dp)),
                colors = if (otpValue.value.length == OTP_LENGTH) ButtonDefaults.buttonColors(blueDark) else ButtonDefaults.buttonColors(
                    buttonBgColor
                ),
                shape = RoundedCornerShape(6.dp),
                enabled = otpValue.value.length == OTP_LENGTH
            ) {

                Text(
                    text = stringResource(id = R.string.submit),
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
        }
    }

    LaunchedEffect(key1 = true){
        snackState.addMessage(message=context.getString(R.string.otp_send_to_mobile_number_message, mobileNumber),
            isSuccess = true, isCustomIcon = false)
    }

    LaunchedEffect(key1 = viewModel.validateApiSuccess.value) {
        viewModel.onEvent(LoaderEvent.UpdateLoaderState(false))
        if (viewModel.validateApiSuccess.value) {
            if(navController.graph.route?.equals(Graph.HOME,true) == true){
                //Commented for now

                navController.navigate(route = Graph.HOME){
                    launchSingleTop=true
                    popUpTo(AuthScreen.START_SCREEN.route){
                        inclusive=true
                    }
                }

                viewModel.validateApiSuccess.value
            }else{
                navController.navigate(route = Graph.HOME){
                    launchSingleTop=true
                    popUpTo(AuthScreen.START_SCREEN.route){
                        inclusive=true
                    }
                }
                viewModel.validateApiSuccess.value
            }
            BaselineCore.autoReadOtp.value = ""
        }
    }

    LaunchedEffect(key1 = viewModel.resendApiSuccess.value) {
        if (viewModel.resendApiSuccess.value) {
            snackState.addMessage(
                message = context.getString(
                    R.string.otp_resend_to_mobile_number_message,
                    mobileNumber
                ), isSuccess = true, isCustomIcon = false
            )
            viewModel.resendApiSuccess.value = false
        }
    }

    CustomSnackBarShow(state = snackState)
}