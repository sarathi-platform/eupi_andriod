package com.sarathi.surveymanager.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nudge.core.BLANK_STRING
import com.nudge.core.ui.theme.borderGrey
import com.nudge.core.ui.theme.placeholderGrey
import com.nudge.core.ui.theme.textColorDark
import com.sarathi.surveymanager.MAXIMUM_RANGE_LENGTH
import com.sarathi.surveymanager.utils.onlyNumberField

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NumberTextComponent(
    title: String? = "select",
    defaultValue: String = BLANK_STRING,
    isOnlyNumber: Boolean = false,
    maxLength: Int = 150,
    onAnswerSelection: (selectValue: String) -> Unit,
) {
    val txt = remember {
        mutableStateOf(defaultValue)
    }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp)
    ) {
        if (title?.isNotBlank() == true) {
            QuestionComponent(title = title, isRequiredField = true)
        }
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            value = txt.value,
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
                onAnswerSelection(txt.value)
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
                keyboardController?.hide()
                onAnswerSelection(txt.value)
            }),
            maxLines = 2,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = placeholderGrey,
                unfocusedBorderColor = borderGrey,
                textColor = textColorDark
            )
        )

    }
}

@Preview(showSystemUi = true)
@Composable
fun NumberTextComponentPreview() {
    NumberTextComponent(onAnswerSelection = {}, isOnlyNumber = true)
}
