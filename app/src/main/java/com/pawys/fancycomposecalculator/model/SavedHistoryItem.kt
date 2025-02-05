package com.pawys.fancycomposecalculator.model

import kotlinx.serialization.Serializable

@Serializable
data class SavedHistoryItem(
    val outputString: String = "",
    val result: String = "",
)
