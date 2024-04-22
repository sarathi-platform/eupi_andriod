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
import com.nrlm.baselinesurvey.ui.Constants.QuestionType
import com.nrlm.baselinesurvey.ui.question_type_screen.presentation.component.OptionItemEntityState
import com.nrlm.baselinesurvey.ui.question_type_screen.presentation.component.TypeDropDownComponent
import com.nrlm.baselinesurvey.ui.theme.NotoSans
import com.nrlm.baselinesurvey.ui.theme.red
import com.nrlm.baselinesurvey.ui.theme.textColorDark

@Composable
fun TimePickerComponent(
    isMandatory: Boolean = false,
    title: String? = BLANK_STRING,
    defaultValue: String = BLANK_STRING,
    showQuestionState: OptionItemEntityState? = OptionItemEntityState.getEmptyStateObject(),
    isContent: Boolean = false,
    typePicker: String,
    onInfoButtonClicked: () -> Unit,
    onAnswerSelection: (selectValue: String) -> Unit,
) {
    val firstInputValue = remember {
        mutableStateOf(getFirstValue(typePicker, defaultValue))
    }
    val secondInputValue = remember {
        mutableStateOf(getSecondValue(typePicker, defaultValue))
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
                        title = getFirstTitle(typePicker),
                        maxLength = 2,
                        onInfoButtonClicked = { /*TODO*/ },
                    ) { selectValue ->
                        firstInputValue.value = selectValue
                        val secondValue =
                            if (secondInputValue.value.equals(
                                    "select",
                                    true
                                )
                            ) "00" else secondInputValue.value
                        onAnswerSelection(
                            "${firstInputValue.value}${
                                getDelimiter(typePicker)
                            }${secondValue}"
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
                        title = getSecondTitle(typePicker),
                        hintText = secondInputValue.value,
                        sources = getSources(typePicker),
                        onInfoButtonClicked = {}
                    ) { selectedValue ->
                        secondInputValue.value = selectedValue
                        onAnswerSelection(
                            "${firstInputValue.value}${
                                getDelimiter(typePicker)
                            }$selectedValue"
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

fun getFirstTitle(typePicker: String): String {
    if (typePicker.equals(QuestionType.HrsMinPicker.name)) {
        return "Hours"
    } else if (typePicker.equals(QuestionType.YrsMonthPicker.name)) {
        return "Years"
    }
    return BLANK_STRING
}

fun getSecondTitle(typePicker: String): String {
    if (typePicker.equals(QuestionType.HrsMinPicker.name)) {
        return "Minute"
    } else if (typePicker.equals(QuestionType.YrsMonthPicker.name)) {
        return "Month"
    }
    return BLANK_STRING
}

fun getSources(typePicker: String): List<String> {
    if (typePicker.equals(QuestionType.HrsMinPicker.name)) {
        return getMinutes()
    } else if (typePicker.equals(QuestionType.YrsMonthPicker.name)) {
        return getMonths()
    }
    return listOf("")
}
fun getMinutes(): List<String> {
    return listOf("15", "30", "45")
}

fun getMonths(): List<String> {
    return listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11")
}

fun getDelimiter(typePicker: String): String {
    if (typePicker.equals(QuestionType.HrsMinPicker.name)) {
        return CONDITIONS_TIME
    } else if (typePicker.equals(QuestionType.YrsMonthPicker.name)) {
        return CONDITIONS_YEAR
    }
    return ""
}

fun getFirstValue(typePicker: String, defaultValue: String): String {
    if (getTypePicker(typePicker)?.equals(QuestionType.HrsMinPicker.name) == true) {
        return if (defaultValue.contains(CONDITIONS_TIME)) defaultValue.split(
            CONDITIONS_TIME,
            ignoreCase = true
        )
            .first() else "00"
    } else if (getTypePicker(typePicker)?.equals(QuestionType.YrsMonthPicker.name) == true) {
        return if (defaultValue.contains(CONDITIONS_YEAR)) defaultValue.split(
            CONDITIONS_YEAR,
            ignoreCase = true
        )
            .first() else "0000"
    }
    return BLANK_STRING
}

fun getSecondValue(typePicker: String, defaultValue: String): String {
    if (getTypePicker(typePicker)?.equals(QuestionType.HrsMinPicker.name) == true) {
        return if (defaultValue.contains(CONDITIONS_TIME)) defaultValue.split(
            CONDITIONS_TIME,
            ignoreCase = true
        )[1] else "00"
    } else if (getTypePicker(typePicker)?.equals(QuestionType.YrsMonthPicker.name) == true) {
        return if (defaultValue.contains(CONDITIONS_YEAR)) defaultValue.split(
            CONDITIONS_YEAR,
            ignoreCase = true
        )[1] else "00"
    }
    return "Select"
}

fun getTypePicker(questionType: String): String? {
    if (questionType.equals(QuestionType.HrsMinPicker.name)) {
        return QuestionType.HrsMinPicker.name
    } else if (questionType.equals(QuestionType.YrsMonthPicker.name)) {
        return QuestionType.YrsMonthPicker.name
    }
    return null
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewTimePickerComponent() {
    TimePickerComponent(
        defaultValue = "3/10",
        typePicker = "HrsMinPicker",
        showQuestionState = getEmptyStateObject(),
        onInfoButtonClicked = { /*TODO*/ }) {
    }
}