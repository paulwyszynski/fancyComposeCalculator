package com.pawys.fancycomposecalculator.extensions

fun MutableList<*>.removeLastEntry() {
    this.removeAt(this.lastIndex)
}
