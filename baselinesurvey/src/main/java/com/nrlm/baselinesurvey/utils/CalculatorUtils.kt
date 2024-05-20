package com.nrlm.baselinesurvey.utils

import java.util.Stack


object CalculatorUtils {

    private fun applyOperator(operatorX: Char, a: Float, b: Float): Float {
        when (operatorX) {
            '+' -> return b + a
            '-' -> return b - a
            '*' -> return b * a
            '/' -> if (a != 0f) return b / a
        }
        return 0F
    }


    private fun checkPrecedence(operatorA: Char, operatorB: Char): Boolean {
        if (operatorB == '(' || operatorB == ')') {
            return false
        }
        return if ((operatorA == '+' || operatorA == '-') && (operatorB == '*' || operatorB == '/')) {
            false
        } else {
            true
        }
    }

    fun calculate(mS: String): Float {
        val myChar = mS.toCharArray()
        val myNumStack = Stack<Float>()
        val operatorStack = Stack<Char>()
        var i = 0
        while (i < myChar.size) {
            if (myChar[i] in '0'..'9'
            ) {
                val stringBuffer = StringBuffer()
                while (i < myChar.size && myChar[i] >= '0' && myChar[i] <= '9') {
                    stringBuffer.append(myChar[i++])
                }
                myNumStack.push(stringBuffer.toString().toFloat())
                i--
            } else if (myChar[i] == '(') operatorStack.push(myChar[i]) else if (myChar[i] == ')') {
                while (operatorStack.peek() != '(') {
                    myNumStack.push(
                        applyOperator(
                            operatorStack.pop(),
                            myNumStack.pop(),
                            myNumStack.pop()
                        )
                    )
                }
                operatorStack.pop()
            } else if (myChar[i] == '/' || myChar[i] == '*' || myChar[i] == '+' || myChar[i] == '-') {
                while (!operatorStack.empty() && checkPrecedence(myChar[i], operatorStack.peek())) {
                    myNumStack.push(
                        applyOperator(
                            operatorStack.pop(),
                            myNumStack.pop(),
                            myNumStack.pop()
                        )
                    )
                }
                operatorStack.push(myChar[i])
            }
            i++
        }
        while (!operatorStack.empty()) {
            try {
                myNumStack.push(applyOperator(operatorStack.pop(), myNumStack.pop(),myNumStack.pop()))
            }catch (ex:Exception){
                return myNumStack.pop()
            }
        }
        return myNumStack.pop()
    }

}