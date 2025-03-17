package com.patsurvey.nudge.activities.ui.login

import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nudge.core.setKeyboardToReadjust
import com.nudge.core.ui.theme.dimen_2_dp
import com.nudge.core.ui.theme.textStyleMedium
import com.nudge.navigationmanager.graphs.NudgeNavigationGraph
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.MainActivity
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.activities.ui.theme.buttonBgColor
import com.patsurvey.nudge.activities.ui.theme.midiumBlueColor
import com.patsurvey.nudge.activities.ui.theme.midiumGrayColor
import com.patsurvey.nudge.customviews.CustomSnackBarShow
import com.patsurvey.nudge.customviews.SarathiLogoTextViewV2
import com.patsurvey.nudge.customviews.rememberSnackBarState
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.MOBILE_NUMBER_LENGTH
import com.patsurvey.nudge.utils.onlyNumberField
import com.patsurvey.nudge.utils.stringToInt

@SuppressLint("StringFormatInvalid")
@Composable
fun LoginScreenV2(
    navController: NavController, viewModel: LoginViewModel, modifier: Modifier
) {
    val context = LocalContext.current
    val snackState = rememberSnackBarState()

    val activity = context as MainActivity

    val focusManager = LocalFocusManager.current
    var isFocused by remember { mutableStateOf(false) }
    RequestPermissions()
    setKeyboardToReadjust(activity)
    val networkErrorMessage = viewModel.networkErrorMessage.value
    BackHandler {
        (context as? Activity)?.finish()
    }
    if (networkErrorMessage.isNotEmpty()) {
        snackState.addMessage(
            message = networkErrorMessage, isSuccess = false, isCustomIcon = false
        )
        viewModel.networkErrorMessage.value = BLANK_STRING
    }
    Box(
        modifier = Modifier
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
        ) {
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.dp_6)))
            Row(
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensionResource(id = R.dimen.padding_16dp))
                    .height(dimensionResource(id = R.dimen.dp_65))
                    .border(
                        width = dimen_2_dp, color = when {
                            isFocused -> midiumBlueColor
                            !isFocused -> midiumGrayColor
                            else -> Color.Transparent
                        },
                        shape = RoundedCornerShape(dimensionResource(id = R.dimen.dp_6))
                    )
            ) {

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .onFocusChanged { focusState ->
                            isFocused = focusState.isFocused
                        },
                    singleLine = true,
                    value = viewModel.mobileNumber.value,
                    placeholder = {
                        Text(text = stringResource(R.string.mobile_number), color = midiumGrayColor)
                    },
                    textStyle = textStyleMedium.copy(textAlign = TextAlign.Start),
                    onValueChange = {
                        if (onlyNumberField(it.text)) {
                            if (it.text.length <= MOBILE_NUMBER_LENGTH) viewModel.mobileNumber.value =
                                it
                        }
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        textColor = blueDark,
                        focusedIndicatorColor = Color.Transparent, // Border color when focused (typing)
                        unfocusedIndicatorColor = Color.Transparent, // Border color when not focused
                        disabledIndicatorColor = Color.Transparent,
                    ),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        autoCorrect = true,
                        keyboardType = KeyboardType.Number,
                    ),
                )


            }
            Text(
                text = stringResource(id = R.string.otp_will_be_sent_to_this_number),
                color = blueDark,
                fontSize = 16.sp,
                fontFamily = NotoSans,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .padding(
                        vertical = dimensionResource(id = R.dimen.dp_6),
                        horizontal = dimensionResource(id = R.dimen.padding_16dp)
                    )
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.dp_20)))

            Button(
                onClick = {
                    focusManager.clearFocus()
                    if (stringToInt(viewModel.mobileNumber.value.text[0].toString()) < 6) {
                        snackState.addMessage(
                            message = context.getString(R.string.invalid_mobile_number),
                            isSuccess = false,
                            isCustomIcon = false
                        )
                    } else {
                        viewModel.generateOtp { success, message ->
                            if (success) {
                                if (navController.graph.route?.equals(
                                        NudgeNavigationGraph.HOME,
                                        true
                                    ) == true
                                ) {
                                    navController.navigate(route = "otp_verification_screen/" + viewModel.mobileNumber.value.text)
                                } else
                                    navController.navigate(route = "otp_verification_screen/" + viewModel.mobileNumber.value.text)
                            } else {
                                snackState.addMessage(
                                    message = message,
                                    isSuccess = false,
                                    isCustomIcon = false
                                )
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = dimensionResource(id = R.dimen.padding_16dp),
                        end = dimensionResource(id = R.dimen.padding_16dp),
                        bottom = dimensionResource(
                            id = R.dimen.padding_32dp
                        )
                    )
                    .background(Color.Transparent),
                colors = if (viewModel.mobileNumber.value.text.length == MOBILE_NUMBER_LENGTH)
                    ButtonDefaults.buttonColors(blueDark) else ButtonDefaults.buttonColors(
                    buttonBgColor
                ),
                shape = RoundedCornerShape(dimensionResource(id = R.dimen.dp_6)),
                enabled = viewModel.mobileNumber.value.text.length == MOBILE_NUMBER_LENGTH
            ) {

                Text(
                    text = stringResource(id = R.string.next),
                    color = if (viewModel.mobileNumber.value.text.length == MOBILE_NUMBER_LENGTH)
                        Color.White else blueDark,
                    fontSize = 18.sp,
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = dimensionResource(id = R.dimen.dp_6))
                )

            }
        }


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

