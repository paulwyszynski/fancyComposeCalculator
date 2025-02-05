package com.pawys.fancycomposecalculator.model


class SavedHistory(private val savedHistoryItems: MutableList<SavedHistoryItem> = mutableListOf()) {

    fun addItem(savedHistoryItem: SavedHistoryItem) {
        savedHistoryItems.add(savedHistoryItem)
    }

    fun getItems(): List<SavedHistoryItem> = savedHistoryItems
}