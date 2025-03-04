package com.patsurvey.nudge.activities.ui.login

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.activities.ui.theme.mediumTextStyle
import com.patsurvey.nudge.activities.ui.theme.otpBorderColor
import com.patsurvey.nudge.activities.ui.theme.red
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.utils.onlyNumberField

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun OtpInputField(
    otpLength: Int,
    autoReadOtp: MutableState<String>,
    isOtpInputWrong: Boolean = false,
    onOtpChanged: (String) -> Unit
){
    var otpValue by remember { autoReadOtp }
    val keyboardController = LocalSoftwareKeyboardController.current

    val keyboardState = keyboardAsState(KeyboardStatus.Closed)
    val isShowWarning by remember(keyboardState) {
        derivedStateOf {
            if (keyboardState.value == KeyboardStatus.Closed) {
                if (otpValue.length != otpLength) {
                    return@derivedStateOf true
                }
            }
            false

        }
    }

    val focusRequester = remember {
        FocusRequester()
    }

    BasicTextField(
        modifier = Modifier.focusRequester(focusRequester),
        value = otpValue,
        onValueChange = { value->
            if(onlyNumberField(value)) {
                if (value.length <= otpLength) {
                    otpValue = value
                    onOtpChanged(otpValue)
                }
            }

    }, decorationBox ={
        Row (horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()){
            repeat(otpLength){ index->

                val char = when{
                    index>=otpValue.length->""
                    else -> otpValue[index].toString()
                }

                val isFocus = index == otpValue.length
                OtpCell(
                    char = char,
                    isFocus = isFocus,
                    isOtpInputWrong = isOtpInputWrong,
                    isShowWarning = isShowWarning,
                    modifier = Modifier.weight(
                        1f
                    )
                )

            }
        }
        },

        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
            keyboardController?.hide()

        }
        )
    )

    LaunchedEffect(key1 = true) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }
}

@Composable
fun OtpCell(
    char: String,
    isFocus: Boolean,
    isShowWarning: Boolean,
    isOtpInputWrong: Boolean = false,
    modifier: Modifier=Modifier
){
//    val borderColor = if(isShowWarning){
//        MaterialTheme.colors.error
//    }else if(isFocus){
//        MaterialTheme.colors.primary
//    }else {
//        MaterialTheme.colors.secondary
//    }

    Surface(modifier = Modifier
        .height(dimensionResource(id = R.dimen.otp_box_height))
        .width(dimensionResource(id = R.dimen.otp_box_width))
        .padding(dimensionResource(id = R.dimen.dp_4))
        .border(
            width = if (isFocus) 2.dp else 1.dp,
            color = when {
                isFocus -> textColorDark
                isOtpInputWrong -> red
                else -> otpBorderColor
            },
            shape = MaterialTheme.shapes.small
        ))
        {
            Text(
                text = char,
                style = mediumTextStyle,
                color = blueDark,
                modifier = Modifier
                    .wrapContentSize(align = Alignment.Center)

            )

    }
}

@Preview(showBackground = true)
@Composable
fun OtpInputFieldPreview() {
    MaterialTheme {
        Box(modifier = Modifier.padding(4.dp)) {
            val otp = remember {
                mutableStateOf("")
            }
            OtpInputField(otpLength = 6, otp, onOtpChanged = {})
        }
    }
}

@Preview(name = "OptCell Focus", showBackground = true)
@Composable
fun OtpCellFocusPreview(
) {

    MaterialTheme {
        Box(modifier = Modifier.padding(24.dp)) {
            OtpCell(char = "7", isFocus = true, isShowWarning = false)
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun OtpInputFieldForDialog(
    otpLength: Int,
    autoReadOtp: MutableState<String>,
    onOtpChanged: (String) -> Unit
){
    var otpValue by remember {
        autoReadOtp
    }
    val keyboardController = LocalSoftwareKeyboardController.current

    val keyboardState = keyboardAsState(KeyboardStatus.Closed)
    val isShowWarning by remember(keyboardState) {
        derivedStateOf {
            if (keyboardState.value == KeyboardStatus.Closed) {
                if (otpValue.length != otpLength) {
                    return@derivedStateOf true
                }
            }
            false

        }
    }

//    val focusRequester = remember {
//        FocusRequester()
//    }

    BasicTextField(
        modifier = Modifier/*.focusRequester(focusRequester)*/,
        value = otpValue,
        onValueChange = { value->
            if(onlyNumberField(value = value)) {
                if (value.length <= otpLength) {
                    otpValue = value
                    onOtpChanged(otpValue)
                }
            }

        }, decorationBox ={
            Row (horizontalArrangement = Arrangement.spacedBy(5.dp), modifier = Modifier.fillMaxWidth()){
                repeat(otpLength){ index->

                    val char = when{
                        index>=otpValue.length->""
                        else -> otpValue[index].toString()
                    }

                    val isFocus = index == otpValue.length
                    OtpCellForDialog(
                        char = char,
                        isFocus = isFocus,
                        isShowWarning = isShowWarning,
                        modifier = Modifier.weight(
                            1f
                        )
                    )

                }
            }
        },

        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
            keyboardController?.hide()

        }
        )
    )

    LaunchedEffect(key1 = true) {
//        focusRequester.requestFocus()
//        keyboardController?.show()
    }
}

@Composable
fun OtpCellForDialog(
    char: String,
    isFocus: Boolean,
    isShowWarning: Boolean,
    modifier: Modifier=Modifier
){
//    val borderColor = if(isShowWarning){
//        MaterialTheme.colors.error
//    }else if(isFocus){
//        MaterialTheme.colors.primary
//    }else {
//        MaterialTheme.colors.secondary
//    }

    Surface(modifier = Modifier
        .height(dimensionResource(id = R.dimen.otp_box_height_for_dialog))
        .width(dimensionResource(id = R.dimen.otp_box_width_for_dialog))
        .padding(dimensionResource(id = R.dimen.dp_4))
        .border(
            width = if (isFocus) 2.dp else 1.dp,
            color = if (isFocus) textColorDark else otpBorderColor,
            shape = MaterialTheme.shapes.small
        ))
    {
        Text(
            text = char,
            style = mediumTextStyle,
            color = blueDark,
            modifier = Modifier
                .wrapContentSize(align = Alignment.Center)

        )

    }
}