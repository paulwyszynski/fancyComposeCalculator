package com.pawys.fancycomposecalculator.model

sealed class Action {
    data class Value(var number: String): Action()
    data class Operation(val operand: Operand): Action()
    data class FloatPoint(val symbol: String = ".") : Action()
    data object Calculation : Action()
    data object Invert : Action()
    data object Remove : Action()
    data object Clear : Action()
}