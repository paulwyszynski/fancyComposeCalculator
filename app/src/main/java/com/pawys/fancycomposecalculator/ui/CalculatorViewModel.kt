package com.pawys.fancycomposecalculator.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pawys.fancycomposecalculator.data.repository.SavedHistoryRepository
import com.pawys.fancycomposecalculator.model.Action
import com.pawys.fancycomposecalculator.model.SavedHistoryItem
import com.pawys.fancycomposecalculator.utils.CalculationUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CalculatorUiState(
    val outputString: String = "",
    val savedHistoryItems: List<SavedHistoryItem> = emptyList(),
)

// TODO: Fix something like this: 13.+5
// TODO: Fix dispatcher for testing: Use DI for dispatcher injection here. Check https://developer.android.com/kotlin/coroutines/test#setting-main-dispatcher
@HiltViewModel
class CalculatorViewModel
    @Inject
    constructor(
        private val calculationUtil: CalculationUtil,
        private val repository: SavedHistoryRepository,
    ) : ViewModel() {
        private var lastAction: Action? = null
        private val validMathematicalOperationMatcher =
            Regex("(\\(-?\\d+(\\.\\d+)?\\)|\\d+(\\.\\d+)?)[+\\-*/](\\(-?\\d+(\\.\\d+)?\\)|\\d+(\\.\\d+)?)+")
        private val lastNumberMatcherPositive = Regex("\\d+\\.?\\d*$")
        private val lastNumberMatcherNegative = Regex("\\(-\\d+(\\.\\d+)?\\)")
        private var firstAppStart = true

        private val _uiState = MutableStateFlow(CalculatorUiState())
        val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

        fun collectHistoryCalculations() {
            viewModelScope.launch {
                repository
                    .read()
                    .collect { savedHistoryItems ->
                        if (savedHistoryItems.isEmpty()) {
                            _uiState.update { it.copy(savedHistoryItems = savedHistoryItems) }
                        } else {
                            val historyItem = savedHistoryItems.last()
                            _uiState.update {
                                val result =
                                    if (firstAppStart) {
                                        ""
                                    } else {
                                        historyItem.result
                                    }
                            /*
                            INFO: Use .toList() here to create a new list instance otherwise the UI is not updated.
                             DataStore passes the same instance of the list to the UI.
                             */
                                it.copy(outputString = result, savedHistoryItems = savedHistoryItems.toList())
                            }
                        }
                    }
            }
        }

        fun onCalculatorAction(action: Action) {
            when (action) {
                is Action.Value -> addValue(action)
                is Action.Operation -> addOperation(action)
                is Action.FloatPoint -> addFloatingPoint(action)
                is Action.Invert -> invert()
                is Action.Calculation -> calculate()
                is Action.Clear -> clear()
                is Action.Remove -> remove()
            }
        }

        fun onHistoryItemClick(savedHistoryItem: SavedHistoryItem) {
            _uiState.update { it.copy(outputString = savedHistoryItem.result) }
        }

        fun onHistoryDeleteClick() {
            viewModelScope.launch {
                repository.delete()
            }
        }

        private fun addValue(value: Action.Value) {
            _uiState.update { it.copy(outputString = _uiState.value.outputString + value.number) }
            lastAction = value
        }

        private fun addOperation(operation: Action.Operation) {
            val outputString = removeAnyTrailingDecimalPoint(_uiState.value.outputString)
            if (lastAction !is Action.Operation) {
                _uiState.update { it.copy(outputString = outputString + operation.operand.symbol) }
                lastAction = operation
            }
        }

        private fun addFloatingPoint(action: Action.FloatPoint) {
            if (lastAction !is Action.Operation && lastAction !is Action.Invert) {
                val isFloatingPointValue = isFloatingValue(_uiState.value.outputString)

                if (!isFloatingPointValue && lastAction !is Action.FloatPoint) {
                    _uiState.update { it.copy(outputString = _uiState.value.outputString + action.symbol) }
                    lastAction = action
                }
            }
        }

        private fun isFloatingValue(outputString: String): Boolean {
            val lastNumberMatch =
                lastNumberMatcherPositive.find(outputString) ?: lastNumberMatcherNegative.find(outputString)
            val isFloatingValue =
                lastNumberMatch?.let {
                    val lastNumberIndex = it.range.first
                    val lastNumber = outputString.substring(lastNumberIndex)
                    lastNumber.contains(".")
                } ?: false
            return isFloatingValue
        }

        private fun removeAnyTrailingDecimalPoint(outputString: String): String =
            if (outputString.last() == '.') {
                outputString.dropLast(1)
            } else {
                outputString
            }

        private fun invert() {
            val outputString = removeAnyTrailingDecimalPoint(_uiState.value.outputString)
            val lastNumberMatch =
                lastNumberMatcherPositive.findAll(outputString).lastOrNull()
                    ?: lastNumberMatcherNegative.findAll(outputString).lastOrNull()
            lastNumberMatch?.let { matchResult ->
                val lastNumberIndex = matchResult.range.first
                val lastNumber = outputString.substring(lastNumberIndex)
                val newInputString =
                    if (lastNumber.startsWith("(-") && lastNumber.endsWith(")")) {
                        // If the number is already negative, remove the parentheses and minus sign
                        outputString.substring(0, lastNumberIndex) + lastNumber.substring(2, lastNumber.length - 1)
                    } else {
                        // If the number is positive, add parentheses and minus sign
                        outputString.substring(0, lastNumberIndex) + "(-" + lastNumber + ")"
                    }
                _uiState.update { it.copy(outputString = newInputString) }
                lastAction = Action.Invert
            }
        }

        private fun calculate() {
            firstAppStart = false
            viewModelScope.launch {
                var outputString = _uiState.value.outputString
                if (!isOutputStringValid(outputString)) return@launch

                outputString = removeAnyTrailingOperators(outputString)
                val result = calculationUtil.calculate(outputString)
                saveToRepository(outputString, result)
                lastAction = Action.Calculation
            }
        }

        private fun isOutputStringValid(outputString: String): Boolean = validMathematicalOperationMatcher.containsMatchIn(outputString)

        private fun removeAnyTrailingOperators(outputString: String): String {
            var newOutputString = outputString
            while (listOf("+", "-", "*", "/", ".").contains(newOutputString.last().toString())) {
                newOutputString = newOutputString.dropLast(1)
            }
            return newOutputString
        }

        private suspend fun saveToRepository(
            outputString: String,
            result: String,
        ) {
            repository.save(SavedHistoryItem(outputString, result))
        }

        private fun remove() {
            _uiState.update { it.copy(outputString = _uiState.value.outputString.dropLast(1)) }
            lastAction = Action.Remove
        }

        private fun clear() {
            _uiState.update { it.copy(outputString = "") }
            lastAction = Action.Clear
        }
    }
