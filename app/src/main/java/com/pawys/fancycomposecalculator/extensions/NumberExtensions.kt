package com.pawys.fancycomposecalculator.extensions

fun Number.isInteger(): Boolean {
    return this is Int
}

fun Number.isDouble(): Boolean {
    return this is Double
}

fun Number.convertToConcreteNumber() : Number {
    return if (this.isInteger()) this.toInt()
    else this.toDouble()
}

fun Double.covertToString(): String =
    if (this.isInteger()) this.toInt().toString()
    else this.toString()

fun Double.isInteger (): Boolean = this % 1 == 0.0
