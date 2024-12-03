package com.nudge.core.utils

import com.nudge.core.BLANK_STRING
import com.nudge.core.value
import java.util.Stack

object ExpressionEvaluator {

    private val stringCheckRegex = Regex(".*[a-zA-Z].*")

    private val logicalOperatorCheckRegex = Regex("&&|\\|\\|")

    private val expressionRegex =
        Regex("""(".*?"|\d+\s*[a-zA-Z]*.*?)\s*(!=|==)\s*(".*?"|\d+\s*[a-zA-Z]*.*?)""")

    fun evaluateExpression(expression: String): Boolean {

        return if (isStringExpression(expression))
            evaluateStringExpressions(expression)
        else
            evaluateNonStringExpressions(expression)

    }

    private fun evaluateStringExpressions(expression: String): Boolean {
        val cleanExpression = getCleanExpression(expression)

        return evaluateStringExpression(cleanExpression)
    }

    private fun evaluateNonStringExpressions(expression: String): Boolean {
        // Remove whitespace for simplicity
        val cleanExpression = getCleanExpression(expression)

        // Replace "&&" with "and" and "||" with "or" for easier handling
        val formattedExpression = cleanExpression
            .replace("&&", " and ")
            .replace("||", " or ")

        return evaluate(formattedExpression)
    }

    private fun evaluate(expression: String): Boolean {
        val values = Stack<Boolean>()
        val operators = Stack<String>()
        val parenExpression = Stack<String>()

        var i = 0
        while (i < expression.length) {
            when (val ch = expression[i]) {
                '(' -> {
                    // Start of new expression inside parentheses
                    val innerExpression = StringBuilder()
                    i++

                    // Read until the closing parenthesis
                    var openParens = 1
                    while (i < expression.length && openParens > 0) {
                        if (expression[i] == '(') openParens++
                        if (expression[i] == ')') openParens--
                        if (openParens > 0) innerExpression.append(expression[i])
                        i++
                    }
                    // Evaluate inner expression recursively
                    val innerResult = evaluate(innerExpression.toString())
                    values.push(innerResult)
                }

                't', 'f' -> {
                    // Handle true/false literals
                    val value = if (expression.startsWith("true", i)) {
                        i += 4
                        true
                    } else {
                        i += 5
                        false
                    }
                    values.push(value)
                }

                in '0'..'9' -> {
                    // Parse number and handle comparison
                    val numExpression = StringBuilder()
                    while (i < expression.length && (expression[i].isDigit() || expression[i] in listOf(
                            '>',
                            '<',
                            '=',
                            '!'
                        ))
                    ) {
                        numExpression.append(expression[i])
                        i++
                    }
                    i-- // Adjust for increment in loop
                    val result = evaluateComparison(numExpression.toString())
                    values.push(result)
                }

                'a' -> {
                    // Handle "and"
                    if (expression.startsWith("and", i)) {
                        operators.push("and")
                        i += 2 // advance to end of 'and'
                    }
                }

                'o' -> {
                    // Handle "or"
                    if (expression.startsWith("or", i)) {
                        operators.push("or")
                        i += 1 // advance to end of 'or'
                    }
                }
            }

            // After processing, check if we have two values and an operator to evaluate
            if (values.size > 1 && operators.isNotEmpty()) {
                val right = values.pop()
                val left = values.pop()
                val op = operators.pop()

                val result = if (op == "and") left && right else left || right
                values.push(result)
            }

            i++
        }

        // Final result
        return if (values.isNotEmpty()) values.pop() else false
    }

    private fun evaluateComparison(expression: String): Boolean {
        // Handle comparison operations with >=, <=, >, <, ==, !=
        return when {
            expression.contains(">=") -> {
                val (left, right) = expression.split(">=").map { it.trim().toInt() }
                left >= right
            }

            expression.contains("<=") -> {
                val (left, right) = expression.split("<=").map { it.trim().toInt() }
                left <= right
            }

            expression.contains("<") -> {
                val (left, right) = expression.split("<").map { it.trim().toInt() }
                left < right
            }

            expression.contains(">") -> {
                val (left, right) = expression.split(">").map { it.trim().toInt() }
                left > right
            }

            expression.contains("==") -> {
                val (left, right) = expression.split("==").map { it.trim().toInt() }
                left == right
            }

            expression.contains("!=") -> {
                val (left, right) = expression.split("!=").map { it.trim().toInt() }
                left != right
            }

            else -> false
        }
    }

    private fun evaluateStringExpression(expression: String): Boolean {
        if (logicalOperatorCheckRegex.containsMatchIn(expression)) {
            val logicalOperator =
                logicalOperatorCheckRegex.findAll(expression).map { it.value }.toSet().firstOrNull()
            val individualExpressions = logicalOperator?.let {
                expression.split(it)
            }
            return individualExpressions?.fold(
                evaluateComparisonStringExpression(
                    individualExpressions.firstOrNull() ?: BLANK_STRING
                )
            ) { acc: Boolean, subExpression: String ->
                when (logicalOperator) {
                    "&&" -> acc && evaluateComparisonStringExpression(subExpression)
                    "||" -> acc || evaluateComparisonStringExpression(subExpression)
                    else -> false
                }
            }.value()

        } else {

            return evaluateComparisonStringExpression(expression)
        }
    }

    private fun evaluateComparisonStringExpression(expression: String): Boolean {

        if (expression == BLANK_STRING)
            return false

        val match = expressionRegex.matchEntire(expression)
        if (match != null) {
            val (left, operator, right) = match.destructured
            val leftValue = parseValue(left)
            val rightValue = parseValue(right)
            return when (operator) {
                "!=" -> leftValue != rightValue
                "==" -> leftValue == rightValue
                else -> false
            }
        }
        return false
    }

    private fun parseValue(value: String): String {
        return value.trim().removeSurrounding("\"")
    }

    private fun isStringExpression(expression: String): Boolean {
        return stringCheckRegex.containsMatchIn(expression)
    }

    private fun getCleanExpression(expression: String): String {
        val cleanExpression = expression.replace(" ", "")
        return cleanExpression
    }

}
