package com.nudge.core.ui.commonUi.componet_.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nudge.core.BLANK_STRING
import com.nudge.core.MAXIMUM_RANGE_LENGTH
import com.nudge.core.R
import com.nudge.core.onlyNumberField
import com.nudge.core.ui.commonUi.QuestionComponent
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.borderGrey
import com.nudge.core.ui.theme.dimen_60_dp
import com.nudge.core.ui.theme.newMediumTextStyle
import com.nudge.core.ui.theme.placeholderGrey
import com.nudge.core.ui.theme.red
import com.nudge.core.ui.theme.smallTextStyle
import com.nudge.core.ui.theme.smallTextStyleMediumWeight

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun InputComponent(
    title: String? = "select",
    defaultValue: String = BLANK_STRING,
    isOnlyNumber: Boolean = false,
    maxLength: Int = 150,
    hintText: String = BLANK_STRING,
    isMandatory: Boolean = true,
    isEditable: Boolean = true,
    sanctionedAmount: Int = 0,
    remainingAmount: Int = 0,
    isError: Boolean = false,
    onAnswerSelection: (selectValue: String, remainingAmount: Int) -> Unit,
) {
    val txt = remember(defaultValue) {
        mutableStateOf(defaultValue)
    }

    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp)
    ) {
        if (title?.isNotBlank() == true) {
            QuestionComponent(title = title, isRequiredField = isMandatory)
        }
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimen_60_dp),
            value = txt.value,
            textStyle = newMediumTextStyle.copy(blueDark),
            isError = isError,
            enabled = isEditable,
            onValueChange = {
                if (it.length <= maxLength) {
                    if (isOnlyNumber) {
                        if (onlyNumberField(it) && it.length <= MAXIMUM_RANGE_LENGTH) {
                            txt.value = it
                        }
                    } else {
                        txt.value = it
                    }
                }
                onAnswerSelection(txt.value, remainingAmount)
            },
            placeholder = {
                androidx.compose.material3.Text(
                    text = hintText,
                    style = smallTextStyle.copy(
                        color = placeholderGrey
                    ),
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentHeight(align = Alignment.CenterVertically)
                )
            },
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
                onAnswerSelection(txt.value, remainingAmount)
            }),
            maxLines = 2,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = placeholderGrey,
                unfocusedBorderColor = borderGrey,
                textColor = blueDark
            ),
        )
        if (sanctionedAmount != 0) {
            Text(
                stringResource(
                    R.string.amount_limit,
                    getRemainingValue(remainingAmount, sanctionedAmount, defaultValue)
                ),
                style = smallTextStyleMediumWeight,
                color = red
            )
        }

    }
}

private fun getRemainingValue(remainValue: Int, sanctionedAmount: Int, existValue: String): Int {
    val value = if (existValue.isNotBlank()) existValue.toInt() else 0
    return sanctionedAmount - (value + remainValue)
}

@Preview(showSystemUi = true)
@Composable
fun NumberTextComponentPreview() {
    InputComponent(onAnswerSelection = { _, _ ->
    }, isOnlyNumber = true)
}
