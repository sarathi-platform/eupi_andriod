package com.sarathi.surveymanager.ui.component

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nudge.core.ui.commonUi.BasicCardView
import com.nudge.core.ui.theme.NotoSans
import com.nudge.core.ui.theme.borderGrey
import com.nudge.core.ui.theme.borderGreyLight
import com.nudge.core.ui.theme.defaultCardElevation
import com.nudge.core.ui.theme.defaultTextStyle
import com.nudge.core.ui.theme.dimen_0_dp
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_2_dp
import com.nudge.core.ui.theme.roundedCornerRadiusDefault
import com.nudge.core.ui.theme.textColorDark
import com.nudge.core.ui.theme.white
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.surveymanager.constants.MAXIMUM_RANGE_LENGTH
import com.sarathi.surveymanager.utils.onlyNumberField


@SuppressLint("UnrememberedMutableState", "SuspiciousIndentation")
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CalculationResultComponent(
    title: String? = "Select",
    defaultValue: String = BLANK_STRING,
    isOnlyNumber: Boolean = true,
    maxLength: Int = 150,
    showCardView: Boolean = true
) {

    val txt =mutableStateOf(defaultValue)
    if (txt.value.isBlank()) {
        txt.value = defaultValue
    }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    BasicCardView(
        cardElevation = CardDefaults.cardElevation(
            defaultElevation = if (showCardView) defaultCardElevation else dimen_0_dp
        ),
        cardShape = RoundedCornerShape(roundedCornerRadiusDefault),
        modifier = Modifier
            .fillMaxWidth()
            .background(white)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = if (showCardView) dimen_16_dp else dimen_2_dp,
                    vertical = if (showCardView) dimen_10_dp else dimen_0_dp
                )
        ) {
            if (title?.isNotBlank() == true) {
                Text(
                    text = title ?: "select",
                    style = defaultTextStyle,
                    color = textColorDark
                )
            }
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                value = txt.value,
                readOnly = true,
                onValueChange = {
                    if (it.length <= maxLength) {
                        if (isOnlyNumber) {
                            if (onlyNumberField(it)) {
                                if (it.length <= MAXIMUM_RANGE_LENGTH) {
                                    txt.value = it
                                }
                            }
                        } else {
                            txt.value = it
                        }
                    }
                },
                textStyle = TextStyle(
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                ),
                keyboardOptions = if (isOnlyNumber) {
                    KeyboardOptions(
                        imeAction = ImeAction.Done,
                        capitalization = KeyboardCapitalization.None,
                        autoCorrect = true,
                        keyboardType = KeyboardType.Number,
                    )
                } else {
                    KeyboardOptions(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Ascii
                    )
                },
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                }),
                singleLine = true,
                maxLines = 1,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = borderGrey,
                    unfocusedBorderColor = borderGrey,
                    textColor = textColorDark,
                    backgroundColor = borderGreyLight
                )
            )


        }
    }
}


@Preview(showBackground = true)
@Composable
fun CalculationResultComponentPreview() {
    CalculationResultComponent(

    )
}