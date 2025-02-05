package com.pawys.fancycomposecalculator.model

sealed class Operand(val symbol: String) {
    class Plus(symbol: String = "+") : Operand(symbol)
    class Minus(symbol: String = "-") : Operand(symbol)
    class Multiply(symbol: String = "*") : Operand(symbol)
    class Divide(symbol: String = "/") : Operand(symbol)
}