package com.pawys.fancycomposecalculator.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pawys.fancycomposecalculator.model.SavedHistoryItem
import com.pawys.fancycomposecalculator.ui.theme.FancyComposeCalculatorTheme

@Composable
fun KeyPadOutput(
    modifier: Modifier = Modifier,
    output: String,
    savedHistoryItems: List<SavedHistoryItem> = emptyList(),
    onHistoryItemClick: (SavedHistoryItem) -> Unit,
) {
    Column(
        modifier = modifier.background(MaterialTheme.colorScheme.background),
    ) {
        HistoryItemList(
            modifier = modifier,
            savedHistoryItems = savedHistoryItems,
            onHistoryItemClick = onHistoryItemClick,
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = output,
            textAlign = TextAlign.End,
            style = TextStyle(lineHeight = 40.sp),
            fontSize = MaterialTheme.typography.headlineLarge.fontSize,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
fun HistoryItemList(
    modifier: Modifier = Modifier,
    savedHistoryItems: List<SavedHistoryItem>,
    onHistoryItemClick: (SavedHistoryItem) -> Unit,
) {
    val listState = rememberLazyListState()
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        state = listState,
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End,
    ) {
        items(
            items = savedHistoryItems,
        ) { historyItem ->
            HistoryItemRow(historyItem = historyItem) {
                onHistoryItemClick(it)
            }
        }
    }
    LaunchedEffect(savedHistoryItems.size) {
        val lastIndex = if (savedHistoryItems.isEmpty()) 0 else savedHistoryItems.lastIndex
        listState.scrollToItem(lastIndex)
    }
}

@Composable
fun HistoryItemRow(
    modifier: Modifier = Modifier,
    historyItem: SavedHistoryItem,
    onHistoryItemClick: (SavedHistoryItem) -> Unit,
) {
    Box(
        modifier =
            modifier
                .padding(2.dp)
                .border(2.dp, MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier =
                Modifier
                    .padding(8.dp)
                    .clickable {
                        onHistoryItemClick(historyItem)
                    },
        ) {
            Text(
                textAlign = TextAlign.End,
                maxLines = 5,
                text = "${historyItem.outputString} = ${historyItem.result}",
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun KeyPadOutputLight() {
    FancyComposeCalculatorTheme {
        KeyPadOutput(
            output = "15+5+4",
            savedHistoryItems =
                listOf(
                    SavedHistoryItem("15+5+4", "24"),
                    SavedHistoryItem("15+5+4", "24"),
                ),
        ) { /* no-op */ }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun KeyPadOutputDark() {
    FancyComposeCalculatorTheme {
        KeyPadOutput(
            output = "15+5+4",
            savedHistoryItems =
                listOf(
                    SavedHistoryItem("15+5+4", "20"),
                    SavedHistoryItem("15+5+4", "20"),
                ),
        ) {
            // no-op
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HistoryItemRowLight() {
    FancyComposeCalculatorTheme {
        HistoryItemRow(historyItem = SavedHistoryItem("15 + 5", "20")) { }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun HistoryItemRowDark() {
    FancyComposeCalculatorTheme {
        HistoryItemRow(historyItem = SavedHistoryItem("15 + 5", "20")) { }
    }
}
