package com.patsurvey.nudge.activities.ui.login

import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.*
import com.patsurvey.nudge.customviews.SarathiLogoTextView
import com.patsurvey.nudge.navigation.ScreenRoutes
import com.patsurvey.nudge.utils.MOBILE_NUMBER_LENGTH
import com.patsurvey.nudge.utils.showCustomToast

@SuppressLint("StringFormatInvalid")
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel,
    modifier: Modifier
) {
    val context = LocalContext.current

    BackHandler {
        (context as? Activity)?.finish()
    }

    Box(
        modifier = Modifier
            .background(color = Color.White)
            .fillMaxSize()
            .padding(
                horizontal = dimensionResource(id = R.dimen.padding_16dp),
                vertical = dimensionResource(
                    id = R.dimen.padding_32dp
                )
            )
            .then(modifier)
    ) {
        SarathiLogoTextView()

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
            Text(
                text = stringResource(id = R.string.enter_mobile_text),
                color = blueDark,
                fontSize = 16.sp,
                fontFamily = NotoSans,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .padding(vertical = dimensionResource(id = R.dimen.dp_6))
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.dp_6)))
            Row(
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(id = R.dimen.dp_65))
                    .border(
                        dimensionResource(id = R.dimen.dp_1),
                        blueDark,
                        shape = RoundedCornerShape(dimensionResource(id = R.dimen.dp_6))
                    )
            ) {

                Text(
                    text = "+91 - ",
                    color = colorResource(id = R.color.placeholder_91_color),
                    fontSize = 18.sp,
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(start = dimensionResource(id = R.dimen.dp_20))
                )
                TextField(
                    modifier = Modifier
                        .background(Color.Transparent),
                    singleLine = true,
                    value = viewModel.mobileNumber.value,
                    textStyle = TextStyle(
                        fontSize = 18.sp,
                        fontFamily = NotoSans,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Start
                    ),
                    onValueChange = {
                        if (it.text.length <= MOBILE_NUMBER_LENGTH)
                            viewModel.mobileNumber.value = it
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        textColor = blueDark,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
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
                    .padding(vertical = dimensionResource(id = R.dimen.dp_6))
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.dp_20)))

            Button(onClick = {
                if(viewModel.mobileNumber.value.text[0].toString().toInt()<6){
                    showCustomToast(context,context.getString(R.string.invalid_mobile_number))
                }else {
                    viewModel.generateOtp { success, message ->
                        showCustomToast(context, message)
                        if (success)
                            navController.navigate(ScreenRoutes.OTP_VERIFICATION_SCREEN.route)
                    }
                }
                             },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent),
                colors = if (viewModel.mobileNumber.value.text.length == MOBILE_NUMBER_LENGTH)
                    ButtonDefaults.buttonColors(blueDark) else ButtonDefaults.buttonColors(
                    buttonBgColor
                ),
                shape = RoundedCornerShape(dimensionResource(id = R.dimen.dp_6)),
                enabled = viewModel.mobileNumber.value.text.length == MOBILE_NUMBER_LENGTH
            ) {

                Text(
                    text = stringResource(id = R.string.get_otp),
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
}