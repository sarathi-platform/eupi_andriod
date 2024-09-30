package com.sarathi.surveymanager.utils.conditions

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.nudge.core.BLANK_STRING
import com.nudge.core.DEFAULT_OPERAND_FOR_EXPRESSION_VALUE
import com.nudge.core.OPERAND_DELIMITER
import com.nudge.core.ifNotEmpty
import com.nudge.core.utils.CoreLogger
import com.nudge.core.value
import com.sarathi.dataloadingmangement.model.survey.response.Conditions
import com.sarathi.dataloadingmangement.model.uiModel.ConditionsUiModel
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import com.sarathi.dataloadingmangement.util.constants.QuestionType
import com.sarathi.surveymanager.utils.onlyNumberField

class ConditionsUtils private constructor() {

    private val LOGGING_TAG = ConditionsUtils::class.java.simpleName

    private val listOfOperators = listOf("==", "<>", ">", "<", "=", "<=", ">=", "><")

    private var sourceTargetMap: Map<Int, List<Int>> = mapOf()

    private var questionConditionMap: Map<Int, List<Conditions>> = mapOf()

    private var responseMap: HashMap<Int, List<Int>> = hashMapOf()

    private var conditionsUiModelList: List<ConditionsUiModel> = listOf()

    private var questionUiModel: List<QuestionUiModel> = listOf()

    val questionVisibilityMap: SnapshotStateMap<Int, Boolean> = mutableStateMapOf()

    companion object {
        private var INSTANCE: ConditionsUtils? = null

        fun getInstance(): ConditionsUtils {

            INSTANCE = ConditionsUtils()

            return INSTANCE!!

        }
    }

    private fun setConditionsUiModelList(conditionsUiModelList: List<ConditionsUiModel>) {
        this.conditionsUiModelList = conditionsUiModelList
    }

    private fun setSourceTargetMap(conditionsUiModelList: List<ConditionsUiModel>) {
        val map: HashMap<Int, List<Int>> = hashMapOf()
        conditionsUiModelList.groupBy { it.sourceQuestionId }.forEach {
            map[it.key] = it.value.map { it.targetQuestionId }
        }

        sourceTargetMap = map
    }

    private fun setQuestionConditionMap(conditionsUiModelList: List<ConditionsUiModel>) {
        val map: HashMap<Int, List<Conditions>> = hashMapOf()

        conditionsUiModelList.groupBy { it.targetQuestionId }.forEach { mapEntry ->

            map[mapEntry.key] = mapEntry.value.map { it ->
                Conditions(
                    expression = it.condition,
                    it.sourceQuestionId
                )
            }

        }

        questionConditionMap = map
    }

    private fun setResponseMap(questionUiModelList: List<QuestionUiModel>) {
        val map: HashMap<Int, List<Int>> = hashMapOf()
        questionUiModelList
            .filter {
                it.options?.any { opt ->
                    opt.isSelected == true
                            || (opt.selectedValue != BLANK_STRING && opt.selectedValue != null)
                }.value()
            }
            .forEach {
                if (it.type != QuestionType.TextField.name && it.type != QuestionType.InputNumber.name && it.type != QuestionType.NumericField.name) {
                    it.options?.filter { option -> option.isSelected.value() }
                        ?.map { opt -> opt.optionId!! }?.ifNotEmpty { optionIds ->
                            map[it.questionId] = optionIds
                        }
                } else if (it.type != QuestionType.InputNumber.name || it.type != QuestionType.NumericField.name) {
                    it.options?.filter { option -> option.isSelected.value() }
                        ?.map { opt -> opt.selectedValue?.toInt()!! }?.ifNotEmpty { optionIds ->
                            map[it.questionId] = optionIds
                        }

                }

            }
        responseMap = map
    }

    private fun setQuestionUiModelList(questionUiModel: List<QuestionUiModel>) {
        this.questionUiModel = questionUiModel
    }

    fun init(
        questionUiModel: List<QuestionUiModel>,
        sourceTargetQuestionMapping: List<ConditionsUiModel>
    ) {
        setSourceTargetMap(sourceTargetQuestionMapping)
        setQuestionConditionMap(sourceTargetQuestionMapping)
        setConditionsUiModelList(sourceTargetQuestionMapping)
        setResponseMap(questionUiModel)
        setQuestionUiModelList(questionUiModel)
    }

    fun initQuestionVisibilityMap(questionUiModel: List<QuestionUiModel>) {
        questionUiModel.forEach {
            questionVisibilityMap.put(it.questionId, !it.isConditional)
            if (it.options?.any { optionsUiModel -> optionsUiModel.isSelected == true } == true) {
                questionVisibilityMap.put(it.questionId, true)
            }
        }
    }

