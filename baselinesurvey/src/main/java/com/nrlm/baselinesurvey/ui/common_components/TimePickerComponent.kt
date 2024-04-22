package com.nrlm.baselinesurvey.ui.common_components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.CONDITIONS_TIME
import com.nrlm.baselinesurvey.CONDITIONS_YEAR
import com.nrlm.baselinesurvey.ui.question_type_screen.presentation.component.OptionItemEntityState
import com.nrlm.baselinesurvey.ui.question_type_screen.presentation.component.TypeDropDownComponent
import com.nrlm.baselinesurvey.ui.theme.NotoSans
import com.nrlm.baselinesurvey.ui.theme.red
import com.nrlm.baselinesurvey.ui.theme.textColorDark

@Composable
fun TimePickerComponent(
    isMandatory: Boolean = false,
    title: String? = BLANK_STRING,
    title_1: String? = "Hour",
    title_2: String? = "Minute",
    defaultValue: String = BLANK_STRING,
    showQuestionState: OptionItemEntityState? = OptionItemEntityState.getEmptyStateObject(),
    isContent: Boolean = false,
    onInfoButtonClicked: () -> Unit,
    isHrsMinutes: Boolean = false,
    isYrMonths: Boolean = false,
    onAnswerSelection: (selectValue: String) -> Unit,
) {
    val firstInputValue = remember {
        mutableStateOf(getFirstValue(isHrsMinutes, isHrsMinutes, defaultValue))
    }
    val inputValue_2 = remember {
        mutableStateOf(getSecondValue(isHrsMinutes, isHrsMinutes, defaultValue))
    }
    VerticalAnimatedVisibilityComponent(visible = showQuestionState?.showQuestion ?: true) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp)
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontFamily = NotoSans,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = textColorDark
                        )
                    ) {
                        append(title)
                    }
                    withStyle(
                        style = SpanStyle(
                            color = red,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = NotoSans
                        )
                    ) {
                        if (isMandatory) {
                            append(" *")
                        }
                    }
                },
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    EditTextWithTitleComponent(
                        defaultValue = firstInputValue.value,
                        isOnlyNumber = true,
                        showQuestion = getEmptyStateObject(),
                        title = title_1,
                        maxLength = 2,
                        onInfoButtonClicked = { /*TODO*/ },
                    ) { selectValue ->
                        firstInputValue.value = selectValue
                        val secandValue =
                            if (inputValue_2.value.equals(
                                    "select",
                                    true
                                )
                            ) "00" else inputValue_2.value
                        onAnswerSelection(
                            "${firstInputValue.value}${
                                getDelimiter(
                                    isHrsMinutes,
                                    isYrMonths
                                )
                            }${secandValue}"
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 10.dp)
                ) {
                    TypeDropDownComponent(
                        showQuestionState = getEmptyStateObject(),
                        title = title_2,
                        hintText = inputValue_2.value,
                        sources = if (isHrsMinutes) getMinutes() else getMonths(),
                        onInfoButtonClicked = {}
                    ) { selectedvalue ->
                        inputValue_2.value = selectedvalue
                        onAnswerSelection(
                            "${firstInputValue.value}${
                                getDelimiter(
                                    isHrsMinutes,
                                    isYrMonths
                                )
                            }$selectedvalue"
                        )

                    }
                }
            }
        }
    }
}

fun getEmptyStateObject(): OptionItemEntityState {
    return OptionItemEntityState(
        optionItemEntity = null,
        showQuestion = true
    )
}

fun getMinutes(): List<String> {
    return listOf("15", "30", "45")
}

fun getMonths(): List<String> {
    return listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11")
}

fun getDelimiter(isHrsMinutes: Boolean, isYrMonths: Boolean): String {
    return if (isHrsMinutes) CONDITIONS_TIME else CONDITIONS_YEAR
}

fun getFirstValue(isHrsMinutes: Boolean, isYrMonths: Boolean, defaultValue: String): String {
    if (isHrsMinutes) {
        return if (defaultValue.contains(CONDITIONS_TIME)) defaultValue.split(
            CONDITIONS_TIME,
            ignoreCase = true
        )
            .first() else "00"
    } else if (isYrMonths) {
        return if (defaultValue.contains(CONDITIONS_YEAR)) defaultValue.split(
            CONDITIONS_YEAR,
            ignoreCase = true
        )
            .first() else "0000"
    }
    return BLANK_STRING
}

fun getSecondValue(isHrsMinutes: Boolean, isYrMonths: Boolean, defaultValue: String): String {
    if (isHrsMinutes) {
        return if (defaultValue.contains(CONDITIONS_TIME)) defaultValue.split(
            CONDITIONS_TIME,
            ignoreCase = true
        )
            .first() else "00"
    } else if (isYrMonths) {
        return if (defaultValue.contains(CONDITIONS_YEAR)) defaultValue.split(
            CONDITIONS_YEAR,
            ignoreCase = true
        )
            .first() else "00"
    }
    return "Select"
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewTimePickerComponent() {
    TimePickerComponent(
        defaultValue = "4:50",
        isHrsMinutes = true,
        showQuestionState = getEmptyStateObject(),
        onInfoButtonClicked = { /*TODO*/ }) {
    }
}