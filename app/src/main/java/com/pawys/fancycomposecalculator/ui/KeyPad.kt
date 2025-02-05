package com.pawys.fancycomposecalculator.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pawys.fancycomposecalculator.model.Action
import com.pawys.fancycomposecalculator.model.Operand
import com.pawys.fancycomposecalculator.ui.theme.FancyComposeCalculatorTheme
import com.pawys.fancycomposecalculator.ui.theme.OperationContainerColor

@Composable
fun KeyPad(
    modifier: Modifier = Modifier,
    onClick: (Action) -> Unit = {},
) {
    Column(modifier = modifier.padding(4.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Key(modifier = modifier.weight(1f), symbol = "AC", containerColor = OperationContainerColor) {
                onClick(Action.Clear)
            }
            Key(modifier = modifier.weight(1f), symbol = "DEL", containerColor = OperationContainerColor) {
                onClick(Action.Remove)
            }
            Key(
                modifier = modifier.weight(1f),
                symbol = "±",
                containerColor = OperationContainerColor,
            ) {
                onClick(Action.Invert)
            }
            Key(modifier = modifier.weight(1f), symbol = "/", containerColor = OperationContainerColor) {
                onClick(Action.Operation(Operand.Divide()))
            }
        }
        Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Key(modifier = modifier.weight(1f), symbol = "7") { onClick(Action.Value("7")) }
            Key(modifier = modifier.weight(1f), symbol = "8") { onClick(Action.Value("8")) }
            Key(modifier = modifier.weight(1f), symbol = "9") { onClick(Action.Value("9")) }
            Key(modifier = modifier.weight(1f), symbol = "*", containerColor = OperationContainerColor) {
                onClick(Action.Operation(Operand.Multiply()))
            }
        }
        Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Key(modifier = modifier.weight(1f), symbol = "4") {
                onClick(Action.Value("4"))
            }
            Key(modifier = modifier.weight(1f), symbol = "5") {
                onClick(Action.Value("5"))
            }
            Key(modifier = modifier.weight(1f), symbol = "6") {
                onClick(Action.Value("6"))
            }
            Key(modifier = modifier.weight(1f), symbol = "-", containerColor = OperationContainerColor) {
                onClick(Action.Operation(Operand.Minus()))
            }
        }
        Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Key(modifier = modifier.weight(1f), symbol = "1") {
                onClick(Action.Value("1"))
            }
            Key(modifier = modifier.weight(1f), symbol = "2") {
                onClick(Action.Value("2"))
            }
            Key(modifier = modifier.weight(1f), symbol = "3") {
                onClick(Action.Value("3"))
            }
            Key(modifier = modifier.weight(1f), symbol = "+", containerColor = OperationContainerColor) {
                onClick(Action.Operation(Operand.Plus()))
            }
        }
        Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Key(modifier = modifier.weight(2f), symbol = "0") {
                onClick(Action.Value("0"))
            }
            Key(modifier = modifier.weight(1f), symbol = ".") {
                onClick(Action.FloatPoint())
            }
            Key(modifier = modifier.weight(1f), symbol = "=", containerColor = OperationContainerColor) {
                onClick(Action.Calculation)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun KeyPadPreviewLight() {
    FancyComposeCalculatorTheme {
        KeyPad()
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun KeyPadPreviewDark() {
    FancyComposeCalculatorTheme {
        KeyPad()
    }
}