    fun updateQuestionResponseMap(question: QuestionUiModel) {
        when (question.type) {
            QuestionType.InputNumber.name,
            QuestionType.NumericField.name -> {
                question.options?.firstOrNull()?.let { opt ->
                    if (runValidResponseCheck(opt.selectedValue))
                        updateResponseMap(
                            question.questionId,
                            listOf(opt.selectedValue?.toInt().value())
                        )
                    else
                        updateResponseMap(question.questionId, listOf())
                } ?: {
                    CoreLogger.d(
                        tag = LOGGING_TAG,
                        msg = "updateQuestionResponseMap: question.options?.firstOrNull() is null -> ${question.options}"
                    )
                }
            }

            QuestionType.SingleSelectDropDown.name,
            QuestionType.ToggleGrid.name,
            QuestionType.RadioButton.name,
            QuestionType.Toggle.name,
            QuestionType.DropDown.name,
            QuestionType.MultiSelect.name,
            QuestionType.Grid.name,
            QuestionType.MultiSelectDropDown.name,
            -> {
                val isSelectedOptions = question.options?.filter { it.isSelected == true }
                isSelectedOptions?.map { it.optionId!! }?.let {
                    updateResponseMap(question.questionId, it)
                } ?: {
                    CoreLogger.d(
                        tag = LOGGING_TAG,
                        msg = "updateQuestionResponseMap: isSelectedOptions List is null -> ${isSelectedOptions}"
                    )
                }
            }
        }
    }

    private fun updateResponseMap(questionId: Int, responseList: List<Int>) {
        responseMap[questionId] = responseList
    }

    fun runConditionCheck(sourceQuestion: QuestionUiModel): Map<Int, Boolean> {

        return evaluateConditions(sourceQuestion)

    }

    private fun evaluateConditions(sourceQuestion: QuestionUiModel): Map<Int, Boolean> {

        val questionsToShow = mutableMapOf<Int, Boolean>()

        val sourceQuestionType = sourceQuestion.type

        val targetQuestionsIdList = sourceTargetMap[sourceQuestion.questionId]

        if (targetQuestionsIdList.isNullOrEmpty())
            return questionsToShow

        for (targetQuestionId in targetQuestionsIdList) {

            val condition = questionConditionMap.findConditionForQuestion(targetQuestionId)

            val response = responseMap[sourceQuestion.questionId]

            if (condition.isNullOrEmpty())
                break

            if (condition.size == 1) {
                questionVisibilityMap.put(
                    targetQuestionId,
                    evaluateSingleCondition(
                        sourceQuestion = sourceQuestion,
                        response = response,
                        conditions = condition.first(),
                        sourceQuestionType = sourceQuestionType
                    )
                )

                val targetQuestionUiModel =
                    questionUiModel.find { it.questionId == targetQuestionId } ?: continue

                evaluateConditions(targetQuestionUiModel)

            }

            if (condition.size > 1) {

                val targetQuestionUiModel =
                    questionUiModel.find { it.questionId == targetQuestionId } ?: continue

                val conditionOperator =
                    conditionsUiModelList.find { it.targetQuestionId == targetQuestionId }?.conditionOperator
                        ?: continue

                questionVisibilityMap.put(
                    targetQuestionId,
                    evaluateMultipleCondition(
                        sourceQuestion = sourceQuestion,
                        response = response,
                        conditions = condition,
                        conditionOperator = conditionOperator,
                        sourceQuestionType = sourceQuestionType
                    )
                )

                evaluateConditions(targetQuestionUiModel)

            }


        }

        questionsToShow.putAll(questionVisibilityMap)
        return questionsToShow

    }

    private fun evaluateSingleCondition(
        sourceQuestion: QuestionUiModel,
        response: List<Int>?,
        conditions: Conditions,
        sourceQuestionType: String
    ): Boolean {

        return when (sourceQuestionType) {

            QuestionType.InputNumber.name,
            QuestionType.NumericField.name,
            QuestionType.SingleSelectDropDown.name,
            QuestionType.ToggleGrid.name,
            QuestionType.RadioButton.name,
            QuestionType.Toggle.name,
            QuestionType.DropDown.name,
            -> {
                evaluateSingleResponseConditions(response, conditions)
            }

            QuestionType.MultiSelect.name,
            QuestionType.Grid.name,
            QuestionType.MultiSelectDropDown.name,
            -> {
                /**
                 * Handle MultiselectConditions
                 */
                evaluateMultipleResponseConditions(response, conditions)
            }

            else -> {
                false
            }
        }


    }

