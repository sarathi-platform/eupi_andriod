package com.sarathi.surveymanager.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nudge.core.BLANK_STRING
import com.nudge.core.DELIMITER_TIME
import com.nudge.core.DELIMITER_YEAR
import com.nudge.core.HOURS
import com.nudge.core.MINUTE
import com.nudge.core.MONTHS
import com.nudge.core.YEAR
import com.sarathi.dataloadingmangement.model.survey.response.ValuesDto
import com.sarathi.dataloadingmangement.util.constants.QuestionType
import com.sarathi.surveymanager.R

@Composable
fun HrsMinRangePickerComponent(
    isMandatory: Boolean = false,
    isEditAllowed: Boolean = true,
    title: String? = BLANK_STRING,
    defaultValue: String = BLANK_STRING,
    typePicker: String,
    onAnswerSelection: (selectValue: String, selectedValueId: Int) -> Unit,
) {
    val firstInputValue = remember {
        mutableStateOf(getFirstValue(typePicker, defaultValue))
    }
    val secondInputValue = remember {
        mutableStateOf(getSecondValue(typePicker, defaultValue))
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp)
    ) {
        if (title != null) {
            if (title.isNotBlank()) {
                QuestionComponent(
                    title = title,
                    isRequiredField = isMandatory
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f)) {

                InputComponent(
                    questionIndex = 0,
                    isOnlyNumber = true,
                    isMandatory = false,
                    maxLength = 3,
                    title = getFirstTitle(typePicker),
                    isEditable = isEditAllowed,
                ) { selectedValue, remainingAmout ->
                    firstInputValue.value = selectedValue
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
                    title = getSecondTitle(typePicker),
                    hintText = secondInputValue.value,
                    sources = getSources(typePicker),
                ) { selectedValue ->
                    secondInputValue.value =
                        getSources(typePicker).find { it.id == selectedValue.id }?.value
                            ?: BLANK_STRING/* selectedValue*/
                    onAnswerSelection(
                        getPickerValue(
                            typePicker = typePicker,
                            firstValue = firstInputValue.value,
                            secondValue = getSources(typePicker).find { it.id == selectedValue.id }?.value
                                ?: BLANK_STRING
                        ),
                        selectedValue.id
                    )

                }
            }
        }
    }
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
    if ((firstValue.isBlank() && secondValue.isBlank()) || ((firstValue.equals("00") && secondValue.equals(
            "00"
        ))) || ((firstValue.equals("0") && secondValue.equals(
            "0"
        )))
    ) {
        return ""
    }
    if (typePicker == QuestionType.HrsMinPicker.name) {
        return if (firstValue.isBlank() || firstValue.equals("00") || firstValue.equals("0")) "${secondValue} ${MINUTE}" else if (secondValue.isBlank() || secondValue.equals(
                "00"
            ) || secondValue.equals("0")
        ) "${firstValue} ${HOURS}" else "${firstValue} ${HOURS} ${secondValue} ${MINUTE}"
    } else if (typePicker == QuestionType.YrsMonthPicker.name) {
        return if (firstValue.isBlank() || firstValue.equals("00") || firstValue.equals("0")) "${secondValue} ${MONTHS}" else if (secondValue.isBlank() || secondValue.equals(
                "00"
            ) || secondValue.equals("0")
        ) "${firstValue} ${YEAR}" else "${firstValue} ${YEAR} ${secondValue} ${MONTHS}"
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
        firstValue = numbers.getOrElse(0) { "" } // Fetches the first number (5)
    } else {
        val value = defaultValue.split(" ")
        if (value.first().isBlank()) {
            firstValue = ""
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
        secondValue = numbers.getOrElse(0) { "" } // Fetches the first number (5)
    } else {
        secondValue = numbers.getOrElse(1) { "" } // Fetches the first number (5)
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
        return ""
    } else if (typePicker == QuestionType.YrsMonthPicker.name) {
        return ""
    }
    return BLANK_STRING
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewTimePickerComponent() {
    HrsMinRangePickerComponent(
        defaultValue = ":15",
        typePicker = "HrsMinPicker",
        onAnswerSelection = { selectValue, selectedValueId ->

        })
}