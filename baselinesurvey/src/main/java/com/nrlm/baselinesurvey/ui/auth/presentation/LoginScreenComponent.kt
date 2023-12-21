package com.nrlm.baselinesurvey.ui.auth.presentation

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.MOBILE_NUMBER_LENGTH
import com.nrlm.baselinesurvey.activity.MainActivity
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.ui.auth.viewmodel.LoginScreenViewModel
import com.nrlm.baselinesurvey.ui.common_components.CustomSnackBarShow
import com.nrlm.baselinesurvey.ui.common_components.LoaderComponent
import com.nrlm.baselinesurvey.ui.common_components.SarathiLogoTextViewComponent
import com.nrlm.baselinesurvey.ui.common_components.rememberSnackBarState
import com.nrlm.baselinesurvey.ui.theme.NotoSans
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.ui.theme.buttonBgColor
import com.nrlm.baselinesurvey.utils.onlyNumberField
import com.nrlm.baselinesurvey.utils.setKeyboardToReadjust
import com.nrlm.baselinesurvey.utils.stringToInt
import com.nrlm.baselinesurvey.navigation.navgraph.Graph
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.ui.theme.dimen_8_dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreenComponent(
    navController: NavController,
    viewModel: LoginScreenViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val snackState = rememberSnackBarState()

    val loaderState = viewModel.loaderState.value

    val activity = context as MainActivity

    val focusManager = LocalFocusManager.current

    setKeyboardToReadjust(activity)
    val networkErrorMessage = viewModel.networkErrorMessage.value

    BackHandler {
        (context as? Activity)?.finish()
    }

    if(networkErrorMessage.isNotEmpty()){
        snackState.addMessage(
            message = networkErrorMessage,
            isSuccess = false,
            isCustomIcon = false
        )
        viewModel.networkErrorMessage.value = BLANK_STRING
    }

    val mobileNumberState = viewModel.mobileNumberState.value
    
    LaunchedEffect(key1 = mobileNumberState) {
        viewModel.onEvent(LoaderEvent.UpdateLoaderState(false))
        if (mobileNumberState.isMobileNumberValidatedFromServer) {
            if(navController.graph.route?.equals(Graph.HOME,true) == true){
                navController.navigate(route = "otp_verification_screen/" + mobileNumberState.mobileNumber.text)
            }else
                navController.navigate(route = "otp_verification_screen/" + mobileNumberState.mobileNumber.text)
        } else {
            if (!mobileNumberState.errorMessage.equals(BLANK_STRING, true)) {
                snackState.addMessage(
                    message = mobileNumberState.errorMessage,
                    isSuccess = false,
                    isCustomIcon = false
                )
                viewModel.resetMobileNumberState()
            }
        }
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
        Column(verticalArrangement = Arrangement.spacedBy(dimen_8_dp), modifier = Modifier.align(Alignment.TopCenter)) {

            SarathiLogoTextViewComponent()

            LoaderComponent(visible = loaderState.isLoaderVisible)
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
                    value = mobileNumberState.mobileNumber,
                    textStyle = TextStyle(
                        fontSize = 18.sp,
                        fontFamily = NotoSans,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Start
                    ),
                    onValueChange = {
                        if(onlyNumberField(it.text)) {
                            if (it.text.length <= MOBILE_NUMBER_LENGTH){
                                viewModel.onEvent(LoginScreenEvent.OnValueChangeEvent(it))
                            }

                        }
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
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

            Button(
                onClick = {
                    focusManager.clearFocus()
                    if (stringToInt(mobileNumberState.mobileNumber.text[0].toString()) < 6) {
                        snackState.addMessage(
                            message = context.getString(R.string.invalid_mobile_number),
                            isSuccess = false,
                            isCustomIcon = false
                        )
                    } else {
                        viewModel.onEvent(LoginScreenEvent.GenerateOtpEvent(mobileNumberState.mobileNumber))
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent),
                colors = if (mobileNumberState.mobileNumber.text.length == MOBILE_NUMBER_LENGTH)
                    ButtonDefaults.buttonColors(blueDark) else ButtonDefaults.buttonColors(
                    buttonBgColor
                ),
                shape = RoundedCornerShape(dimensionResource(id = R.dimen.dp_6)),
                enabled = mobileNumberState.mobileNumber.text.length == MOBILE_NUMBER_LENGTH
            ) {

                Text(
                    text = stringResource(id = R.string.get_otp),
                    color = if (mobileNumberState.mobileNumber.text.length == MOBILE_NUMBER_LENGTH)
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
    CustomSnackBarShow(state = snackState)
}