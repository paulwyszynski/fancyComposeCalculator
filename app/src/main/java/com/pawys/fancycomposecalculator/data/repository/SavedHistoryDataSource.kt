package com.pawys.fancycomposecalculator.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.pawys.fancycomposecalculator.data.SavedHistorySerializer
import com.pawys.fancycomposecalculator.model.SavedHistoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private object PreferencesKeys {
    val SAVED_HISTORY = stringPreferencesKey("saved_history")
}

class SavedHistoryDataSource
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
    ) : LocalDataSource<SavedHistoryItem> {
        private var savedHistoryItems: MutableList<SavedHistoryItem> = mutableListOf()

        override suspend fun save(item: SavedHistoryItem) {
            savedHistoryItems.add(item)
            dataStore.edit { settings ->
                val serialized = SavedHistorySerializer.serialize(savedHistoryItems)
                settings[PreferencesKeys.SAVED_HISTORY] = serialized
            }
        }

        override suspend fun read(): Flow<List<SavedHistoryItem>> =
            dataStore.data
                .catch { exception ->
                    if (exception is IOException) {
                        emit(emptyPreferences())
                    } else {
                        throw exception
                    }
                }.map { settings ->
                    settings[PreferencesKeys.SAVED_HISTORY]?.let {
                        savedHistoryItems = SavedHistorySerializer.deserialize(it)
                        savedHistoryItems
                    } ?: emptyList()
                }

        override suspend fun delete() {
            dataStore.edit { settings ->
                savedHistoryItems.clear()
                settings.remove(PreferencesKeys.SAVED_HISTORY)
            }
        }
    }
