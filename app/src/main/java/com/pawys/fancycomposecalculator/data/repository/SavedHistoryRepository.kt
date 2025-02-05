package com.pawys.fancycomposecalculator.data.repository

import com.pawys.fancycomposecalculator.model.SavedHistoryItem

class SavedHistoryRepository(
    private val localDataSource: LocalDataSource<SavedHistoryItem>,
) {
    suspend fun save(savedHistoryItem: SavedHistoryItem) {
        localDataSource.save(savedHistoryItem)
    }

    suspend fun read() = localDataSource.read()

    suspend fun delete() {
        localDataSource.delete()
    }
}
