package com.pawys.fancycomposecalculator.data

import com.pawys.fancycomposecalculator.model.SavedHistoryItem
import kotlinx.serialization.json.Json

object SavedHistorySerializer {
    fun serialize(savedHistoryItems: MutableList<SavedHistoryItem>): String = Json.encodeToString(savedHistoryItems)

    fun deserialize(jsonString: String): MutableList<SavedHistoryItem> = Json.decodeFromString(jsonString)
}
