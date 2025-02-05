package com.pawys.fancycomposecalculator.data.repository

import kotlinx.coroutines.flow.Flow

interface LocalDataSource<T> {
    suspend fun save(item: T)

    suspend fun read(): Flow<List<T>>

    suspend fun delete()
}
