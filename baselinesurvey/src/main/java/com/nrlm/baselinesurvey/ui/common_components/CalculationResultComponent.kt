package com.nrlm.baselinesurvey.ui.common_components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.MAXIMUM_RANGE_LENGTH
import com.nrlm.baselinesurvey.ui.question_type_screen.presentation.component.OptionItemEntityState
import com.nrlm.baselinesurvey.ui.theme.NotoSans
import com.nrlm.baselinesurvey.ui.theme.borderGrey
import com.nrlm.baselinesurvey.ui.theme.borderGreyLight
import com.nrlm.baselinesurvey.ui.theme.defaultTextStyle
import com.nrlm.baselinesurvey.ui.theme.textColorDark
import com.nrlm.baselinesurvey.utils.onlyNumberField

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CalculationResultComponent(
    title: String? = "Select",
    defaultValue: String = BLANK_STRING,
    showQuestion: OptionItemEntityState? = OptionItemEntityState.getEmptyStateObject(),
    isOnlyNumber: Boolean = true,
    maxLength: Int = 150,
) {

    val txt =mutableStateOf(defaultValue)
    if (txt.value.isBlank()) {
        txt.value = defaultValue
    }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    VerticalAnimatedVisibilityComponent(visible = showQuestion?.showQuestion ?: true) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp)
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