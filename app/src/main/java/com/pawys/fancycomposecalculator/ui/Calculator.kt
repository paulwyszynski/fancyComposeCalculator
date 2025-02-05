package com.pawys.fancycomposecalculator.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.pawys.fancycomposecalculator.model.Action
import com.pawys.fancycomposecalculator.model.SavedHistoryItem
import com.pawys.fancycomposecalculator.ui.theme.FancyComposeCalculatorTheme

@Composable
fun Calculator(
    modifier: Modifier = Modifier,
    output: String,
    savedHistoryItems: List<SavedHistoryItem>,
    onHistoryItemClick: (SavedHistoryItem) -> Unit,
    onclick: (Action) -> Unit,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
    ) {
        Column {
            KeyPadOutput(
                modifier = Modifier.weight(1f),
                output = output,
                savedHistoryItems = savedHistoryItems,
                onHistoryItemClick = onHistoryItemClick,
            )
            KeyPad(modifier = Modifier.weight(1f), onClick = { onclick(it) })
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CalculatorPreviewLight() {
    FancyComposeCalculatorTheme {
        Calculator(
            output = "5+5",
            savedHistoryItems =
                listOf(
                    SavedHistoryItem("15 + 5", "20"),
                    SavedHistoryItem("15 + 5", "20"),
                    SavedHistoryItem("15 + 5", "20"),
                    SavedHistoryItem("15 + 5", "20"),
                ),
            onclick = { },
            onHistoryItemClick = { },
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun CalculatorPreviewDark() {
    FancyComposeCalculatorTheme {
        Calculator(
            output = "5+5",
            savedHistoryItems =
                listOf(
                    SavedHistoryItem("15 + 5", "20"),
                    SavedHistoryItem("15 + 5", "20"),
                    SavedHistoryItem("15 + 5", "20"),
                    SavedHistoryItem("15 + 5", "20"),
                    SavedHistoryItem("15 + 5", "20"),
                ),
            onclick = { },
            onHistoryItemClick = { },
        )
    }
}
