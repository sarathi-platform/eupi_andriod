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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.DELIMITER_TIME
import com.nrlm.baselinesurvey.DELIMITER_YEAR
import com.nrlm.baselinesurvey.HOURS
import com.nrlm.baselinesurvey.MINUTE
import com.nrlm.baselinesurvey.MONTHS
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.YEAR
import com.nrlm.baselinesurvey.model.datamodel.ValuesDto
import com.nrlm.baselinesurvey.ui.Constants.QuestionType
import com.nrlm.baselinesurvey.ui.question_type_screen.presentation.component.OptionItemEntityState
import com.nrlm.baselinesurvey.ui.question_type_screen.presentation.component.TypeDropDownComponent
import com.nrlm.baselinesurvey.ui.theme.NotoSans
import com.nrlm.baselinesurvey.ui.theme.red
import com.nrlm.baselinesurvey.ui.theme.textColorDark

@Composable
fun RangePickerComponent(
    isMandatory: Boolean = false,
    title: String? = BLANK_STRING,
    defaultValue: String = BLANK_STRING,
    showQuestionState: OptionItemEntityState? = OptionItemEntityState.getEmptyStateObject(),
    isContent: Boolean = false,
    typePicker: String,
    onInfoButtonClicked: () -> Unit,
    onAnswerSelection: (selectValue: String, selectedValueId: Int) -> Unit,
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
                            ) getSecondDefaultValue(typePicker) else secondInputValue.value
                        onAnswerSelection(
                            getPickerValue(
                                typePicker = typePicker,
                                firstValue = firstInputValue.value,
                                secondValue = secondValue
                            ),
                            0
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
                        secondInputValue.value =
                            getSources(typePicker).find { it.id == selectedValue }?.value
                                ?: BLANK_STRING/* selectedValue*/
                        onAnswerSelection(
                            getPickerValue(
                                typePicker = typePicker,
                                firstValue = firstInputValue.value,
                                secondValue = getSources(typePicker).find { it.id == selectedValue }?.value
                                    ?: BLANK_STRING
                            ),
                            selectedValue
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

@Composable
fun getFirstTitle(typePicker: String): String {
    if (typePicker == QuestionType.HrsMinPicker.name) {
        return stringResource(R.string.hours)
    } else if (typePicker == QuestionType.YrsMonthPicker.name) {
        return stringResource(R.string.years)
    }
    return BLANK_STRING
}

@Composable
fun getSecondTitle(typePicker: String): String {
    if (typePicker == QuestionType.HrsMinPicker.name) {
        return stringResource(R.string.minute)
    } else if (typePicker == QuestionType.YrsMonthPicker.name) {
        return stringResource(R.string.month)
    }
    return BLANK_STRING
}

fun getSources(typePicker: String): List<ValuesDto> {
    if (typePicker == QuestionType.HrsMinPicker.name) {
        return getMinutes()
    } else if (typePicker == QuestionType.YrsMonthPicker.name) {
        return getMonths()
    }
    return listOf()
}

fun getMinutes(): List<ValuesDto> {
    return listOf(
        ValuesDto(id = 0, "00"),
        ValuesDto(id = 1, "15"),
        ValuesDto(id = 2, "30"),
        ValuesDto(id = 3, "45")
    )
}

fun getMonths(): List<ValuesDto> {

    val list = ArrayList<ValuesDto>()
    (0..11).toList().forEach {
        list.add(ValuesDto(it, it.toString()))
    }

    return list
}

fun getDelimiter(typePicker: String): String {
    if (typePicker == QuestionType.HrsMinPicker.name) {
        return DELIMITER_TIME
    } else if (typePicker == QuestionType.YrsMonthPicker.name) {
        return DELIMITER_YEAR
    }
    return ""
}

fun getPickerValue(typePicker: String, firstValue: String, secondValue: String): String {
    if (typePicker == QuestionType.HrsMinPicker.name) {
        return "${firstValue} Hours ${secondValue} Minutes"
    } else if (typePicker == QuestionType.YrsMonthPicker.name) {
        return "${firstValue} Year ${secondValue} Months"
    }
    return BLANK_STRING
}

fun isFirstValueContain(value: String): Boolean {
    return value.contains(YEAR, true) || value.contains(DELIMITER_YEAR, true) || value.contains(
        HOURS,
        true
    ) || value.contains(
        DELIMITER_TIME, true
    )
}

fun isOnlySecondValueContain(value: String): Boolean {
    return (value.contains(MONTHS, true) && !value.contains(YEAR, true)) || (value.contains(
        MINUTE,
        true
    ) && !value.contains(HOURS, true))
}

fun getFirstValue(typePicker: String, defaultValue: String): String {
    var firstValue = BLANK_STRING
    val delimiter =
        if (getTypePicker(typePicker)?.equals(QuestionType.HrsMinPicker.name) == true) DELIMITER_TIME else DELIMITER_YEAR
    if (defaultValue.contains(delimiter)) {
        return defaultValue.split(
            delimiter,
            ignoreCase = true
        )
            .first()
    }
    if (isFirstValueContain(defaultValue)) {
        val regex = Regex("\\d+")
        val numbers = regex.findAll(defaultValue)
            .map { it.value }
            .toList()
        firstValue = numbers.getOrElse(0) { "00" } // Fetches the first number (5)
    } else {
        val value = defaultValue.split(" ")
        if (value.first().isBlank()) {
            firstValue = "00"
        }
    }
    return firstValue
}


fun getSecondValue(typePicker: String, defaultValue: String): String {
    var secondValue = BLANK_STRING
    val delimiter =
        if (getTypePicker(typePicker)?.equals(QuestionType.HrsMinPicker.name) == true) DELIMITER_TIME else DELIMITER_YEAR
    if (defaultValue.contains(delimiter)) {
        return defaultValue.split(
            delimiter,
            ignoreCase = true
        )[1]
    }
    val regex = Regex("\\d+")
    val numbers = regex.findAll(defaultValue)
        .map { it.value }
        .toList()
    if (isOnlySecondValueContain(defaultValue)) {
        secondValue = numbers.getOrElse(0) { "00" } // Fetches the first number (5)
    } else {
        secondValue = numbers.getOrElse(1) { "00" } // Fetches the first number (5)
    }
    return secondValue
}

fun getTypePicker(questionType: String): String? {
    if (questionType == QuestionType.HrsMinPicker.name) {
        return QuestionType.HrsMinPicker.name
    } else if (questionType == QuestionType.YrsMonthPicker.name) {
        return QuestionType.YrsMonthPicker.name
    }
    return null
}

fun getSecondDefaultValue(typePicker: String): String {
    if (typePicker == QuestionType.HrsMinPicker.name) {
        return "00"
    } else if (typePicker == QuestionType.YrsMonthPicker.name) {
        return "00"
    }
    return BLANK_STRING
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewTimePickerComponent() {
    RangePickerComponent(
        defaultValue = ":15",
        typePicker = "HrsMinPicker",
        showQuestionState = getEmptyStateObject(),
        onInfoButtonClicked = { /*TODO*/ }) { value, id ->
    }
}