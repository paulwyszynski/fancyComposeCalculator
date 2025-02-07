package com.pawys.fancycomposecalculator.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.pawys.fancycomposecalculator.data.repository.LocalDataSource
import com.pawys.fancycomposecalculator.data.repository.SavedHistoryDataSource
import com.pawys.fancycomposecalculator.data.repository.SavedHistoryRepository
import com.pawys.fancycomposecalculator.model.SavedHistoryItem
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SavedHistoryModule {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

//    @Provides
//    @Singleton
//    fun provideSavedHistory(): SavedHistory {
//        return SavedHistory()
//    }

    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = context.dataStore

    @Provides
    fun provideSavedHistoryRepository(localDataSource: LocalDataSource<SavedHistoryItem>): SavedHistoryRepository =
        SavedHistoryRepository(localDataSource)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {
    @Binds
    abstract fun bindHistoryDataSource(dataSource: SavedHistoryDataSource): LocalDataSource<SavedHistoryItem>
}
