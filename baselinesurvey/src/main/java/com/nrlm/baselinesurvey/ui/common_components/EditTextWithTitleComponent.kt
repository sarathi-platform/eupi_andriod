package com.nrlm.baselinesurvey.ui.common_components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.MAXIMUM_RANGE_LENGTH
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.ui.question_type_screen.presentation.component.OptionItemEntityState
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.ui.theme.borderGrey
import com.nrlm.baselinesurvey.ui.theme.defaultTextStyle
import com.nrlm.baselinesurvey.ui.theme.dimen_18_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_8_dp
import com.nrlm.baselinesurvey.ui.theme.placeholderGrey
import com.nrlm.baselinesurvey.ui.theme.textColorDark
import com.nrlm.baselinesurvey.utils.onlyNumberField

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditTextWithTitleComponent(
    title: String? = "select",
    defaultValue: String = BLANK_STRING,
    showQuestion: OptionItemEntityState? = OptionItemEntityState.getEmptyStateObject(),
    isOnlyNumber: Boolean = false,
    maxLength: Int = 150,
    isContent: Boolean = false,
    resetResponse: Boolean = false,
    onInfoButtonClicked: () -> Unit,
    onAnswerSelection: (selectValue: String) -> Unit,
) {
    val txt = remember(resetResponse, showQuestion?.optionId) {
        mutableStateOf(defaultValue)
    }
    Log.d(
        "TAG",
        "EditTextWithTitleComponent in component: ${showQuestion?.optionItemEntity?.display}, type: ${showQuestion?.optionItemEntity?.optionType}, showQuestion: ${showQuestion?.showQuestion}"
    )
    Log.d(
        "TAG",
        "EditTextWithTitleComponent in component response: ${txt.value}"
    )
//    if (txt.value.isBlank()) {
//        txt.value = defaultValue
//    }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    VerticalAnimatedVisibilityComponent(visible = showQuestion?.showQuestion ?: true) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp)
        ) {
            if (title?.isNotBlank() == true) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(.9f),
                        text = title ?: "select",
                        style = defaultTextStyle,
                        color = textColorDark
                    )
                    if (isContent) {
                        Spacer(modifier = Modifier.size(dimen_8_dp))
                        Icon(
                            painter = painterResource(id = R.drawable.info_icon),
                            contentDescription = "question info button",
                            Modifier
                                .size(dimen_18_dp)
                                .clickable {
                                    onInfoButtonClicked()
                                },

                            tint = blueDark
                        )
                    }
                }
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

}

@Composable
@Preview(showBackground = true)
fun EditTextWithTitleComponentPreview() {
    EditTextWithTitleComponent(title = "select", defaultValue = "", onInfoButtonClicked = {}) {}
}