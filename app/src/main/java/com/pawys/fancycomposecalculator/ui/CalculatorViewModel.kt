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
            lastAction = action
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
        }

        private fun addOperation(operation: Action.Operation) {
            if (lastAction !is Action.Operation) {
                _uiState.update { it.copy(outputString = _uiState.value.outputString + operation.operand.symbol) }
            }
        }

        private fun addFloatingPoint(action: Action.FloatPoint) {
            val isFloatingPointValue: () -> Boolean = {
                val inputString = _uiState.value.outputString
                val lastNumberMatch =
                    lastNumberMatcherPositive.find(inputString) ?: lastNumberMatcherNegative.find(inputString)
                val isFloatingValue =
                    lastNumberMatch?.let {
                        val lastNumberIndex = it.range.first
                        val lastNumber = inputString.substring(lastNumberIndex)
                        lastNumber.contains(".")
                    } ?: false
                isFloatingValue
            }

            if (!isFloatingPointValue() && lastAction !is Action.FloatPoint) {
                _uiState.update { it.copy(outputString = _uiState.value.outputString + action.symbol) }
            }
        }

        private fun invert() {
            val inputString = _uiState.value.outputString
            val lastNumberMatch = lastNumberMatcherPositive.find(inputString) ?: lastNumberMatcherNegative.find(inputString)
            lastNumberMatch?.let { matchResult ->
                val lastNumberIndex = matchResult.range.first
                val lastNumber = inputString.substring(lastNumberIndex)
                val newInputString =
                    if (lastNumber.startsWith("(-") && lastNumber.endsWith(")")) {
                        // If the number is already negative, remove the parentheses and minus sign
                        inputString.substring(0, lastNumberIndex) + lastNumber.substring(2, lastNumber.length - 1)
                    } else {
                        // If the number is positive, add parentheses and minus sign
                        inputString.substring(0, lastNumberIndex) + "(-" + lastNumber + ")"
                    }
                _uiState.update { it.copy(outputString = newInputString) }
            }
        }

        private fun calculate() {
            firstAppStart = false
            viewModelScope.launch {
                var outputString = _uiState.value.outputString
                if (!validMathematicalOperationMatcher.containsMatchIn(outputString)) return@launch

                while (listOf("+", "-", "*", "/", ".").contains(outputString.last().toString())) {
                    outputString = outputString.dropLast(1)
                }
                val result = calculationUtil.calculate(outputString)
                repository.save(SavedHistoryItem(outputString, result))
            }
        }

        private fun remove() {
            _uiState.update { it.copy(outputString = _uiState.value.outputString.dropLast(1)) }
        }

        private fun clear() {
            _uiState.update { it.copy(outputString = "") }
        }
    }
