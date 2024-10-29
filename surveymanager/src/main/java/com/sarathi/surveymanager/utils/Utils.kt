package com.sarathi.surveymanager.utils

import androidx.core.text.isDigitsOnly
import com.nudge.core.BLANK_STRING
import com.nudge.core.DEFAULT_NUMERIC_INPUT_MAX_LENGTH
import com.nudge.core.DEFAULT_TEXT_INPUT_MAX_LENGTH
import com.nudge.core.model.response.SurveyValidations
import com.nudge.core.toSafeInt
import com.sarathi.dataloadingmangement.enums.ValidationExpressionEnum
import com.sarathi.dataloadingmangement.util.constants.QuestionType

fun onlyNumberField(value: String): Boolean {
    if (value.isDigitsOnly() && value != "_" && value != "N") {
        return true
    }
    return false
}

fun onlyNumberField(value: String, excludeBlankSpace: Boolean = false): Boolean {
    var result = false
    if (excludeBlankSpace) {
        result = value.isDigitsOnly() && value != "_" && value != "N" && value != BLANK_STRING
    } else {
        result = onlyNumberField(value)
    }
    return result
}

fun getMaxInputLength(
    questionId: Int,
    sectionId: Int,
    type: String,
    validations: List<SurveyValidations>
): Int {

    var defaultLength =
        if (QuestionType.numericUseInputQuestionTypeList.contains(type.toLowerCase())) DEFAULT_NUMERIC_INPUT_MAX_LENGTH else DEFAULT_TEXT_INPUT_MAX_LENGTH
    var length = defaultLength

    if (validations.isNotEmpty()) {
        validations.filter { it.sectionId == sectionId && it.questionId == questionId }
            ?.let { filterList ->
                if (filterList.isNotEmpty()) {
                    for (filterItem in filterList) {
                        val lengthValidation = filterItem.validation.find {
                            it.condition.contains(
                                ValidationExpressionEnum.INPUT_LENGTH.originalValue, true
                            )
                        }
                        length = lengthValidation?.field.toSafeInt(defaultLength.toString())
                        break
                    }
                }
            }
    }

    return length

}