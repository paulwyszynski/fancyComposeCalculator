@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.pawys.fancycomposecalculator

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pawys.fancycomposecalculator.model.Action
import com.pawys.fancycomposecalculator.model.SavedHistoryItem
import com.pawys.fancycomposecalculator.ui.Calculator
import com.pawys.fancycomposecalculator.ui.CalculatorUiState
import com.pawys.fancycomposecalculator.ui.CalculatorViewModel
import com.pawys.fancycomposecalculator.ui.theme.FancyComposeCalculatorTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: CalculatorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewModel.collectHistoryCalculations()
        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            FancyCalculator(
                uiState = uiState,
                onCalculatorAction = { viewModel.onCalculatorAction(it) },
                onHistoryItemClick = { viewModel.onHistoryItemClick(it) },
                onHistoryDeleteClick = { viewModel.onHistoryDeleteClick() },
            )
        }
    }
}

@Composable
fun FancyCalculator(
    uiState: CalculatorUiState,
    onCalculatorAction: (Action) -> Unit,
    onHistoryItemClick: (SavedHistoryItem) -> Unit,
    onHistoryDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showAlertDialog by remember { mutableStateOf(false) }
    FancyComposeCalculatorTheme {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    colors =
                        TopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.secondary,
                            navigationIconContentColor = MaterialTheme.colorScheme.primary,
                            actionIconContentColor = MaterialTheme.colorScheme.primary,
                            scrolledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        ),
                    title = { Text(stringResource(R.string.app_name)) },
                    actions = {
                        IconButton(onClick = { showAlertDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                tint = MaterialTheme.colorScheme.secondary,
                                contentDescription = stringResource(id = R.string.cd_history_wipe),
                            )
                        }
                    },
                )
            },
        ) { innerPadding ->
            Calculator(
                modifier = Modifier.padding(innerPadding),
                output = uiState.outputString,
                savedHistoryItems = uiState.savedHistoryItems,
                onclick = { onCalculatorAction(it) },
                onHistoryItemClick = { onHistoryItemClick(it) },
            )
        }
        if (showAlertDialog) {
            CalculatorAlertDialog(
                onDismissClick = { showAlertDialog = false },
                onConfirmClick = {
                    showAlertDialog = false
                    onHistoryDeleteClick()
                },
                dialogTitle = stringResource(id = R.string.alert_dialog_history_title),
                dialogText = stringResource(id = R.string.alert_dialog_history_text),
            )
        }
    }
}

@Composable
fun CalculatorAlertDialog(
    onDismissClick: () -> Unit,
    onConfirmClick: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    modifier: Modifier = Modifier,
) {
    BasicAlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissClick,
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
            ) {
                Text(
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = dialogTitle,
                )
                Spacer(modifier = Modifier.padding(8.dp))
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = dialogText,
                )
                Spacer(modifier = Modifier.padding(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Spacer(modifier = Modifier.weight(0.5f))
                    TextButton(modifier = Modifier.weight(1f), onClick = onDismissClick) {
                        Text(text = stringResource(id = R.string.alert_dialog_button_cancel))
                    }
                    TextButton(modifier = Modifier.weight(1f), onClick = onConfirmClick) {
                        Text(stringResource(id = R.string.alert_dialog_button_confirm))
                    }
                    Spacer(modifier = Modifier.weight(0.5f))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AlertDialogPreviewLight() {
    FancyComposeCalculatorTheme {
        CalculatorAlertDialog(
            onDismissClick = { },
            onConfirmClick = { },
            dialogTitle = stringResource(id = R.string.alert_dialog_history_title),
            dialogText = stringResource(id = R.string.alert_dialog_history_text),
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun AlertDialogPreviewDark() {
    FancyComposeCalculatorTheme {
        CalculatorAlertDialog(
            onDismissClick = { },
            onConfirmClick = { },
            dialogTitle = stringResource(id = R.string.alert_dialog_history_title),
            dialogText = stringResource(id = R.string.alert_dialog_history_text),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FancyCalculatorPreviewLight() {
    FancyCalculator(
        modifier = Modifier,
        uiState = CalculatorUiState(),
        onCalculatorAction = { },
        onHistoryItemClick = { },
        onHistoryDeleteClick = { },
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun FancyCalculatorPreviewDark() {
    FancyCalculator(
        modifier = Modifier,
        uiState = CalculatorUiState(),
        onCalculatorAction = { },
        onHistoryItemClick = { },
        onHistoryDeleteClick = { },
    )
}
