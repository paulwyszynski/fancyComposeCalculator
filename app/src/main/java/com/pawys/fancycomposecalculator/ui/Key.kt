package com.pawys.fancycomposecalculator.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pawys.fancycomposecalculator.ui.theme.FancyComposeCalculatorTheme

@Composable
fun Key(modifier: Modifier = Modifier, symbol: String, containerColor: Color? = null, onClick: () -> Unit) {
    ElevatedButton(
        modifier = modifier
            .width(100.dp)
            .height(100.dp),
        onClick = onClick,
        colors = containerColor?.let { ButtonDefaults.buttonColors().copy(containerColor = it) }
            ?: ButtonDefaults.elevatedButtonColors()
    ) {
        Text(
            modifier = modifier,
            textAlign = TextAlign.Center,
            text = symbol,
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Preview(showBackground = true, widthDp = 100, heightDp = 100)
@Composable
fun KeyPreviewLight() {
    FancyComposeCalculatorTheme {
        Key(symbol = "9") { }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true, widthDp = 100, heightDp = 100)
@Composable
fun KeyPreviewDark() {
    FancyComposeCalculatorTheme {
        Key(symbol = "DEL") { }
    }
}