    private fun evaluateMultipleCondition(
        sourceQuestion: QuestionUiModel,
        response: List<Int>?,
        conditions: List<Conditions>,
        conditionOperator: String?,
        sourceQuestionType: String
    ): Boolean {

        if (conditionOperator == null)
            return false

        return conditions.fold(
            evaluateSingleCondition(
                sourceQuestion,
                response,
                conditions = conditions.first(),
                sourceQuestionType
            )
        ) { acc: Boolean, condition: Conditions ->
            when (Operator.checkStringOperator(conditionOperator)) {
                Operator.AND -> {
                    acc && evaluateSingleCondition(
                        sourceQuestion,
                        response,
                        conditions = condition,
                        sourceQuestionType
                    )
                }

                Operator.OR -> {
                    acc || evaluateSingleCondition(
                        sourceQuestion,
                        response,
                        conditions = condition,
                        sourceQuestionType
                    )
                }

                else -> {
                    false
                }
            }
        }
    }

    private fun evaluateSingleResponseConditions(
        response: List<Int>?,
        conditions: Conditions
    ): Boolean {

        if (response.isNullOrEmpty())
            return false

        return evaluateExpression(response.first(), conditions.expression)

    }

    private fun evaluateMultipleResponseConditions(
        responses: List<Int>?,
        conditions: Conditions
    ): Boolean {

        if (responses.isNullOrEmpty())
            return false

        var result = false
        for (response in responses) {

            if (evaluateExpression(response, conditions.expression)) {
                result = true
                break
            }

        }

        return result
    }


    /**
     * Create and evaluate condition expression.
     */
    private fun evaluateExpression(response: Int, expression: String?): Boolean {

        val operator = getOperatorForExpression(expression)

        val operandVariables = getOperandVariables(expression, operator)

        val operands = getOperandsForExpression(operandVariables, response)

        return evaluate(operator, operands)
    }

    /**
     * Get the operator for the condition expression. Only single operator expressions are allowed.
     * For eg: If the expression is "@? < 6", then this method will return "<".
     */
    private fun getOperatorForExpression(expression: String?): String {
        var operator = ""
        for (op in listOfOperators.sortedByDescending { it.length }) {
            if (expression?.contains(op) == true) {
                operator = op
                break
            }
        }
        return operator
    }

    /**
     * Get the operand variables from the condition expression.
     *
     * For eg: if the expression is "@? < 6", then this method will return a [List] of string with values "@?" and "6".
     */
    private fun getOperandVariables(
        expression: String?,
        operator: String
    ) = expression?.filter { !it.isWhitespace() }?.split(operator)


    /**
     * Get the actual operands for the variables returned by the [getOperandVariables] method.
     * It will replace the [OPERAND_DELIMITER] with the actual response value as [Pair]
     * where the first value will be the response and the second will be the expected value for the condition to satisfy.
     */
    private fun getOperandsForExpression(
        operandVariables: List<String>?,
        response: Int
    ): Pair<Int, Int> {
        var operands: Pair<Int, Int>
        var first = DEFAULT_OPERAND_FOR_EXPRESSION_VALUE
        var second = DEFAULT_OPERAND_FOR_EXPRESSION_VALUE
        operandVariables?.forEach { opVar ->

            if (opVar.equals(OPERAND_DELIMITER)) {
                first = response
            } else {
                second = opVar.toInt()
            }
        }
        operands = Pair(first, second)
        return operands
    }

    /**
     * Evaluates the expression with the operators provided by [getOperatorForExpression] method
     * and the operands with value provided by [getOperandsForExpression] method.
     */
    private fun evaluate(operator: String, operands: Pair<Int, Int>): Boolean {
        return when (Operator.checkStringOperator(operator)) {
            Operator.EQUAL_TO -> {
                operands.first == operands.second
            }

            Operator.NOT_EQUAL_TO -> {
                operands.first != operands.second
            }

            Operator.LESS_THAN -> {
                operands.first < operands.second
            }

            Operator.MORE_THAN -> {
                operands.first > operands.second
            }

            else -> false
        }
    }

    /**
     * Check if the response is valid for an Numeric Input Type questions.
     */
    private fun runValidResponseCheck(response: String?): Boolean {
        var isResponseValid = true
        if ((response == null || response == BLANK_STRING) && !onlyNumberField(response.value()))
            isResponseValid = false

        return isResponseValid

    }

}

enum class Operator {
    EQUAL_TO,
    LESS_THAN,
    MORE_THAN,
    IN_BETWEEN,
    NOT_EQUAL_TO,
    NO_OPERATOR,
    AND,
    OR;

    companion object {

        fun checkStringOperator(operator: String) = when (operator) {
            "==",
            "=" -> EQUAL_TO

            "<" -> LESS_THAN
            ">" -> MORE_THAN
            "><" -> IN_BETWEEN
            "<>" -> NOT_EQUAL_TO
            "&&" -> AND
            "||" -> OR
            else -> NO_OPERATOR
        }

    }
}

private fun Map<Int, List<Conditions>>.findConditionForQuestion(targetQuestionId: Int): List<Conditions>? {
    return this[targetQuestionId]
}
