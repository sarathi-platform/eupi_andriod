package com.sarathi.dataloadingmangement.domain.use_case

import android.text.TextUtils
import com.nudge.core.CoreDispatchers
import com.nudge.core.extractSubstrings
import com.nudge.core.model.response.SurveyValidations
import com.nudge.core.model.response.Validation
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.utils.ExpressionEvaluator
import com.nudge.core.value
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.enums.ValidationExpressionEnum
import com.sarathi.dataloadingmangement.model.uiModel.OptionsUiModel
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import com.sarathi.dataloadingmangement.util.constants.QuestionType
import javax.inject.Inject

class SurveyValidationUseCase @Inject constructor(
    private val coreSharedPrefs: CoreSharedPrefs
) {

    fun validateExpressionEvaluator(
        validations: List<SurveyValidations>?,
        questionUiModel: QuestionUiModel?,
        onValidationResult: (Boolean, String) -> Unit
    ) {
        CoreDispatchers.ioCoroutineScope {

            val selectedValidations =
                validations?.filter { it.questionId == questionUiModel?.questionId }

            if (!selectedValidations.isNullOrEmpty()) {
                if (QuestionType.userInputQuestionTypeList.contains(
                        questionUiModel?.type?.value()?.toLowerCase()
                    )
                ) {
                    val selectionOption = questionUiModel?.options?.firstOrNull()
                    if (selectionOption != null) {
                        val validation =
                            selectedValidations.find { it.optionId == selectionOption.optionId }?.validation?.find { it.languageCode == coreSharedPrefs.getAppLanguage() }
                        validateExpression(
                            validation,
                            questionUiModel,
                            selectionOption,
                            onValidationResult
                        )
                    } else {
                        onValidationResult(true, com.nudge.core.BLANK_STRING)
                    }
                }
            } else {
                onValidationResult(true, com.nudge.core.BLANK_STRING)
            }
        }

    }

    private suspend fun validateExpression(
        validation: Validation?,
        questionUiModel: QuestionUiModel,
        selectionOption: OptionsUiModel,
        onValidationResult: (Boolean, String) -> Unit
    ) {

        if (validation != null) {

            val expressionResult = invoke(
                validationExpression = validation.condition,
                validationRegex = validation.regex,
                questionUiModel = questionUiModel,
                selectionOption = selectionOption,
                message = validation.message
            )
            onValidationResult(expressionResult.first, expressionResult.second)

        } else {
            onValidationResult(true, com.nudge.core.BLANK_STRING)
        }

    }


    suspend fun invoke(
        validationExpression: String?,
        validationRegex: String?,
        questionUiModel: QuestionUiModel,
        selectionOption: OptionsUiModel,
        message: String
    ): Pair<Boolean, String> {

        if (TextUtils.isEmpty(validationExpression) || TextUtils.isEmpty(message)) {
            return Pair(true, BLANK_STRING)
        }

        var map = HashMap<String, String>()
        var messageMap = HashMap<String, String>()

        val expressionArray = validationExpression?.split(" ")
        val msgExpressionArray = message.split(" ")

        ValidationExpressionEnum.values().forEach {
            if (expressionArray?.contains(it.originalValue) == true) {
                map[it.originalValue] = BLANK_STRING
            }
            if (msgExpressionArray?.contains(it.originalValue) == true) {
                messageMap[it.originalValue] = BLANK_STRING
            }
        }

        if (validationExpression?.contains("{") == true && validationExpression.contains("}") == true) {
            extractSubstrings(validationExpression).forEach {
                map[it] = BLANK_STRING
            }
        }

        if (message?.contains("{") == true && message?.contains("}") == true) {
            extractSubstrings(message).forEach {
                messageMap[it] = BLANK_STRING
            }
        }

        map.forEach {
            fetchMapValues(
                it,
                selectionOption,
                map
            )
        }

        messageMap.forEach {
            fetchMapValues(
                it,
                selectionOption,
                messageMap
            )
        }

        var completedExpression = validationExpression
        var validationMessage = message
        map.forEach {
            completedExpression = completedExpression?.replace(it.key, it.value)
        }
        messageMap.forEach {
            validationMessage = validationMessage.replace(it.key, it.value)
        }

        if (completedExpression.isNullOrEmpty())
            return Pair(true, BLANK_STRING)

        return Pair(
            ExpressionEvaluator.evaluateExpression(
                completedExpression.value(),
                selectionOption.selectedValue.value(),
                validationRegex
            ),
            validationMessage
        )
    }

    private fun fetchMapValues(
        mapEntry: Map.Entry<String, String>,
        selectionOption: OptionsUiModel,
        map: HashMap<String, String>
    ) {

        when (mapEntry.key) {
            ValidationExpressionEnum.INPUT_VALUE.originalValue -> {
                map[mapEntry.key] = selectionOption.selectedValue.value()
            }

            ValidationExpressionEnum.INPUT_LENGTH.originalValue -> {
                map[mapEntry.key] = selectionOption.selectedValue.value().length.toString()
            }
        }

    }

}