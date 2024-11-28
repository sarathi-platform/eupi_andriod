package com.sarathi.surveymanager.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.nudge.core.BLANK_STRING
import com.nudge.core.getQuestionNumber
import com.nudge.core.ui.commonUi.BasicCardView
import com.nudge.core.ui.commonUi.CustomVerticalSpacer
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.borderGrey
import com.nudge.core.ui.theme.defaultCardElevation
import com.nudge.core.ui.theme.dimen_0_dp
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_56_dp
import com.nudge.core.ui.theme.dimen_6_dp
import com.nudge.core.ui.theme.newMediumTextStyle
import com.nudge.core.ui.theme.placeholderGrey
import com.nudge.core.ui.theme.red
import com.nudge.core.ui.theme.roundedCornerRadiusDefault
import com.nudge.core.ui.theme.smallTextStyle
import com.nudge.core.ui.theme.smallTextStyleMediumWeight
import com.nudge.core.ui.theme.white
import com.sarathi.dataloadingmangement.model.survey.response.ContentList
import com.sarathi.surveymanager.R
import com.sarathi.surveymanager.constants.MAXIMUM_RANGE_LENGTH
import com.sarathi.surveymanager.utils.onlyNumberField

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun InputComponent(
    contests: List<ContentList?>? = listOf(),
    title: String? = "select",
    defaultValue: String = BLANK_STRING,
    questionIndex: Int,
    isOnlyNumber: Boolean = false,
    maxLength: Int = 150,
    hintText: String = BLANK_STRING,
    isMandatory: Boolean = true,
    isEditable: Boolean = true,
    sanctionedAmount: Int = 0,
    remainingAmount: Int = 0,
    isZeroNotAllowed: Boolean = false,
    showCardView: Boolean = false,
    isFromTypeQuestion: Boolean = false,
    onDetailIconClicked: () -> Unit = {}, // Default empty lambda
    navigateToMediaPlayerScreen: (ContentList) -> Unit,
    onAnswerSelection: (selectValue: String, remainingAmount: Int) -> Unit,
) {
    val txt = remember {
        mutableStateOf(defaultValue)
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
                    horizontal = if (showCardView) dimen_16_dp else dimen_0_dp,
                    vertical = if (showCardView) dimen_10_dp else dimen_0_dp
                )
        ) {
            if (title?.isNotBlank() == true) {
                QuestionComponent(
                    isFromTypeQuestionInfoIconVisible = isFromTypeQuestion && contests?.isNotEmpty() == true,
                    title = title,
                    questionNumber = if (showCardView) getQuestionNumber(questionIndex) else BLANK_STRING,
                    isRequiredField = isMandatory,
                    onDetailIconClicked = {
                        onDetailIconClicked()
                    }
                )
            }
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimen_56_dp),
                value = txt.value,
                textStyle = newMediumTextStyle.copy(blueDark),
                enabled = isEditable,
                onValueChange = { value ->
                    if (value.isEmpty()) {
                        // Allow clearing the field
                        txt.value = value
                    } else if (value.length <= maxLength) {
                        if (isOnlyNumber) {
                            if (onlyNumberField(value) && value.length <= MAXIMUM_RANGE_LENGTH) {
                                if (isZeroNotAllowed) {
                                    if (!value.all { it == '0' }) {
                                        txt.value = value
                                    }
                                } else {
                                    txt.value = value
                                }
                            }
                        } else {
                            txt.value = value
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
                    keyboardController?.hide()
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
        if (showCardView && contests?.isNotEmpty() == true) {
            CustomVerticalSpacer(size = dimen_6_dp)
            ContentBottomViewComponent(
                contents = contests,
                questionIndex = questionIndex,
                showCardView = showCardView,
                questionDetailExpanded = {},
                navigateToMediaPlayerScreen = { contentList ->
                    navigateToMediaPlayerScreen(contentList)
                }
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
    }, isOnlyNumber = true, questionIndex = 0, navigateToMediaPlayerScreen = {})
}
