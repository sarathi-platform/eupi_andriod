package com.patsurvey.nudge.activities.ui.login

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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.activities.ui.theme.buttonBgColor
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.navigation.ScreenRoutes
import com.patsurvey.nudge.utils.MOBILE_NUMBER_LENGTH

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel,
    modifier: Modifier
) {
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

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Text(
                text = stringResource(id = R.string.verification_text),
                color = textColorDark,
                fontSize = 24.sp,
                fontFamily = NotoSans,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )

        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
        ) {
            Text(
                text = stringResource(id = R.string.mobile_text),
                fontSize = 18.sp,
                fontFamily = NotoSans,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .padding(vertical = dimensionResource(id = R.dimen.dp_6))
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.dp_6)))
            Column(
            ) {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimensionResource(id = R.dimen.height_60dp))
                        .background(Color.Transparent)
                        .border(
                            dimensionResource(id = R.dimen.dp_1),
                            Color.Black,
                            shape = RoundedCornerShape(dimensionResource(id = R.dimen.dp_6))
                        ),
                    singleLine = true,
                    value = viewModel.mobileNumber.value,
                    onValueChange = {
                        if (it.text.length <= MOBILE_NUMBER_LENGTH)
                            viewModel.mobileNumber.value = it
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        textColor = blueDark
                    ),
                    placeholder = { Text(text = stringResource(id = R.string.enter_mobile_number)) },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        autoCorrect = true,
                        keyboardType = KeyboardType.Number,
                    ),

                    )
            }

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.dp_20)))

            Button(
                onClick = {
                    navController.navigate(ScreenRoutes.OTP_VERIFICATION_SCREEN.route)
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