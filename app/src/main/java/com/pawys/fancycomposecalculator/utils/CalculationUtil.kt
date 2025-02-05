package com.pawys.fancycomposecalculator.utils

import java.math.RoundingMode
import javax.inject.Inject

/*TODO:
   - Add unit tests
   - Refactor the Shunting-yard algorithm for unary minus
 */
class CalculationUtil
    @Inject
    constructor() {
        private val inputStringSplitter = """\d+(\.\d+)?|[+\-*/()]|(?<=\()-\d+(\.\d+)?""".toRegex()

        private fun splitInputString(inputString: String): List<String> = inputStringSplitter.findAll(inputString).map { it.value }.toList()

        // INFO: Shunting-yard algorithm with parentheses support and unary minus
        fun calculate(inputString: String): String {
            if (inputString.isBlank()) throw IllegalArgumentException("Input string is empty")
            if (!areParenthesesBalanced(inputString)) throw IllegalArgumentException("Unbalanced parentheses in expression")

            val inputList = splitInputString(inputString)
            val outputString = mutableListOf<Any>()
            val operatorStack = mutableListOf<String>()

            for (i in inputList.indices) {
                val token = inputList[i]
                when {
                    token.toDoubleOrNull() != null -> outputString.add(token.toDouble())
                    token in listOf("+", "-", "*", "/") -> {
                        // INFO: Handle unary minus
                        if (token == "-" && (i == 0 || inputList[i - 1] in listOf("+", "-", "*", "/", "("))) {
                            outputString.add(0.0)
                        }
                        while (
                            operatorStack.isNotEmpty() &&
                            operatorStack.last() != "(" &&
                            operatorPrecedence(operatorStack.last()) >= operatorPrecedence(token)
                        ) {
                            outputString.add(operatorStack.removeAt(operatorStack.size - 1))
                        }
                        operatorStack.add(token)
                    }
                    token == "(" -> operatorStack.add(token)
                    token == ")" -> {
                        while (operatorStack.isNotEmpty() && operatorStack.last() != "(") {
                            outputString.add(operatorStack.removeAt(operatorStack.size - 1))
                        }
                        if (operatorStack.isNotEmpty() && operatorStack.last() == "(") {
                            operatorStack.removeAt(operatorStack.size - 1)
                        }
                    }
                    else -> throw IllegalArgumentException("Invalid token: $token")
                }
            }

            while (operatorStack.isNotEmpty()) {
                val op = operatorStack.removeAt(operatorStack.size - 1)
                if (op == "(" || op == ")") throw IllegalArgumentException("Mismatched parentheses")
                outputString.add(op)
            }

            val stack = mutableListOf<Double>()
            for (token in outputString) {
                when (token) {
                    is Double -> stack.add(token)
                    is String -> {
                        if (stack.size < 2) throw IllegalArgumentException("Invalid expression: insufficient operands for operator $token")
                        val b = stack.removeAt(stack.size - 1)
                        val a = stack.removeAt(stack.size - 1)
                        stack.add(applyOperator(a, b, token))
                    }
                }
            }

            return formatResult(stack.last())
        }

        private fun areParenthesesBalanced(input: String): Boolean {
            var count = 0
            for (char in input) {
                if (char == '(') count++
                if (char == ')') count--
                if (count < 0) return false // More closing than opening
            }
            return count == 0
        }

        private fun applyOperator(
            a: Double,
            b: Double,
            operator: String,
        ): Double =
            when (operator) {
                "+" -> a + b
                "-" -> a - b
                "*" -> a * b
                "/" -> a / b
                else -> throw IllegalArgumentException("Unknown operator: $operator")
            }

        private fun operatorPrecedence(op: String): Int =
            when (op) {
                "+", "-" -> 1
                "*", "/" -> 2
                else -> 0
            }

        private fun formatResult(result: Double): String =
            if (result % 1.0 != 0.0) {
                result
                    .toBigDecimal()
                    .setScale(3, RoundingMode.HALF_UP)
                    .stripTrailingZeros()
                    .toPlainString()
            } else {
                result.toInt().toString()
            }
    }
